package com.github.jamorin.proxy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    /*
        Redirect to nzbget as there's nothing deployed at "/" currently.
     */
    @GetMapping
    public String index() {
        return "redirect:/nzbget";
    }
}
