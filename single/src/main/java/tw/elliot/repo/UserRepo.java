package tw.elliot.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tw.elliot.model.User;

@Repository
public interface UserRepo extends CrudRepository<User, String> {

}
