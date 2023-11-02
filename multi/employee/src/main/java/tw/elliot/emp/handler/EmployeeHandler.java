package tw.elliot.emp.handler;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tw.elliot.emp.entity.Employee;
import tw.elliot.emp.repo.EmployeeRepo;
import tw.elliot.emp.service.EmployeeService;

@Component
@RequiredArgsConstructor
public class EmployeeHandler {

  private final EmployeeService employeeService;

  public Mono<ServerResponse> findById(ServerRequest request) {
    return ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(employeeService.findById(request.pathVariable("id")), Employee.class);
  }

  public Mono<ServerResponse> findByIdList(ServerRequest request) {
    List ids = Arrays.asList(request.queryParam("ids").orElse("").split(","));

    return ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(employeeService.findByIdList(ids), Employee.class);
  }

  public Mono<ServerResponse> findByDepId(ServerRequest request) {
    return ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(employeeService.findByDepId(request.pathVariable("depId")), Employee.class);
  }
}
