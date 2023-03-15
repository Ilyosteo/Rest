package com.example.ilyos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
public class RestControllerV1 {

    @ResponseBody
    @GetMapping("/hello")
    public String sayHello(){
        return "Hello Men";
    }

}
