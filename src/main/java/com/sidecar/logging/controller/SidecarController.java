/**
 * 
 */
package com.sidecar.logging.controller;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidecar.logging.dto.UsersRes;


/**
 * 
 */
@RestController
public class SidecarController {
	private static Logger log = LogManager.getLogger(SidecarController.class);
	
	@GetMapping
	public List<UsersRes> users() {
		log.debug("@@inside users");
		return Arrays.asList(new UsersRes("accpeted !", 0));
	}

}
