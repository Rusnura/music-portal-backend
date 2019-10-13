package server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.helpers.JwtHelper;
import server.services.DBUserDetailService;

@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DBUserDetailService dbUserDetailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtHelper jwtHelper;

    @PostMapping("/api/authenticate")
    public ResponseEntity<ObjectNode> authenticate(@RequestParam String username, @RequestParam String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        final UserDetails userDetails = dbUserDetailService.loadUserByUsername(username);
        ObjectNode tokenNode = objectMapper.createObjectNode();
        tokenNode.put("token", jwtHelper.generateToken(userDetails));
        return ResponseEntity.ok(tokenNode);
    }
}
