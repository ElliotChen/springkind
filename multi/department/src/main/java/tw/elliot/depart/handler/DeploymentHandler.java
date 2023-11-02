package tw.elliot.depart.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tw.elliot.depart.service.DepartmentService;

@Component
@RequiredArgsConstructor
public class DeploymentHandler {

  private final DepartmentService departmentService;
}
