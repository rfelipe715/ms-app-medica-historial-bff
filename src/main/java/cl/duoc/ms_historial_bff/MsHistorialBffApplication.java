package cl.duoc.ms_historial_bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsHistorialBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsHistorialBffApplication.class, args);
	}

}
