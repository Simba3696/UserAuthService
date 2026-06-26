package com.basim.userauthservice.models;

import jakarta.persistence.Entity;

@Entity
public class Role extends BaseModel {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
