package com.basim.userauthservice.services;

import com.basim.userauthservice.dtos.UserToken;
import com.basim.userauthservice.exceptions.UserAlreadyExistsException;
import com.basim.userauthservice.models.User;

public interface IAuthService {

    User signup(String name, String email, String password) throws UserAlreadyExistsException;

    UserToken login(String email, String password);

    Boolean validateToken(String token);
}
