package com.seminario.pasantias;

<<<<<<< HEAD
=======
import org.mybatis.spring.annotation.MapperScan;
>>>>>>> upstream/feature/docker-setup
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
<<<<<<< HEAD
=======
@MapperScan("com.seminario.pasantias.persistence")
>>>>>>> upstream/feature/docker-setup
public class PasantiasApplication {

	public static void main(String[] args) {
		SpringApplication.run(PasantiasApplication.class, args);
	}

}
