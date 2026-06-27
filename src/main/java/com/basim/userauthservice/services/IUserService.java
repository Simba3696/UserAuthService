package com.basim.userauthservice.services;

import com.basim.userauthservice.models.User;

public interface IUserService {

    User getUserById(Long id);
}
