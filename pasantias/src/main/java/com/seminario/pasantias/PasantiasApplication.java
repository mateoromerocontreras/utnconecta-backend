package com.seminario.pasantias;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.seminario.pasantias.persistence")
public class PasantiasApplication {

	public static void main(String[] args) {
		SpringApplication.run(PasantiasApplication.class, args);
	}

}
