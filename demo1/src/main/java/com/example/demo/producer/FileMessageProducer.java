package com.example.demo.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
// @Component
public class FileMessageProducer {
	
	private static final Logger logger = LoggerFactory.getLogger(FileMessageProducer.class);
	
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.file-processing}")
    private String fileQueue;
    
    public FileMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    /**
     * Sends the file path to the configured RabbitMQ queue.
     * 
     * @param filePath Path of the file to process.
     */
    public void sendFilePath(String filePath) {
        try {
            rabbitTemplate.convertAndSend(fileQueue, filePath);
            logger.info("📤 File path sent to queue [{}]: {}", fileQueue, filePath);
        } catch (Exception ex) {
            logger.error("❌ Error while sending file path to queue: {}", filePath, ex);
        }
    }

}
