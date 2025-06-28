package com.umpierre.functionalinterfaces;

import com.umpierre.functionalinterfaces.util.Validador;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FunctionalInterfacesApplication {

	public static void main(String[] args) {


		Validador validador = new Validador() {
			@Override
			public boolean validarCPF(String valor) {
				if(valor.length() == 11) return true;
				return false;
			}
		};

		System.out.println(validador.validarCPF("81401310087"));


		SpringApplication.run(FunctionalInterfacesApplication.class, args);
	}

}
