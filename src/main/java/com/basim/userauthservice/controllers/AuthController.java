package com.basim.userauthservice.controllers;


import com.basim.userauthservice.dtos.LoginRequestDTO;
import com.basim.userauthservice.dtos.SignupRequestDTO;
import com.basim.userauthservice.dtos.UserDTO;
import com.basim.userauthservice.dtos.UserToken;
import com.basim.userauthservice.exceptions.UnauthorizedException;
import com.basim.userauthservice.models.User;
import com.basim.userauthservice.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody SignupRequestDTO requestDTO) {
        try {
            User user = authService.signup(requestDTO.getName(), requestDTO.getEmail(), requestDTO.getPassword());
            UserDTO userDTO = user.toUserDTO();
            return ResponseEntity.ok().body(userDTO);
        } catch (Exception e) {
            throw new RuntimeException("Signup failed", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO requestDTO) {
        try {
            UserToken userToken = authService.login(requestDTO.getEmail(), requestDTO.getPassword());
            UserDTO userDTO = userToken.getUser().toUserDTO();
            String token = userToken.getToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            return ResponseEntity.ok().headers(headers).body(userDTO);
        } catch (Exception e) {
            throw new RuntimeException("Login failed", e);
        }
    }

    @PostMapping("/validate-token")
    public void validateToken(@RequestBody String token) {

        Boolean response = authService.validateToken(token);

        if (!response) {
            throw new UnauthorizedException("Invalid token");
        }

    }
}
