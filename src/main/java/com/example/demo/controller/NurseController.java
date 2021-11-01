package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Nurse;

@RestController
@RequestMapping("/myapp/nurses")
public class NurseController {

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/users")
	public List<Object> getUsers() {
		String url = "https://jsonplaceholder.typicode.com/users";
		Object[] objects = restTemplate.getForObject(url, Object[].class);
		return Arrays.asList(objects);
	}

//	@GetMapping("/oclc")
//	public Object getObject() {
//		String url = "http://firefly-m1.dev.oclc.org/firefly-service/rs/sru/connex-xwc?query=srw.su";
////		String url = "http://firefly-m1.dev.oclc.org/firefly-service/rs/sru/connex-xwc?query=srw.su+%3D+%22Soccer%22&version=1.1&operation=searchRetrieve&recordSchema=info%3Asrw%2Fschema%2F1%2FCDFXML&maximumRecords=10&x-info-5-serviceName=FireflySRUPageFind&startRecord=1&resultSetTTL=0&recordPacking=xml&recordXPath=&sortKeys=";
//		Object object = restTemplate.getForObject(url, Object.class);
//		return object;
//	}

	@GetMapping("/getAllNurses")
	public List<Nurse> getAllNurses() {
		String url = "http://localhost:8082/nurses/list";
		Nurse[] objects = restTemplate.getForObject(url, Nurse[].class);
		return Arrays.asList(objects);
	}

	@GetMapping("/get/{nurseId}")
	public Nurse getNurseById(@PathVariable("nurseId") int nurseId) {
		String url = "http://localhost:8082/nurses/list/id/" + nurseId;
		Nurse nurse = restTemplate.getForObject(url, Nurse.class);
		return nurse;
	}

	@GetMapping("/getByName/{lastName}")
	public List<Nurse> getNursesByLastName(@PathVariable("lastName") String lastName) {
		String url = "http://localhost:8082/nurses/list/lastname/" + lastName;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<List> responseNurse = restTemplate.exchange(url, HttpMethod.GET, requestEntity, List.class);
		List<Nurse> list = responseNurse.getBody();
		return list;
	}
}
