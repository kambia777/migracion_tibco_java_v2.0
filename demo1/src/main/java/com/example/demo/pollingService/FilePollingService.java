package com.example.demo.pollingService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.example.demo.filePallete.FilePallete;
import com.example.demo.service.XmlFileProcessor;
import com.example.demo.service.XmlXsdValidator;

@Component
@EnableScheduling
public class FilePollingService {

    private static final Logger log = LoggerFactory.getLogger(FilePollingService.class);

    @Value("${file.input-dir}")
    private String inputDir;

    @Value("${file.processed-dir}")
    private String processedDir;

    @Value("${file.error-dir}")
    private String errorDir;

    private final XmlFileProcessor xmlFileProcessor;
    private final FilePallete filePallete;
    private final XmlXsdValidator xmlActivitiesPallete;

    public FilePollingService(XmlFileProcessor xmlFileProcessor, FilePallete filePallete, XmlXsdValidator xmlActivitiesPallete) {
        this.xmlFileProcessor = xmlFileProcessor;
        this.filePallete = filePallete;
        this.xmlActivitiesPallete = xmlActivitiesPallete;
    }

    @Scheduled(fixedDelay = 10000000) // cada 10 segundos
    public void pollDirectory() {
    	
        File[] files = filePallete.listFiles(inputDir, ".xml");

        if (files.length == 0) {
        	log.error("❌ Archivo no encontrado: {}", inputDir);
            return;
        }

        for (File file : files) {
            try {
            	
    			// VALIDACIÓN XSD
            	xmlActivitiesPallete.validarXML(file, "src/main/resources/xsd/input2.xsd");
            	
            	 // 2️⃣ Parseo XML → Document
                Document doc = parseXml(file);

                log.info("Procesando fichero: {}", file.getName());
                xmlFileProcessor.processFile(doc, "cliente");

                moveFile(file, processedDir);
            } catch (Exception e) {
                log.error("Error procesando fichero {}", file.getName(), e);
                moveFile(file, errorDir);
            }
        }
    }
    
    private Document parseXml(File file) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder();
        return builder.parse(file);
    }
    


    private void moveFile(File file, String targetDir) {
        try {
            Files.createDirectories(Paths.get(targetDir));
            Files.move(
                file.toPath(),
                Paths.get(targetDir, file.getName()),
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            log.error("Error moviendo fichero {}", file.getName(), e);
        }
    }
}
