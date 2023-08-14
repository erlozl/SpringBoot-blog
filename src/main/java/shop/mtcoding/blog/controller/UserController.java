package shop.mtcoding.blog.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import shop.mtcoding.blog.dto.JoinDTO;
import shop.mtcoding.blog.dto.LoginDTO;
import shop.mtcoding.blog.dto.UserUpdateDTO;
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

    // localhost:8080/check?username = ssar
    // @ResponseBody
    // ajax통신 - 데이터만 줌
    @GetMapping("/check")
    public ResponseEntity<String> checkin(String username) {
        // ResponseEntity 는 ResponseBody를 안붙여도 데이터를 응답함
        // HttpServletResponse를 적어서 response.setStatus를 안 적어도 됨

        User user = userRepository.findByUsername(username);
        if (user != null) {
            return new ResponseEntity<String>("유저네임이 중복되었습니다", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("유저네임을 사용할 수 있습니다", HttpStatus.OK);
    }

    // (실무 AOP)
    @PostMapping("/join")
    public String hashJoin(JoinDTO joinDTO) {
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

        // jpa ( 기본 메서드 활용, 기술에 대한 원리 공부 )
        User user = userRepository.findByUsername(joinDTO.getUsername());

        // 에러에 대한 처리를 담당하는 클래스 하나 생성 -> 위임 -> 자바스크립트 변경
        if (user != null) {
            return "redirect:/50x";
        }

        String encPassword = BCrypt.hashpw(joinDTO.getPassword(), BCrypt.gensalt());
        joinDTO.setPassword(encPassword);

        userRepository.save(joinDTO); // 핵심 기능
        return "redirect:/loginForm";
    }

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
            User userHash = userRepository.findByUsername(loginDTO.getUsername());
            // User userHash = userRepository.findByUserId(loginDTO);
            boolean isValid = BCrypt.checkpw(loginDTO.getPassword(), userHash.getPassword());
            // user2.getPassword() = DB
            if (isValid == true) {
                session.setAttribute("sessionUser", userHash);
                return "redirect:/";
            } else {
                return "redirect:/loginForm";
            }

            // 로그인 정보 저장
            // 유저 정보가 맞으면 메인페이지로 가기
        } catch (Exception e) {
            return "redirect:/exLogin";
            // 유저 정보가 틀리다면 다시 로그인 페이지로
        }
    }

    @PostMapping("/user/update")
    public String userUpdate(UserUpdateDTO userUpdateDTO) {

        User user = (User) session.getAttribute("sessionUser");
        System.out.println("중복1 회원가입시 해시코드" + user.getPassword());

        if (user == null) {
            return "redirect:/loginForm"; // 401 (반드시 스스로 인증해야 함)
        }

        String hashPassword = BCrypt.hashpw(userUpdateDTO.getPassword(), BCrypt.gensalt());
        userUpdateDTO.setPassword(hashPassword);
        System.out.println("중복3 변경된 해시코드" + userUpdateDTO.getPassword());

        userRepository.update(userUpdateDTO, user.getId());
        System.out.println("중복3 최초등록" + userUpdateDTO.getPassword());

        // 세션 동기화
        return "redirect:/";
    }

    // id 사용하면 인증체크
    // 주소에 적은 값은 신뢰할 수 없다 - 포스트맨 때문에
    // 자기맘대로 주소를 넣을 수 있기 때문에
    // 세션값을 쓸때는 굳이 {id}를 안 써도 됨 , 권한체크도 X
    // @GetMapping("/user/{id}/updateForm")
    @GetMapping("/user/updateForm")
    public String userUpdateForm(HttpServletRequest request) {
        // User user = userRepository.findById(id);
        User sessionUser = (User) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        // return "redirect:/loginForm"; // 401 (반드시 스스로 인증해야 함)
        // }
        User user = userRepository.findByUsername(sessionUser.getUsername());
        request.setAttribute("user", user);
        // 유니크를 걸면 자동으로 인덱스가 걸린다 - 인덱스(목차)

        return "user/updateForm";
    }

    // @PostMapping("/login")
    // public String login(LoginDTO loginDTO) {
    // if (loginDTO.getUsername() == null || loginDTO.getUsername().isEmpty()) {
    // return "redirect:/40x";
    // }
    // if (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
    // return "redirect:/40x";
    // }
    // // 핵심기능

    // try {
    // User user = userRepository.findByUsernameAndPassword(loginDTO);
    // session.setAttribute("sessionUser", user);
    // // 로그인 정보 저장
    // return "redirect:/";
    // // 유저 정보가 맞으면 메인페이지로 가기
    // } catch (Exception e) {
    // return "redirect:/exLogin";
    // // 유저 정보가 틀리다면 다시 로그인 페이지로
    // }
    // }

    // // 이게 진짜 방법 (실무 )
    // @PostMapping("/join")
    // public String join(JoinDTO joinDTO) {
    // // 핵심기능

    // // validation check(유효성 검사 - 반드시 해야함! - 포스트맨을 타고 온 공격자들을 막는 것임)
    // // 공백이거나 값을 적지 않을 때 (view에서는 required로 막은 상태, 포스트맨으로 들어올 가능성이 있을 때 유효성 검사)
    // if (joinDTO.getUsername() == null || joinDTO.getUsername().isEmpty()) {
    // return "redirect:/40x";
    // // @ResponseBody를 붙여서 return에 error라는 데이터 값을 보내도 되지만
    // // 가독성을 좋게 하기 위해서 ErrorController에 페이지 연결해서 내주는 게 좋다
    // // view ㅡ> controller ㅡ> repository
    // }
    // if (joinDTO.getPassword() == null || joinDTO.getPassword().isEmpty()) {
    // return "redirect:/40x";
    // }
    // if (joinDTO.getEmail() == null || joinDTO.getEmail().isEmpty()) {
    // return "redirect:/40x";
    // }

    // // DB에 해당 username이 있는지 체크해보기
    // // 예외처리를 언제해봤나 ?
    // // 포스트맨에서 다이렉트로 연결요청이 왔을 때

    // User user = userRepository.findByUsername(joinDTO.getUsername());
    // if (user != null) {
    // return "redirect:/50x";
    // }
    // userRepository.save(joinDTO); // 핵심 기능
    // return "redirect:/loginForm";

    // }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    // @PostMapping("/user/{id}/update")
    // public String userUpdate(@PathVariable Integer id, UserUpdateDTO
    // userUpdateDTO) {
    // User user = userRepository.findById(id);
    // user = (User) session.getAttribute("sessionUser");
    // if (user == null) {
    // return "redirect:/loginForm"; // 401 (반드시 스스로 인증해야 함)
    // }
    // userRepository.update(userUpdateDTO, id);
    // return "redirect:/";
    // }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // 세션의 모든 데이터가 삭제, 무효화 ( 내 서랍을 비우는 것)
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
