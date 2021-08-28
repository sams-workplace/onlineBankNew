package com.example.demo.table;

import lombok.Data;

@Data
//@Alias("sendmsg")
public class SendMsgVO {
	private Long id; 
	private String phone; 
	private String message; 
	
	public SendMsgVO() {
	}
	
	public SendMsgVO(String pphone, String pmessage) {
		this.phone = pphone;
		this.message = pmessage;
	}
}
