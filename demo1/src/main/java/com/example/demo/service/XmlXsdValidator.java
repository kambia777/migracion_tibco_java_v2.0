package com.example.demo.service;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.xml.sax.SAXException;

@Component
public class XmlXsdValidator {
	
	private static final Logger log = LoggerFactory.getLogger(XmlXsdValidator.class);

    public void validar(File xmlFile, String xsdPath) {
        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();

            validator.validate(new StreamSource(xmlFile));

            log.info("✅ XML válido según el XSD");

        } catch (SAXException e) {
            log.error("❌ XML no cumple el XSD: {}", e.getMessage());
            throw new RuntimeException("XML inválido según XSD", e);
        } catch (IOException e) {
            log.error("❌ Error leyendo XML/XSD", e);
            throw new RuntimeException("Error de lectura XML/XSD", e);
        }
    }

}
