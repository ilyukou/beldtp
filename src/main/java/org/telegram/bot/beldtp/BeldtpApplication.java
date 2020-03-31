package org.telegram.bot.beldtp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BeldtpApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeldtpApplication.class, args);
	}

}
