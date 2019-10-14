package server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.models.User;
import server.repositories.UserRepository;

@Service
public class UserService extends AbstractService<User> {
    @Autowired
    private UserRepository userRepo;

    public boolean isExistsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> new IllegalStateException("User with username ='" + username + "' not found!"));
    }
}
