package tw.elliot.emp.ctrl;

import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tw.elliot.emp.entity.Employee;
import tw.elliot.emp.service.EmployeeService;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeCtrl {
  @Autowired
  private EmployeeService employeeService;

  @GetMapping("/{id}")
  public Mono<Employee> findByIdList(@PathVariable String id) {
    return employeeService.findById(id);
  }

  @GetMapping("/ids")
  public Flux<Employee> findByIdList(@RequestParam("ids") ArrayList<String> ids) {
    return employeeService.findByIdList(ids);
  }

  @GetMapping("/dep/{depId}")
  public Flux<Employee> findByDepId(@PathVariable("depId") String depId) {
    log.error("findByDepId - [{}]", depId);
    return employeeService.findByDepId(depId);
  }
}
