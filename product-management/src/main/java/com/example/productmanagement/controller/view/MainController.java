package com.example.productmanagement.controller.view;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

     @GetMapping({"/main", "/"})
    public String root() {
        return "redirect:/dashboard";
    }

   
}