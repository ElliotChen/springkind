package tw.elliot.emp.conf;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import tw.elliot.emp.handler.EmployeeHandler;

@Configuration
public class RouterConfig {

  @Bean
  public RouterFunction<ServerResponse> routes(EmployeeHandler handler) {
    return nest(path("/func/employee"),
        nest(accept(MediaType.APPLICATION_JSON),
            route(GET("/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::findById)
            .andRoute(GET("/id/all").and(accept(MediaType.APPLICATION_JSON)), handler::findByIdList)
            .andRoute(GET("/dep/{depId}").and(accept(MediaType.APPLICATION_JSON)), handler::findByDepId))
        );
  }
}
