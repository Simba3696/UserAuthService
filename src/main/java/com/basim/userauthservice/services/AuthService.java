package com.basim.userauthservice.services;

import com.basim.userauthservice.clients.KafkaProducerClient;
import com.basim.userauthservice.dtos.EmailDTO;
import com.basim.userauthservice.dtos.UserToken;
import com.basim.userauthservice.exceptions.InvalidCredentialsException;
import com.basim.userauthservice.exceptions.UserAlreadyExistsException;
import com.basim.userauthservice.exceptions.UserNotFoundException;
import com.basim.userauthservice.models.Role;
import com.basim.userauthservice.models.Session;
import com.basim.userauthservice.models.State;
import com.basim.userauthservice.models.User;
import com.basim.userauthservice.repositories.RoleRepository;
import com.basim.userauthservice.repositories.SessionRepository;
import com.basim.userauthservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SecretKey secretKey;

    @Autowired
    private KafkaProducerClient kafkaProducerClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public User signup(String name, String email, String password) throws UserAlreadyExistsException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("User with email already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        user.setState(State.ACTIVE);

        Role role = null;
        Optional<Role> optionalRole = roleRepository.findByValue("DEFAULT_ROLE");
        if (optionalRole.isEmpty()) {
            role = new Role();
            role.setValue("DEFAULT_ROLE");
            role.setCreatedAt(System.currentTimeMillis());
            role.setUpdatedAt(System.currentTimeMillis());
            role.setState(State.ACTIVE);
            roleRepository.save(role);
        } else {
            role = optionalRole.get();
        }

        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setSubject("Welcome to the platform");
        emailDTO.setBody("Welcome to the platform, " + name + ". Your account has been created successfully.");
        emailDTO.setFrom("basimrauf@gmail.com");
        emailDTO.setTo(email);

        try {
            kafkaProducerClient.sendMessage("email-topic", objectMapper.writeValueAsString(emailDTO));
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email" + e.getMessage());
        }


        return savedUser;
    }

    @Override
    public UserToken login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User with email not found");
        }
        User user = optionalUser.get();

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        Map<String, Object> payload = new HashMap<>();
        long currentTime = System.currentTimeMillis();
        payload.put("iat", currentTime);
        payload.put("exp", currentTime + 1000 * 60 * 60 * 24);
        payload.put("userId", user.getId());
        payload.put("iss", "user-auth-service");
        payload.put("scope", user.getRoles());

        String token = Jwts.builder().claims(payload).signWith(secretKey).compact();

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setCreatedAt(currentTime);
        session.setUpdatedAt(currentTime);
        session.setState(State.ACTIVE);
        sessionRepository.save(session);

        return new UserToken(user, token);
    }

    @Override
    public Boolean validateToken(String token) {

        Optional<Session> optionalSession = sessionRepository.findByToken(token);

        if (optionalSession.isEmpty()) {
            return false;
        }

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        long tokenExpiry = (Long) claims.get("exp");
        long currentTime = System.currentTimeMillis();

        if (tokenExpiry < currentTime) {
            return false;
        } else {
            return true;
        }
    }
}
