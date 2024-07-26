package org.main.hackerthon.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("v1/api")
public class LoginContorller {

    @GetMapping("/login")
    @ResponseBody
    public String root(){
        return "Hello World";
    }

    @GetMapping("/my")
    @ResponseBody
    public String myAPI() {
        return "my route";
    }

    @GetMapping("/route")
    @ResponseBody
    public String route(){
        return "route";
    }
}
