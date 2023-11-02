package tw.elliot.depart.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import tw.elliot.depart.entity.Department;

@Repository
public interface DepartmentRepo extends R2dbcRepository<Department, String> {

}
