package tw.elliot.emp.repo;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import tw.elliot.emp.entity.Employee;

/**
 * @author elliot
 */
@Repository
public interface EmployeeRepo extends R2dbcRepository<Employee, String> {

  Flux<Employee> findByDepartmentid(String departmentid);
}
