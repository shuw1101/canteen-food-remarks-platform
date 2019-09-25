package com.example.se.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class SettingController {
    @RequestMapping(path = {"/setting", "/about"})
    public String setting() {
        return "news";
    }
}
