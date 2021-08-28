package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.table.KakaoDAO;
import com.example.demo.table.SendMsgVO;

@Service
@RestController
@EnableAutoConfiguration
@Transactional
@MapperScan(basePackages="com.example.KakaoTakMapper.table")
public class KakaoService {

    @Autowired
    KakaoDAO kakaoDAO;
    
    @RequestMapping(value ="/kakaotalk/inqer", method = RequestMethod.GET)
    public @ResponseBody List<SendMsgVO> selectmsg() {
    	
    	List<SendMsgVO> sendmsg  = null;
    	try {
    		sendmsg = kakaoDAO.selectmsg();
    	} catch (Exception e  ) {
    		e.printStackTrace();
    	}
    	
        return  sendmsg;
    }

    @RequestMapping(value ="/kakaotalk/regist", method = RequestMethod.POST)
    public @ResponseBody String insertmsg(@RequestBody Map<String,String> param) {
    	String rv="";
    	try {
    		SendMsgVO vo = new SendMsgVO(); 
    		if ( param.get( "phone") == null || param.get( "phone").toString().isEmpty() ) {
    			rv="입력값을 확인해주세요";
    		} else if ( param.get( "message") == null || param.get( "message").toString().isEmpty() ) {
    			rv="입력값을 확인해주세요";
    		} else {
    			vo.setPhone(  param.get( "phone") );
    			vo.setMessage( param.get( "message") );
    			kakaoDAO.insertmsg(vo);
				    	
		        rv = "전송번호 : " + param.get("phone") + "\n전송메세지 : " + param.get("message") ;
    		}
    	}catch ( Exception e) {
    		e.printStackTrace();
    		rv = e.getMessage();
    	}
    	return rv;
    }
}
