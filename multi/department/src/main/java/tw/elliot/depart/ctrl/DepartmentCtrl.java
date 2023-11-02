package tw.elliot.depart.ctrl;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import tw.elliot.depart.dto.DepartmentDto;
import tw.elliot.depart.dto.Employee;
import tw.elliot.depart.entity.Department;
import tw.elliot.depart.repo.DepartmentRepo;
import tw.elliot.depart.service.DepartmentService;
import tw.elliot.depart.service.DepartmentServiceImpl;

@RestController
@RequestMapping("/department")
@Slf4j
public class DepartmentCtrl {

  @Autowired
  private DepartmentService departmentService;

  @Autowired
  private DepartmentRepo departmentRepo;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Value("${service.employee.url}")
  private String url;

  @RequestMapping("/{id}")
  public Mono<DepartmentDto> findById(@PathVariable String id) {
    log.info("Send test1 to kafka");
    this.kafkaTemplate.send("pubsub", "test1");
    log.info("WebClient Check trace_id");
    return departmentService.findById(id);
  }

  @RequestMapping("/rest/{id}")
  public Mono<DepartmentDto> findByRestTemplate(@PathVariable String id) {
    log.info("Send test1 to kafka");
    this.kafkaTemplate.send("pubsub", "test1");
    String empurl = url+"/dep/" + id;
    log.info("RestTemplate for [{}]", empurl);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<Employee[]> restResponse = restTemplate.getForEntity(empurl,
        Employee[].class);
    List<Employee> employees = Arrays.stream(restResponse.getBody()).toList();

    return departmentRepo.findById(id).flatMap(dep -> Mono.just(new DepartmentDto(dep, employees)));
  }
}
