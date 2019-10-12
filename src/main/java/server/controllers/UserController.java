package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import server.services.UserService;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/api/user/register")
    public void register() { // TODO: Release this

    }
}
