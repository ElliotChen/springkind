package tw.elliot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "T_USER")
@Data
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class User {

  @Id
  @GeneratedValue(generator = "jpa-uuid")
  @Column(length = 32)
  private String id;

  @Column(length = 60)
  private String name;

  private Integer age;
}
