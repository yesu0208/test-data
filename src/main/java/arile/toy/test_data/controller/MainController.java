package arile.toy.test_data.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        return "index"; // 기본 페이지를 주로 index라 함.
    }
}
