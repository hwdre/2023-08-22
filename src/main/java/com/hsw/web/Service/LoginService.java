package com.hsw.web.Service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsw.web.dao.LoginDAO;

@Service
public class LoginService {
	
	@Autowired
	private LoginDAO loginDAO;

	public Map<String, Object> login(Map<String,String> map) {
		return loginDAO.login(map);
	}
	
	
	
}
