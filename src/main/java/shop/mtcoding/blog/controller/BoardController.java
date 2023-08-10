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
import org.springframework.web.bind.annotation.ResponseBody;

import shop.mtcoding.blog.dto.BoardDetailDTO;
import shop.mtcoding.blog.dto.UpdateDTO;
import shop.mtcoding.blog.dto.WriteDTO;
import shop.mtcoding.blog.model.Board;
import shop.mtcoding.blog.model.Reply;
import shop.mtcoding.blog.model.User;
import shop.mtcoding.blog.repository.BoardRepository;
import shop.mtcoding.blog.repository.ReplyRepository;

// 테이블 하나당 mvc가 나옴

@Controller
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private HttpSession session;

    @ResponseBody
    @GetMapping("/test/count")
    public String testCount() {
        int count = boardRepository.count();
        return count + "";
    }
    // @ResponseBody
    // @GetMapping("/board/reply")
    // public List<Reply> test2() {
    // List<Reply> replys = replyRepository.findByBoardId(1);
    // return replys;
    // // object 리턴하면 디폴트값이 json 데이터로 리턴
    // }
    // 편해서 쓰는 로직,
    // 하지만 서비스가 느려질 때는 다른 걸로 바꿔야함

    @ResponseBody
    @GetMapping("/board/test/1")
    public Board test() {
        Board board = boardRepository.findById(1);
        return board;
        // object 리턴하면 디폴트값이 json 데이터로 리턴
    }

    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable Integer id, HttpServletRequest request) {
        // 1. 인증 검사
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm"; // 401 (반드시 스스로 인증해야 함)
        }
        // 2. 권한 체크
        Board board = boardRepository.findById(id);
        if (board.getUser().getId() != sessionUser.getId()) {
            return "redirect:/40x"; // 403 권한없음
        }

        // 3. 핵심 로직
        request.setAttribute("board", board);
        return "board/updateForm";
    }

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable Integer id, UpdateDTO updateDTO) {
        // 1. 인증 검사
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm"; // 401 (반드시 스스로 인증해야 함)
        }
        // 2. 권한 체크
        Board board = boardRepository.findById(id);
        if (board.getUser().getId() != sessionUser.getId()) {
            return "redirect:/40x"; // 403 권한없음
        }

        boardRepository.update(updateDTO, id);
        return "redirect:/board/" + id;
    }

    @PostMapping("/board/{id}/delete") // 1.PathVariable 값 받기
    public String delete(@PathVariable Integer id) {
        // 2. 인증검사 (로그인 페이지 보내기)
        // session에 접근해서 sessionUser 키값을 가져오세요
        // nul 이면, 로그인페이지로 보내고 null 아니면, 3번을 실행하세요
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm"; // 401 (반드시 스스로 인증해야 함)
        }
        // 3. 권한검사
        Board board = boardRepository.findById(id);
        if (board.getUser().getId() != sessionUser.getId()) {
            return "redirect:/40x"; // 403 권한없음
        }

        // 4. 모델에 접근해서 삭제
        // boardRepository.deleteById(id); 호출하세요 ㅡ> 리턴을 받지 마세요
        // delete from board_tb where id = :id

        boardRepository.deleteById(id);

        return "redirect:/";
    }

    // 보통 메인은 주소를 두개 잡음
    // localhost:8080?page=1
    // localhost:8080 페이지로 그냥 요청하면 page의 값이 null이 뜸
    @GetMapping({ "/", "/board" })
    // 값이 안 들어오면 디폴트값으로 0이 들어간다는 뜻
    // getMapping에서 들어오는 주소의 변수명은 쿼리스트링의 키 값
    public String index(
            @RequestParam(defaultValue = "") String keyword,
            // 검색 안했을 때 keyword = null
            @RequestParam(defaultValue = "0") Integer page,
            HttpServletRequest request) {
        // 1. 유효성 검사 X
        // 2. 인증 검사 X
        System.out.println("테스트 : keyword : " + keyword);
        System.out.println("테스트 : keyword length : " + keyword.length());
        System.out.println("테스트 : keyword isEmpty : " + keyword.isEmpty());
        System.out.println("테스트 : keyword isBlank : " + keyword.isBlank());

        List<Board> boardList = null;
        int totalCount = 0;
        request.setAttribute("keyword", keyword); // 공백 or 값 있음
        if (keyword.isBlank()) {
            boardList = boardRepository.findAll(page); // 1
            totalCount = boardRepository.count(); // totalCount = 5
        } else {
            boardList = boardRepository.findAll(page, keyword);
            System.out.println("테스트 페이지 " + boardList);

            totalCount = boardRepository.count(keyword);
            System.out.println("테스트 전체 게시물 수 " + totalCount);
        }

        // final int PAGESIZE = 3;
        // List<Board> countAll = boardRepository.findCountAll();
        int totalPage = totalCount / 3; // totalPage = 1
        if (totalCount % 3 > 0) {
            totalPage = totalPage + 1; // totalPage = 2
        }
        boolean last = totalPage - 1 == page;
        // int boardAllSize = countAll.size();
        // System.out.println("테스트 boardList :" + boardList.size()); // 게시물 3개
        // System.out.println("테스트 boardList :" + boardList.get(0).getTitle());
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
        if (writeDTO.getTitle() == null || writeDTO.getTitle().isEmpty()) {
            return "redirect:/40x";
        }
        if (writeDTO.getContent() == null || writeDTO.getContent().isEmpty()) {
            return "redirect:/40x";
        }

        // 인증체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        writeDTO.setTitle(writeDTO.getTitle().replaceAll("<", "&lt;"));
        writeDTO.setTitle(writeDTO.getTitle().replaceAll(">", "&gt;"));

        boardRepository.save(writeDTO, sessionUser.getId());
        boardRepository.save(writeDTO, 1);
        return "redirect:/";
    }

    @GetMapping("/board/{id}")
    public String detailPage(@PathVariable Integer id, HttpServletRequest request, Integer sessionUserid) { // C
        User sessionUser = (User) session.getAttribute("sessionUser"); // session접근하는 이유 - 권한 체크
        // Board board = boardRepository.findById(id); // M

        List<BoardDetailDTO> replyJoinAll = null;
        // 데이터베이스에서 조회를 했더니 담을 그릇이 없어서 DTO를 만듬

        if (sessionUser == null) {
            replyJoinAll = boardRepository.findByIdJoinReply(id, sessionUserid);
        } else {
            replyJoinAll = boardRepository.findByIdJoinReply(id, null);
        }

        boolean pageOwner = false;
        if (sessionUser != null) {
            System.out.println("테스트 세션 ID : " + sessionUser.getId());
            System.out.println("테스트 세션 board.getUser().getId() : " +
                    replyJoinAll.get(0).getBoardUserId());
            pageOwner = sessionUser.getId() == replyJoinAll.get(0).getBoardUserId();
            System.out.println("테스트 : pageOwner : " + pageOwner);
        }

        request.setAttribute("replyJoinAll", replyJoinAll);
        request.setAttribute("pageOwner", pageOwner);
        // true는 내가 적은 글, false는 남이 적은 글

        return "board/detail"; // V
    }

}
