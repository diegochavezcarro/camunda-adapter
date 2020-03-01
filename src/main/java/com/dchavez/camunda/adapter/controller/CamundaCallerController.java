package com.dchavez.camunda.adapter.controller;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryException;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RestController
public class CamundaCallerController {

	private static final Logger logger = LoggerFactory.getLogger(CamundaCallerController.class);

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/camunda")
	@ExceptionHandler({ Exception.class })
	public String callCamunda() {
		logger.debug("===============================");
		logger.debug("Inside Camunda Caller");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String json = "{\"variables\": {\"name\":{\"value\":\"viaje\",\"type\":\"String\"}}}";

		HttpEntity<String> entity = new HttpEntity<String>(json, headers);

		ResponseEntity<String> restExchange = restTemplate.exchange(
				"http://localhost:8081/rest/engine/default/process-definition/key/trip/start", HttpMethod.POST, entity,
				String.class);

		logger.debug("resultado de camunda: " + restExchange.getBody());

		return restExchange.getBody();

	}

	@GetMapping("/retry")
	public String validateSPringRetryCapability(@RequestParam(required = false) boolean simulateretry,
			@RequestParam(required = false) boolean simulateretryfallback) {
		logger.debug("===============================");
		logger.debug("Inside Retry");

		Map<Class<? extends Throwable>, Boolean> r = new HashMap<>();
		//r.put(RetryException.class, true);
		r.put(ResourceAccessException.class, true);

		SimpleRetryPolicy simplePolicy = new SimpleRetryPolicy(5, r);
		// Set the max retry attempts
		simplePolicy.setMaxAttempts(5);

		// Política BackOff
		FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();

		// 5000ms -> 5s
		backOffPolicy.setBackOffPeriod(5000);
		TimeoutRetryPolicy timeoutpolicy = new TimeoutRetryPolicy();
		timeoutpolicy.setTimeout(15000);
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(simplePolicy);
		retryTemplate.setBackOffPolicy(backOffPolicy);
		//retryTemplate.setRetryPolicy(timeoutpolicy);

		String result = retryTemplate.execute(arg0 -> {
			logger.debug("hace un retry");
			 
			ResponseEntity<String> restExchange = restTemplate.exchange("http://localhost:8082/products/", HttpMethod.GET,
					null, String.class);

			logger.debug("resultado de productos: " + restExchange.getBody());
			return restExchange.getBody();
			
			
		}, arg0 -> {
			logger.debug("hace un recovery");
			throw new Exception("no fue posible");
		});
		logger.debug("salida del retry " + result);

		return result;

	}

}
