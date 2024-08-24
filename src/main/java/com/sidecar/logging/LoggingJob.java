package com.sidecar.logging;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class LoggingJob {
	
	private static Logger log = LogManager.getLogger(LoggingJob.class);
	
	@PostConstruct
	private void init() {
		
	}

}
