package shop.mtcoding.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 테이블 하나당 mvc가 나옴

@Controller
public class BoardController {

    @GetMapping({ "/", "/board" })
    public String index() {

        return "index";
    }

    @GetMapping("/board/saveForm")
    public String boardForm() {

        return "board/saveForm";
    }

    @GetMapping("/board/{id}")
    public String detailPage() {
        return "board/detail";
    }
}
