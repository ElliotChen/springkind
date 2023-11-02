package tw.elliot.depart.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tw.elliot.depart.dto.Employee;

@Component
@Slf4j
public class EmployeeClient {

  @Autowired
  private WebClient webClient;

  @Value("${service.employee.url}")
  private String url;

  public Mono<Employee> retrieveEmployee(String id) {
    return webClient.get().uri(url+"/{id}", id).retrieve().bodyToMono(Employee.class);
  }

  public Flux<Employee> retrieveEmployeeByDepId(String depId) {
    return webClient.get().uri(url+"/dep/"+depId).retrieve().bodyToFlux(Employee.class);
  }
}
