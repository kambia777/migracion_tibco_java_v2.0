package com.example.demo.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.demo.service.XmlFileProcessor;

@Component
public class FileMessageListener {
	
	private static final Logger logger = LoggerFactory.getLogger(FileMessageListener.class);
	
    private final XmlFileProcessor xmlFileProcessor;

    public FileMessageListener(XmlFileProcessor xmlFileProcessor) {
        this.xmlFileProcessor = xmlFileProcessor;
    }
	@RabbitListener(queues = "cola.ficheros")
	public void receiveMessage(String filePath)  {
		logger.info("📥 Mensaje recibido desde la cola: {}", filePath);

        // 1. Validación básica
        if (filePath == null || filePath.isBlank()) {
            logger.warn("⚠ Se recibió un mensaje vacío o nulo. Ignorando...");
            return; // Importante: no lanzar excepción innecesaria
        }
        try {
            logger.info("▶ Procesando archivo: {}", filePath);
            xmlFileProcessor.processFile(filePath);
            logger.info("✔ Archivo procesado correctamente: {}", filePath);

        } catch (Exception e) {
            logger.error("🔥 Error inesperado procesando el mensaje", e);
        }
		//ClienteServiceApi servicio = new ClienteServiceApi();
        //Cliente[] clientes = servicio.obtenerTodosClientes("admin", "1234");
	}

}
