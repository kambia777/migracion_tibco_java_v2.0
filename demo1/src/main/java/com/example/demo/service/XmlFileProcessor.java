package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.example.demo.dto.EstadoCrediticioDTO;
import com.example.demo.entity.Cliente;
import com.example.demo.entity.DetallePedido;
import com.example.demo.entity.Pedido;
import com.example.demo.entity.Producto;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;

import java.math.BigDecimal;
@Component
public class XmlFileProcessor {
	private final ClienteRepository clienteRepository;
	private final ProductoRepository productoRepository;
	private final PedidoRepository pedidoRepository;


	public XmlFileProcessor(ClienteRepository clienteRepository,
			ProductoRepository productoRepository,
			PedidoRepository pedidoRepository) {
		this.clienteRepository = clienteRepository;
		this.productoRepository = productoRepository;
		this.pedidoRepository = pedidoRepository;

	}


	public void processFile(String filePath) throws SAXException, IOException, ParserConfigurationException {
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("⚠ Archivo no encontrado: " + filePath);
			return;
		}

		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.parse(file);

		NodeList clientes = doc.getElementsByTagName("cliente");

		for (int i = 0; i < clientes.getLength(); i++) {

			Node nodo = clientes.item(i);
			if (nodo.getNodeType() == Node.ELEMENT_NODE) {
				Element clienteElement = (Element) nodo;
				processCliente(clienteElement);
			}
			//Element clienteElement = (Element) clientes.item(i);
			//processCliente(clienteElement);
		}
	}

	public void processXmlNodes(Document doc, String nodoName) {

		NodeList clientes = doc.getElementsByTagName(nodoName);

		for (int i = 0; i < clientes.getLength(); i++) {

			Node nodo = clientes.item(i);

			if (nodo.getNodeType() == Node.ELEMENT_NODE) {

				Element elemento = (Element) nodo;

				String codigo = elemento.getElementsByTagName("codigo").item(0).getTextContent();
				String nombre = elemento.getElementsByTagName("nombre").item(0).getTextContent();
				Cliente cliente = new Cliente();
				cliente.setCodigo(codigo);
				cliente.setNombre(nombre);

				clienteRepository.save(cliente);

				System.out.println("💾 Cliente insertado - código: " + codigo);
			}
		}
	}

	private void processCliente(Element clienteElement) {

		String codigoCliente = getText(clienteElement, "id");

		Cliente cliente = clienteRepository.findById(Long.valueOf(codigoCliente))
				.orElseThrow(() -> new RuntimeException("❌ Cliente no encontrado: " + codigoCliente));
		System.out.println("✔ Cliente encontrado en BD: " + cliente.getCodigo());

		// Procesar pedidos
		NodeList pedidos = clienteElement.getElementsByTagName("pedido");

		for (int i = 0; i < pedidos.getLength(); i++) {
			Element pedidoElement = (Element) pedidos.item(i);
			processPedido(pedidoElement, cliente);
		}
	}

	private void processPedido(Element pedidoElement, Cliente cliente) {


		//verificar estado crediticio
		EstadoCrediticioServiceApi estadoCrediticio = new EstadoCrediticioServiceApi();
		EstadoCrediticioDTO esDeudor = estadoCrediticio.esDeudor("admin", "1234", Long.valueOf(cliente.getCodigo()));

		if(!esDeudor.isEs_deudor()) {

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

			System.out.println("✔ Pedido registrado para cliente: " + cliente.getCodigo());
		}
		else {
			System.out.println("✔ Pedido no registrado para cliente: " + cliente.getCodigo() + " por deuda pendiente");	
		}
	}


	private List<DetallePedido> processDetalles(Element pedidoElement, Pedido pedido) {

		List<DetallePedido> lista = new ArrayList<>();

		NodeList detalles = pedidoElement.getElementsByTagName("detalle_pedido");

		for (int i = 0; i < detalles.getLength(); i++) {

			Element detElement = (Element) detalles.item(i);
			DetallePedido detalle = new DetallePedido();

			Long idProducto = Long.parseLong(
					((Element) detElement.getElementsByTagName("producto").item(0))
					.getElementsByTagName("id_producto")
					.item(0)
					.getTextContent()
					);

			Producto producto = productoRepository.findById(idProducto)
					.orElseThrow(() -> new RuntimeException("❌ Producto no encontrado: " + idProducto));

			int cantidad = Integer.parseInt(getText(detElement, "cantidad"));

			if (producto.getStock() < cantidad) {
				throw new RuntimeException("❌ Stock insuficiente para producto " + producto.getNombre());
			}

			// Descontar stock
			producto.setStock(producto.getStock() - cantidad);
			productoRepository.save(producto);

			detalle.setPedido(pedido);
			detalle.setProducto(producto);
			detalle.setCantidad(cantidad);
			detalle.setPrecioUnitario(new BigDecimal(getText(detElement, "precio_unitario")));

			lista.add(detalle);

			System.out.println("✔ Producto procesado: " + producto.getNombre() +
					" (stock restante: " + producto.getStock() + ")");
		}

		return lista;
	}

	private String getText(Element element, String tag) {
		return element.getElementsByTagName(tag).item(0).getTextContent();
	}

}
