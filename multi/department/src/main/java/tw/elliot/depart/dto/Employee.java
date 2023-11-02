package tw.elliot.depart.dto;

import lombok.Data;

@Data
public class Employee {
  private String id;
  private String name;
  private Integer age;
  private String description;
  private String departmentid;
}
