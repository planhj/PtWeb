// src/main/java/com/example/ptweb/dto/ChangeEmailRequestDTO.java
package com.example.ptweb.controller.auth.dto.request;

public class ChangeEmailRequestDTO {
    private String newEmail;

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}