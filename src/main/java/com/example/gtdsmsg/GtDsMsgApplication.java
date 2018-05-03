package com.example.gtdsmsg;

import java.security.PrivateKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.jms.Destination;
import javax.transaction.Transactional;

import org.h2.util.New;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.Jdbc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.bytebuddy.asm.Advice.This;

@SpringBootApplication
public class GtDsMsgApplication {

	public static void main(String[] args) {
		SpringApplication.run(GtDsMsgApplication.class, args);
	

	}
	public static final String DESTINATION= "messages";
	
	@Service
	public static class Messagelistener{
		@JmsListener(destination= DESTINATION )
		public void newMessage(String id)
		{
			
		System.out.println("MESSAGE ID :"+id);	
		}
	}
	
	
	
	@RestController
	public static class GtApiRestController
	{
		private final JdbcTemplate jdbctemplate;
		private final JmsTemplate jmstemplate;
		public GtApiRestController(JdbcTemplate jdbctemplate, JmsTemplate jmstemplate) {
			
			this.jdbctemplate = jdbctemplate;
			this.jmstemplate = jmstemplate;
		}
		
		
		@GetMapping
			public  Collection<Map<String, String>> read()
			{
			
			return this.jdbctemplate.query("select * FROM MESSAGE", new RowMapper<Map<String,String>>(){

				@Override
				public Map<String, String> mapRow(ResultSet resultSet, int arg1) throws SQLException {
					Map<String, String> msg=new HashMap<String,String>();
					msg.put("id", resultSet.getString("ID"));
					msg.put("message", resultSet.getString("MESSAGE"));
					return msg;
					
				
				}
				
							
			}
				)
			;
			
			}
		
		
		
		
@Transactional
		@PostMapping
		public void write(@RequestBody Map<String,String> payload, @RequestParam Optional<Boolean> rollback)
		
		
		{
			String Id=UUID.randomUUID().toString();
			
			String name=payload.get("name");
			String msg="HELLO"+name;
	          this.jdbctemplate.update("insert into MESSAGE(ID,MESSAGE) VALUES (?,?) ",Id, msg);
	          this.jmstemplate.convertAndSend(DESTINATION,Id);
	          
			
			
			if (rollback.orElse(false)) {
throw new RuntimeException("couldnt write the message");



 
			}
			
			
			
			
			
			
			
			
			
			
			
		}
	}
}

	
	
	



