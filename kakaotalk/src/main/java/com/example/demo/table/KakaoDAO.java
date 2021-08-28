package com.example.demo.table;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface  KakaoDAO {
	void insertmsg(SendMsgVO vo) throws Exception;;
	List<SendMsgVO> selectmsg() throws Exception;;
}
