package tw.elliot.depart.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import tw.elliot.depart.dto.Employee;

@SpringBootTest
@Slf4j
public class WebClientTest {

  @Autowired
  public WebClient webClient;
  @Test
  public void test01() {
    ResponseSpec retrieve = webClient.get().uri("http://localhost:8081/employee/dep/dep01").retrieve();
    Flux<Employee> flux = retrieve.bodyToFlux(Employee.class);

    flux.toStream().forEach(employee -> {log.error("Found employee {}", employee.getName());});


  }
}
