package com.example.demo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.demo.authInterface.AuthenticationStrategy;
import com.example.demo.authPolymorphism.BasicAuthStrategy;
import com.example.demo.dto.EstadoCrediticioDTO;
import com.example.demo.entity.Cliente;
import com.example.demo.entity.DetallePedido;
import com.example.demo.entity.Pedido;
import com.example.demo.entity.Producto;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;

import java.io.File;
import java.math.BigDecimal;
@Component
public class XmlFileProcessor {
	private final ClienteRepository clienteRepository;
	private final ProductoRepository productoRepository;
	private final PedidoRepository pedidoRepository;
	private final XmlXsdValidator xmlActivitiesPallete;


	private static final Logger logger = LoggerFactory.getLogger(XmlFileProcessor.class);


	public XmlFileProcessor(ClienteRepository clienteRepository,
			ProductoRepository productoRepository,
			PedidoRepository pedidoRepository, XmlXsdValidator xmlActivitiesPallete) {
		this.clienteRepository = clienteRepository;
		this.productoRepository = productoRepository;
		this.pedidoRepository = pedidoRepository;
		this.xmlActivitiesPallete = xmlActivitiesPallete;
	}


	public void processFile(File file) throws Exception {
		
	
		String node = "cliente";
		
		// VALIDACIÓN XSD
    	// Document doc = xmlActivitiesPallete.parseXML(file, "/app/resources/xsd/input2.xsd");
		Document doc = xmlActivitiesPallete.parseXML(file, "src/main/resources/xsd/input2.xsd");
	
		NodeList clientes = doc.getElementsByTagName(node);

		for (int i = 0; i < clientes.getLength(); i++) {

			Node nodo = clientes.item(i);
			if (nodo.getNodeType() == Node.ELEMENT_NODE) {
				Element clienteElement = (Element) nodo;
				processCliente(clienteElement);
			}
		}
			
	}


	private void processCliente(Element clienteElement) {

		String codigoClienteStr = getText(clienteElement, "codigo");

		if (codigoClienteStr == null || codigoClienteStr.isBlank()) {
			logger.warn("El nodo <cliente> no contiene un <codigo> válido. Se omite el procesamiento.");
			return;
		}

		String codigoCliente;
		try {
			codigoCliente = codigoClienteStr;
		} catch (NumberFormatException e) {
			logger.error("El valor del <codigo> no es numérico: {}", codigoClienteStr);
			return;
		}

		// Buscar cliente en BD
		Cliente cliente = clienteRepository.findByCodigo(codigoCliente);

		if (cliente == null) {
			logger.error("Cliente no encontrado en BD: {}", codigoCliente);
			return;
		}

		logger.info("Cliente encontrado: {}", cliente.getCodigo());
		
		//nuevas lineas de codigo
		AuthenticationStrategy auth =
		        new BasicAuthStrategy("admin", "1234");

		//verificar estado crediticio
		EstadoCrediticioServiceApi estadoCrediticio = new EstadoCrediticioServiceApi();
		EstadoCrediticioDTO esDeudor =estadoCrediticio.esDeudorGenerico(auth, Long.valueOf(cliente.getCodigo()));
		if(!esDeudor.isEs_deudor()) {
			// Procesar pedidos
			NodeList pedidos = clienteElement.getElementsByTagName("pedido");

			for (int i = 0; i < pedidos.getLength(); i++) {
				Element pedidoElement = (Element) pedidos.item(i);
				processPedido(pedidoElement, cliente);
			}
		}
		else {
			System.out.println("✔ Cliente con codigo: " + cliente.getCodigo() + " no puede solicitar pedido por deuda pendiente");	
		}
	}

	private void processPedido(Element pedidoElement, Cliente cliente) {

		try {

			Pedido pedido = new Pedido();
			pedido.setCliente(cliente);

			String fechaStr = getText(pedidoElement, "fecha");
			LocalDate date = LocalDate.parse(fechaStr);

			pedido.setFecha(date);
			pedido.setEstado(getText(pedidoElement, "estado"));
			pedido.setTotal(new BigDecimal(getText(pedidoElement, "total")));

			List<DetallePedido> detalles = processDetalles(pedidoElement, pedido);

			pedido.setDetalles(detalles);

			pedidoRepository.save(pedido);


			logger.info("Pedido registrado correctamente para el cliente {}", cliente.getCodigo());
		}     catch (Exception e) {
			logger.error("Error procesando pedido para cliente {}: {}", cliente.getCodigo(), e.getMessage(), e);
		}


	}


	private List<DetallePedido> processDetalles(Element pedidoElement, Pedido pedido) {

		List<DetallePedido> lista = new ArrayList<>();

		NodeList detalles = pedidoElement.getElementsByTagName("detalle_pedido");

		for (int i = 0; i < detalles.getLength(); i++) {
			try {
				Element detElement = (Element) detalles.item(i);
				DetallePedido detalle = new DetallePedido();

				Long idProducto = Long.parseLong(
						((Element) detElement.getElementsByTagName("producto").item(0))
						.getElementsByTagName("id_producto")
						.item(0)
						.getTextContent()
						);

				Producto producto = productoRepository.findById(idProducto)
						.orElseThrow(() -> new RuntimeException("Producto no encontrado: " + idProducto));

				int cantidad = Integer.parseInt(getText(detElement, "cantidad"));

				if (producto.getStock() < cantidad) {
					throw new RuntimeException("Stock insuficiente para producto " + producto.getNombre());
				}

				// Descontar stock
				producto.setStock(producto.getStock() - cantidad);
				productoRepository.save(producto);

				detalle.setPedido(pedido);
				detalle.setProducto(producto);
				detalle.setCantidad(cantidad);
				detalle.setPrecioUnitario(new BigDecimal(getText(detElement, "precio_unitario")));

				lista.add(detalle);

				logger.info("Detalle procesado: producto={} cantidad={} stockRestante={}",
						producto.getNombre(), cantidad, producto.getStock());
			} catch (Exception e) {
				logger.error("Error procesando detalle del pedido {}: {}", pedido.getIdPedido(), e.getMessage(), e);
				// No lanzamos la excepción para continuar con los otros detalles
			}
		}

		return lista;
	}
	

	private String getText(Element element, String tag) {
		return element.getElementsByTagName(tag).item(0).getTextContent();
	}
	

    

}
