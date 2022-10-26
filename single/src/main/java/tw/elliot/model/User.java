package tw.elliot.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
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
