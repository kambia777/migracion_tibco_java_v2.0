package com.example.demo.listener;


import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.example.demo.service.XmlFileProcessor;
import com.example.demo.service.XmlXsdValidator;

//@Component
public class FileMessageListener {
	
	private static final Logger logger = LoggerFactory.getLogger(FileMessageListener.class);
	
    private final XmlFileProcessor xmlFileProcessor;
    private final XmlXsdValidator xmlXsdValidator;

    public FileMessageListener(XmlFileProcessor xmlFileProcessor, XmlXsdValidator xmlXsdValidator) {
        this.xmlFileProcessor = xmlFileProcessor;
        this.xmlXsdValidator = xmlXsdValidator;
    }
	@RabbitListener(queues = "cola.ficheros")
	public void receiveMessage(String filePath)  {
        logger.info("📥 Mensaje recibido desde la cola: {}", filePath);

        // 1️⃣ Validación básica del mensaje
        if (filePath == null || filePath.isBlank()) {
            logger.warn("⚠ Mensaje vacío o nulo. Ignorando...");
            return;
        }

        File file = new File(filePath);

        if (!file.exists()) {
            logger.error("❌ El fichero no existe: {}", filePath);
            return;
        }

        try {
            logger.info("📄 Procesando fichero: {}", file.getName());

            // 2️⃣ Validación XSD
            xmlXsdValidator.validarXML(file, "src/main/resources/xsd/input2.xsd");
            logger.info("✅ Validación XSD correcta");

            // 3️⃣ Parseo XML → Document
            Document document = parseXml(file);

            // 4️⃣ Procesamiento de negocio
            xmlFileProcessor.processFile(document, "cliente");
            logger.info("✔ Fichero procesado correctamente: {}", file.getName());

        } catch (Exception e) {
            logger.error("🔥 Error procesando el fichero {}", file.getName(), e);
        }
    }

	
    private Document parseXml(File file) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder();
        return builder.parse(file);
    }

}
