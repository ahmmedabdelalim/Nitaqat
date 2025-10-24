package com.nitaqat.nitaqat.controller.web;

import com.nitaqat.nitaqat.entity.User;
import com.nitaqat.nitaqat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/edit-user")
    @ResponseBody
    public ResponseEntity<String> editUser(@RequestBody User updateUser)
    {
        User user = userRepository.findById(updateUser.getId())
                .orElseThrow(()->new RuntimeException("User not Found"));
        user.setName(updateUser.getName());
        user.setEmail(updateUser.getEmail());
        user.setRole(updateUser.getRole());
        user.setActive(updateUser.isActive());
        user.setUpload_active(updateUser.isUpload_active());
        user.setProfessions_active(updateUser.isProfessions_active());
        user.setActivity_active(updateUser.isActivity_active());

        userRepository.save(user);

        return ResponseEntity.ok("User updated successfully");
    }
}
