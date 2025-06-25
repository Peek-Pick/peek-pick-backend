package org.beep.sbpp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class Application {
	//@EnableElasticsearchRepositories(basePackages = "org.beep.sbpp.products.repository")  // ✅ 존재하는 패키지
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
