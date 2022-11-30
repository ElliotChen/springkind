package tw.elliot.service;

import tw.elliot.model.User;

public interface UserService {

  User findUserById(String id);
}
