package tw.elliot.depart.dto;

import java.util.List;
import lombok.Data;
import tw.elliot.depart.entity.Department;

@Data
public class DepartmentDto {
  private String id;
  private String name;
  private String description;
  private List<Employee> employees;

  public DepartmentDto(Department department, List<Employee> employees) {
    this.id = department.getId();
    this.name = department.getName();
    this.description = department.getDescription();
    this.employees = employees;
  }

}
