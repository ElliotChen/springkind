package tw.elliot.emp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableR2dbcRepositories
@EnableR2dbcAuditing
@EnableKafka
@EnableScheduling
public class EmployeeApplication {

  public static void main(String[] args) {
    SpringApplication.run(EmployeeApplication.class, args);
  }

}
