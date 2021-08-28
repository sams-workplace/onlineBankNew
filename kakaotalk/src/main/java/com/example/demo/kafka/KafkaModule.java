package com.example.demo.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafkaModule {
	
	private static final String TOPIC = "onlinebank";
	
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(KafkaVO kafkaVO) {
    	try { 
    		ObjectMapper objectMapper = new ObjectMapper();
        	String message = objectMapper.writeValueAsString(kafkaVO);
        	System.out.println("#### -> Producing message : "+ message );
            this.kafkaTemplate.send(TOPIC, message);
    	} catch ( Exception e ) {
    		e.printStackTrace();
    	}
    }
}
