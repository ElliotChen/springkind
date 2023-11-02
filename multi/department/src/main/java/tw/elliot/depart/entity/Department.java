package tw.elliot.depart.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Department {
  @Id
  private String id;
  private String name;
  private String description;
}
