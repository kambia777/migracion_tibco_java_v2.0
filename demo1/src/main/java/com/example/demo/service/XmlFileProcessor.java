package com.example.demo.service;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.example.demo.Repository.ClienteRepository;
import com.example.demo.entity.Cliente;
@Component
public class XmlFileProcessor {
    private final ClienteRepository clienteRepository;

    public XmlFileProcessor(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }


    public void processFile(String filePath) throws SAXException, IOException, ParserConfigurationException {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("⚠️ Archivo no encontrado: " + filePath);
            return;
        }

        System.out.println("✅ Procesando archivo: " + file.getName());

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(file);

        processXmlNodes(doc, "cliente");
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

}
