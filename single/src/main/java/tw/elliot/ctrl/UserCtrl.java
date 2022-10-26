package tw.elliot.ctrl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.elliot.model.User;
import tw.elliot.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserCtrl {
	private final UserService userService;

	@GetMapping(path = "/{id}", produces = "application/json")
	public User findUserById(@PathVariable String id) {
		return userService.findUserById(id);
	}
}
