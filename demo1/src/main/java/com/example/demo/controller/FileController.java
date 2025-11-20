package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.producer.FileMessageProducer;
@RestController
@RequestMapping("/files")
public class FileController {
	
	
	private final FileMessageProducer fileMessageProducer;

    public FileController(FileMessageProducer fileMessageProducer) {
        this.fileMessageProducer = fileMessageProducer;
    }

    @PostMapping("/send")
    public String sendFile(@RequestParam String filePath) {
        fileMessageProducer.sendFilePath(filePath);
        return "Ruta enviada a la cola: " + filePath;
    }

}
