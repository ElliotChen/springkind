package tw.elliot.emp.service;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tw.elliot.emp.entity.Employee;

public interface EmployeeService  {
  Mono<Employee> findById(String id);
  Flux<Employee> findByIdList(List<String> ids);

  Flux<Employee> findByDepId(String depId);
}
