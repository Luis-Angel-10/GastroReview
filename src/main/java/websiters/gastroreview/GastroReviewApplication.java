package websiters.gastroreview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "websiters.gastroreview.client")
@SpringBootApplication
public class GastroReviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(GastroReviewApplication.class, args);
	}

}
