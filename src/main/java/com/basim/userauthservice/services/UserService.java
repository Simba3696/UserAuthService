package com.basim.userauthservice.services;

import com.basim.userauthservice.models.User;
import com.basim.userauthservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements  IUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserById(Long id) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User with id " + id + " not found");
        }

        return optionalUser.get();

    }
}
