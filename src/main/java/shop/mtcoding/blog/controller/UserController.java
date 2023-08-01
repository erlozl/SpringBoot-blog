package shop.mtcoding.blog.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import shop.mtcoding.blog.dto.JoinDTO;
import shop.mtcoding.blog.dto.LoginDTO;
import shop.mtcoding.blog.model.User;
import shop.mtcoding.blog.repository.UserRepository;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired // Autowried를 사용한다는 것은 ioc컨테이너에서 꺼내준다는 것임
    private HttpSession session; // request는 가방, session은 서랍

    // @ResponseBody ResponseBody를 붙이면 데이터를 return함
    // @GetMapping("/test/login")
    // public String testLogin() {
    // User sessionUser = (User) session.getAttribute("sessionUser");
    // if (sessionUser == null) {
    // return "로그인이 되지 않았습니다";
    // } else {
    // return "로그인 됨 : " + sessionUser.getUsername();
    // }
    // }

    @PostMapping("/login")
    public String login(LoginDTO loginDTO) {
        if (loginDTO.getUsername() == null || loginDTO.getUsername().isEmpty()) {
            return "redirect:/40x";
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
            return "redirect:/40x";
        }
        // 핵심기능

        try {
            User user = userRepository.findByUsernameAndPassword(loginDTO);
            session.setAttribute("sessionUser", user);
            return "redirect:/";
        } catch (Exception e) {
            return "redirect:/exLogin";
        }
    }

    // 이게 진짜 방법 (실무 )
    @PostMapping("/join")
    public String join(JoinDTO joinDTO) {
        // 핵심기능

        // validation check(유효성 검사 - 반드시 해야함! - 포스트맨을 타고 온 공격자들을 막는 것임)
        // 공백이거나 값을 적지 않을 때 (view에서는 required로 막은 상태, 포스트맨으로 들어올 가능성이 있을 때 유효성 검사)
        if (joinDTO.getUsername() == null || joinDTO.getUsername().isEmpty()) {
            return "redirect:/40x";
            // @ResponseBody를 붙여서 return에 error라는 데이터 값을 보내도 되지만
            // 가독성을 좋게 하기 위해서 ErrorController에 페이지 연결해서 내주는 게 좋다
            // view ㅡ> controller ㅡ> repository
        }
        if (joinDTO.getPassword() == null || joinDTO.getPassword().isEmpty()) {
            return "redirect:/40x";
        }
        if (joinDTO.getEmail() == null || joinDTO.getEmail().isEmpty()) {
            return "redirect:/40x";
        }

        // 중복체크 예외처리
        try {
            userRepository.save(joinDTO); // 핵심 기능
        } catch (Exception e) {
            return "redirect:/50x";
        }
        return "redirect:/loginForm";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/user/updateForm")
    public String userUpdateForm() {
        return "user/updateForm";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // 세션 무효화 ( 내 서랍을 비우는 것)
        return "redirect:/";
    }

    // 스프링이 직접 파싱해줌 (정상인 )
    // @PostMapping("/join")
    // public String join(String username, String password, String email) {
    // System.out.println("username : " + username);
    // System.out.println("password : " + password);
    // System.out.println("email : " + email);
    // return "redirect:/loginForm";
    // }

    // @PostMapping("/join")
    // public String join(HttpServletRequest request) throws IOException {
    // // username=ssar&password=1234&email=ssar@nate.com 이게 들어와있음
    // BufferedReader br = request.getReader(); // 헤더는 여기에 안 담김

    // // 버퍼가 소비됨
    // String body = br.readLine();

    // // 버퍼에 값이 없어서 못 꺼냄
    // String username = request.getParameter("username");

    // System.out.println("body : " + body);
    // // body : username=ssar&email=ssar%40nate.com&password=1234
    // System.out.println("username : " + username);
    // // username : null
    // return "redirect:/loginForm";
    // }

    // DS(컨트롤러 메서드 찾기, 바디데이터 파싱)
    // DS가 바디 데이터를 파싱안하고 컨트롤러 메서드만 찾은 상황
    // 컨트롤러에서 파싱을 내가 한 것임

    // @PostMapping("/join")
    // public String join(HttpServletRequest request) {
    // String username = request.getParameter("username");
    // String email = request.getParameter("email");
    // String password = request.getParameter("password");
    // System.out.println("username : " + username);
    // System.out.println("email : " + email);
    // System.out.println("password : " + password);
    // return "redirect:/loginForm";
    // }

    // 원래 이렇게 받아야 함, getParameter(파싱해주는 메서드)
    // -> x-www-form-urlencoded
    // 디스패처서블릿(자바로 소켓통신을 구현해주는 객체(http의 헤더와 바디를 파싱해주는 메서드)),
    // ㅡ> 컨트롤러의 주소(메서드)를 보고 찾아주는 것, 여기까지가 스프링이 해주는 것

    // request만 적어주면 파싱안함
    // 파싱의 정의 - 구문분석
    // request객체 안에 있는 String username이렇게 한다면 request.username해서 넣어줌
    // 이렇게 주는 것은 내가 파싱하는 것임

    // 이렇게 HTTP요청에 담긴 데이터를 파싱하는 부분은 스프링이 자동으로 처리해줌

}
