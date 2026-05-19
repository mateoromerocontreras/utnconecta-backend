package com.seminario.pasantias;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.seminario.pasantias.persistence")
@EnableScheduling
public class PasantiasApplication {

	public static void main(String[] args) {
		SpringApplication.run(PasantiasApplication.class, args);
	}

}
