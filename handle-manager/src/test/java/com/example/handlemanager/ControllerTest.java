package com.example.handlemanager;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.handlemanager.controller.HandleController;
import com.example.handlemanager.model.HandleRecord;
import com.example.handlemanager.service.HandleService;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	
	@Test
	public void testHello() throws Exception{
		/*mockMvc
		.perform(get("/hello"))
		.andExpect(status().isOk());*/
	}


}
