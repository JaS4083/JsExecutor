package com.scriptjs.run.scriptjsrun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ScriptjsRunApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScriptjsRunApplication.class, args);
	}

}
