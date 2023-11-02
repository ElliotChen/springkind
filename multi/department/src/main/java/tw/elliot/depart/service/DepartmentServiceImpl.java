package tw.elliot.depart.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tw.elliot.depart.client.EmployeeClient;
import tw.elliot.depart.dto.DepartmentDto;
import tw.elliot.depart.dto.Employee;
import tw.elliot.depart.repo.DepartmentRepo;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

  private DepartmentRepo departmentRepo;
  private EmployeeClient employeeClient;
  public DepartmentServiceImpl(DepartmentRepo repo, EmployeeClient employeeClient) {
    this.departmentRepo = repo;
    this.employeeClient = employeeClient;
  }

  @Override
  public Mono<DepartmentDto> findById(String id) {
    return departmentRepo.findById(id).flatMap(dep -> {
      Mono<List<Employee>> listMono = employeeClient.retrieveEmployeeByDepId(dep.getId()).collectList();
      return listMono.map(employees -> new DepartmentDto(dep, employees));
    });
  }
}
