package shop.mtcoding.blog.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.mtcoding.blog.dto.WriteDTO;
import shop.mtcoding.blog.model.Board;
import shop.mtcoding.blog.model.User;
import shop.mtcoding.blog.repository.BoardRepository;

// 테이블 하나당 mvc가 나옴

@Controller
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private HttpSession session;

    // 보통 메인은 주소를 두개 잡음
    // localhost:8080?page=1
    // localhost:8080 페이지로 그냥 요청하면 page의 값이 null이 뜸
    @GetMapping({ "/", "/board" })
    // 값이 안 들어오면 디폴트값으로 0이 들어간다는 뜻
    public String index(@RequestParam(defaultValue = "0") Integer page,
            HttpServletRequest request) {
        // 1. 유효성 검사 X
        // 2. 인증 검사 X

        final int PAGESIZE = 3;
        List<Board> boardList = boardRepository.findAll(page); // 1
        // List<Board> countAll = boardRepository.findCountAll();
        int totalCount = boardRepository.count(); // totalCount = 5
        int totalPage = totalCount / 3; // totalPage = 1
        if (totalCount % 3 > 0) {
            totalPage = totalPage + 1; // totalPage = 2
        }
        boolean last = totalPage - 1 == page;
        // int boardAllSize = countAll.size();
        System.out.println("테스트 boardList :" + boardList.size()); // 게시물 3개
        System.out.println("테스트 boardList :" + boardList.get(0).getTitle());
        // 데이터가 잘 들어갔는지 확인

        request.setAttribute("boardList", boardList);
        request.setAttribute("prevPage", page - 1);
        request.setAttribute("nextPage", page + 1);
        request.setAttribute("first", page == 0 ? true : false);
        // request.setAttribute("last", (boardAllSize / PAGESIZE) == page
        // || ((boardAllSize % PAGESIZE) == 0 && (boardAllSize / PAGESIZE) - 1 == page)
        // ? true : false);
        request.setAttribute("last", last);
        request.setAttribute("totalPage", totalPage);
        request.setAttribute("totalCount", totalCount);
        // 페이징 데이터라고 함
        return "index";
    }

    // 글쓰기는 본인만 할 수 있어야 함
    @GetMapping("/board/saveForm")
    public String saveForm() {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }
        return "board/saveForm";
    }

    @PostMapping("/board/save") // 포스트맨으로 바로 적을 수 있기 때문에 다시 인증체크 해야함
    public String save(WriteDTO writeDTO) {
        // validation check(유효성 검사)
        if (writeDTO.getTitle() == null || writeDTO.getContent().isEmpty()) {
            return "redirect:/40x";
        }
        if (writeDTO.getTitle() == null || writeDTO.getContent().isEmpty()) {
            return "redirect:/40x";
        }

        // 인증체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        boardRepository.save(writeDTO, sessionUser.getId());
        return "redirect:/";
    }

    @GetMapping("/board/{id}")
    public String detailPage(@PathVariable Integer id, HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        Board boardWriteById = boardRepository.boardWriteById(id);
        request.setAttribute("boardWriteById", boardWriteById);

        return "board/detail";
    }

}
