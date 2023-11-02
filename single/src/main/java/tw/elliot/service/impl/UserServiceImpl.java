package tw.elliot.service.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tw.elliot.model.User;
import tw.elliot.repo.UserRepo;
import tw.elliot.service.UserService;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepo userRepo;

  @Override
  public User findUserById(String id) {
    return this.userRepo.findById(id).orElse(new User());
  }
}
