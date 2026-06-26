package com.basim.userauthservice.models;

import com.basim.userauthservice.dtos.UserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User extends  BaseModel{

    private String name;

    private String email;

    private String password;

    @ManyToMany
    private List<Role> roles = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public UserDTO toUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(getId());
        userDTO.setName(getName());
        userDTO.setEmail(getEmail());
        userDTO.setRoles(getRoles());
        return userDTO;
    }
}
