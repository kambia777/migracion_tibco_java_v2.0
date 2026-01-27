package com.example.demo.filePallete;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class FilePallete {
	
	private static final Logger log = LoggerFactory.getLogger(FilePallete.class);

    /**
     * Lista los archivos de un directorio que cumplan con un patrón glob.
     * Ejemplo de patrón: "*.xml", "CLIENTE_*.csv"
     *
     * @param directoryPath ruta del directorio
     * @param pattern patrón de archivos (glob)
     * @return array de ficheros que cumplen el patrón
     */
    public File[] listFiles(String directoryPath, String pattern) {
        File folder = new File(directoryPath);

        if (!folder.exists() || !folder.isDirectory()) {
            log.error("Directorio no válido: {}", directoryPath);
            
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(pattern));

        if (files == null || files.length == 0) {
        	log.error("❌ Archivo no encontrado: {}");
           
        }
        
        return files;
    }

}
