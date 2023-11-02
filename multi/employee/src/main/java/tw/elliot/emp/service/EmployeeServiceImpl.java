package tw.elliot.emp.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tw.elliot.emp.entity.Employee;
import tw.elliot.emp.repo.EmployeeRepo;

/**
 * @author Elliot
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
  private final EmployeeRepo employeeRepo;

  @Override
  public Mono<Employee> findById(String id) {
    return employeeRepo.findById(id);
  }

  @Override
  public Flux<Employee> findByIdList(List<String> ids) {
    return employeeRepo.findAllById(ids);
  }

  @Override
  public Flux<Employee> findByDepId(String depId) {
    return employeeRepo.findByDepartmentid(depId);
  }
}
