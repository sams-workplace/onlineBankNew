package com.example.demo.kafka;

import lombok.Data;

@Data
public class KafkaVO {
	private String PHONE; //전화번호
	private String MESSAGE; //카카오 전송 메세지
}
