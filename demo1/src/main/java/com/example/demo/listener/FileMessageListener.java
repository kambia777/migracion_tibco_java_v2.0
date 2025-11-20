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

import com.example.demo.Repository.ClienteRepository;
import com.example.demo.entity.Cliente;
import com.example.demo.service.ClienteServiceApi;
import com.example.demo.service.XmlFileProcessor;

@Component
public class FileMessageListener {
    private final XmlFileProcessor xmlFileProcessor;

    public FileMessageListener(XmlFileProcessor xmlFileProcessor) {
        this.xmlFileProcessor = xmlFileProcessor;
    }
	@RabbitListener(queues = "cola.ficheros")
	public void receiveMessage(String filePath) throws SAXException, IOException, ParserConfigurationException {
		System.out.println("📥 Ruta recibida: " + filePath);
		xmlFileProcessor.processFile(filePath);
		ClienteServiceApi servicio = new ClienteServiceApi();
        Cliente[] clientes = servicio.obtenerTodosClientes("admin", "1234");
	}

}
