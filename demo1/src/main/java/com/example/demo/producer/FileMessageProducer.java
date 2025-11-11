package com.example.demo.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
@Component
public class FileMessageProducer {
	
    private final RabbitTemplate rabbitTemplate;

    public FileMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendFilePath(String filePath) {
        rabbitTemplate.convertAndSend("cola.ficheros", filePath);
        System.out.println("📤 Enviada ruta del archivo: " + filePath);
    }

}
