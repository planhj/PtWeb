package com.example.ptweb.controller.auth;

import com.example.ptweb.entity.User;
import com.example.ptweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/{id}")
    public String updateUser(@PathVariable long id, @RequestBody User user) {
        if (id != user.getId()) {
            return "User ID in path and body do not match.";
        }
        userService.updateUser(user);
        return "User updated successfully.";
    }
}
