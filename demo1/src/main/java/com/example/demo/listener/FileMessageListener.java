package com.example.demo.listener;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component
public class FileMessageListener {

	@RabbitListener(queues = "cola.ficheros")
	public void receiveMessage(String filePath) throws SAXException, IOException, ParserConfigurationException {
		System.out.println("📥 Ruta recibida: " + filePath);
		processFile(filePath);
	}

	private void processFile(String filePath) throws SAXException, IOException, ParserConfigurationException {
		File file = new File(filePath);
		if (file.exists()) {
			System.out.println("✅ Procesando archivo: " + file.getName());
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(file);
			processXmlNodes(doc, "cliente");
			/*NodeList clientes = doc.getElementsByTagName("cliente");
			for (int i = 0; i < clientes.getLength(); i++) {
				//System.out.println(clientes.getLength());
				Node nodo = clientes.item(i); 	
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {
					Element elemento = (Element) nodo;

					String nombre = elemento.getElementsByTagName("codigo").item(0).getTextContent();


					System.out.println("codigo: " + nombre );
				}

			}*/



			// Aquí puedes añadir tu lógica de procesamiento
		} else {
			System.out.println("⚠️ Archivo no encontrado: " + filePath);
		}
	}
		
		public void processXmlNodes(Document doc, String nodoName) {
			
			NodeList clientes = doc.getElementsByTagName(nodoName);
			for (int i = 0; i < clientes.getLength(); i++) {
				//System.out.println(clientes.getLength());
				Node nodo = clientes.item(i); 	
				if (nodo.getNodeType() == Node.ELEMENT_NODE) {
					Element elemento = (Element) nodo;

					String nombre = elemento.getElementsByTagName("codigo").item(0).getTextContent();


					System.out.println("codigo: " + nombre );
				}

			}
		}
	

}
