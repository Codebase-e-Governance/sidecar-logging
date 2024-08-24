package com.sidecar.logging.controller;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sidecar.logging.dto.AspRes;

@RestController
@RequestMapping("/test")
public class TestController {

	private static Logger log = LogManager.getLogger(TestController.class);
	
	
	private void init() {
		int cnt = 0;
		Timestamp timestamp;
		System.out.println("@@@init@@@");
		try {
			while(true) {
				timestamp = new Timestamp(System.currentTimeMillis());
				log.info("count:{} time:{}", cnt, timestamp);
				log.debug("count:{} time:{}", cnt, timestamp);
				log.trace("count:{} time:{}", cnt, timestamp);
				cnt++;
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("@@@initExit@@@");
	}
	
	@GetMapping
	public AspRes test() {
		log.debug("@@inside test");
		CompletableFuture.runAsync(() -> init());
		return new AspRes("accepted!");
	}

}
