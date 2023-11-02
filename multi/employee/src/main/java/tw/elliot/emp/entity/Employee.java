package tw.elliot.emp.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Employee {
  @Id
  private String id;
  private String name;
  private Integer age;
  private String description;
  private String departmentid;
}
