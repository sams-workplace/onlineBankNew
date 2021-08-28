package com.example.demo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.table.KakaoDAO;
import com.example.demo.table.SendMsgVO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class KafkaService {
	
    @Autowired
    KakaoDAO kakaoDAO;

	@KafkaListener(topics = "onlinebank")
	public void getKafka(String message) {
		
		System.out.println( "kakaotalk getKafka START " );
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, String> map = (Map<String, String>)objectMapper.readValue( message, Map.class);
			System.out.println( "kafka recerve data : " + map);
			
			if ( map.get("job")!= null && map.get("job").indexOf("kakaotalk") >=0 ) {
				
				SendMsgVO vo = new SendMsgVO(); 
				vo.setPhone(  map.get( "phone").toString() );
	    		vo.setMessage( map.get( "message").toString() );
	    		kakaoDAO.insertmsg(vo);
			} else {
				System.out.println( "kafka Skip ");
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
