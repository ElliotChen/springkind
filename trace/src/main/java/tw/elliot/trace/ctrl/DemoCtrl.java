package tw.elliot.trace.ctrl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoCtrl {

  @GetMapping("/req1")
  public String request1() {
    log.error("!!!!!");
    return "Success";
  }
}
