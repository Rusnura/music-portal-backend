package server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.helpers.JwtHelper;
import server.models.AuthRequest;
import server.models.User;
import server.services.DBUserDetailService;
import server.services.UserService;

@RestController
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DBUserDetailService dbUserDetailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtHelper jwtHelper;

    @PostMapping("/api/authenticate")
    public ResponseEntity<ObjectNode> authenticate(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        final UserDetails userDetails = dbUserDetailService.loadUserByUsername(authRequest.getUsername());
        ObjectNode tokenNode = objectMapper.createObjectNode();
        tokenNode.put("token", jwtHelper.generateToken(userDetails));
        return ResponseEntity.ok(tokenNode);
    }

    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (StringUtils.isEmpty(user.getUsername()) ||
            StringUtils.isEmpty(user.getPassword()) ||
            StringUtils.isEmpty(user.getName()) ||
            StringUtils.isEmpty(user.getLastname())) {
            return ResponseEntity.badRequest().build();
        }

        if (userService.isExistsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User newUser = new User();
        newUser.setName(user.getName());
        newUser.setLastname(user.getLastname());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userService.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
