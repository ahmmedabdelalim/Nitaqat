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



}
