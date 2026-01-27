package com.example.demo.controller;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;

import com.example.demo.producer.FileMessageProducer;
//@RestController
//@RequestMapping("/files")
public class FileController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	
	
	private final FileMessageProducer fileMessageProducer;

    public FileController(FileMessageProducer fileMessageProducer) {
        this.fileMessageProducer = fileMessageProducer;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendFile(@RequestParam String filePath) {
    	logger.info("📥 Solicitud recibida para enviar archivo: {}", filePath);
    	
        // 1. Validación básica
        if (filePath == null || filePath.isBlank()) {
            logger.warn("⚠ Ruta de archivo vacía o nula");
            return ResponseEntity
                    .badRequest()
                    .body("❌ La ruta del archivo no puede estar vacía.");
        }
        
        // 2. Validar que el archivo exista
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            logger.warn("❌ Archivo no encontrado: {}", filePath);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("❌ El archivo no existe: " + filePath);
        }
        // 3. Enviar a la cola
        try {
            fileMessageProducer.sendFilePath(filePath);
            logger.info("📤 Archivo enviado a la cola correctamente: {}", filePath);

            return ResponseEntity.ok("✔ Ruta enviada a la cola: " + filePath);

        } catch (Exception e) {
            logger.error("❌ Error enviando archivo a la cola", e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error enviando el archivo a la cola: " + e.getMessage());
        }
        
    }

}
