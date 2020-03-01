package com.dchavez.camunda.adapter.events.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dchavez.camunda.async.events.model.CamundaStartModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Service
public class Consumer {
	
	private final Logger logger = LoggerFactory.getLogger(Consumer.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@StreamListener(Sink.INPUT)
	public void receiveStartAsyncCamunda(@Payload CamundaStartModel camundaStart) {
		logger.info(String.format("$$ -> Consumed Message with Action -> %s", camundaStart.getAction()));
		logger.debug("Received a message of type " + camundaStart.getType());
		logger.debug("Received a Start event from the camunda start service for pid id {}",
					camundaStart.getpId());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String json = "{\"variables\": {\"name\":{\"value\":\"viaje\",\"type\":\"String\"}}}";

		HttpEntity<String> entity = new HttpEntity<String>(json, headers);

		ResponseEntity<String> restExchange = restTemplate.exchange(
				"http://localhost:8081/rest/engine/default/process-definition/key/trip/start", HttpMethod.POST, entity,
				String.class);

		logger.debug("resultado de camunda: " + restExchange.getBody());

	}

}
