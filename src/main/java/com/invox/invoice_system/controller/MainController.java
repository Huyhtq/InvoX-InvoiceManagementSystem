package com.invox.invoice_system.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/invox")
public class MainController {

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/invox/home";
    }

    @GetMapping("/home")
    public String homePage() {
        return "index";
    }

    @GetMapping("/loginPage")
    public String loginPage(){
        return "login";
    }
}
