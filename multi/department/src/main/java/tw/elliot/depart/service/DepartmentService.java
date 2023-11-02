package tw.elliot.depart.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tw.elliot.depart.dto.DepartmentDto;

@Service
public interface DepartmentService {
  Mono<DepartmentDto> findById(String id);
}
