package com.basim.userauthservice.controllers;

import com.basim.userauthservice.dtos.UserDTO;
import com.basim.userauthservice.models.User;
import com.basim.userauthservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public UserDTO getUsersById(@PathVariable("id") Long id) {

        User user = userService.getUserById(id);

        return user.toUserDTO();
    }
}
