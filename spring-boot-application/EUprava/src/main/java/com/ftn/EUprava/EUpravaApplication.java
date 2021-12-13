package com.ftn.EUprava;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ftn.EUprava.crud.CrudKarton;
import com.ftn.EUprava.models.VakcinalniKarton;

@SpringBootApplication
public class EUpravaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EUpravaApplication.class, args);
	}

}
