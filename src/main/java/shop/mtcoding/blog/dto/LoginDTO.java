package shop.mtcoding.blog.dto;

import lombok.Getter;
import lombok.Setter;

// 로그인을 위한 DTO

/*
 * 로그인 API ( 프론트엔드 개발자한테 이 문서를 전해주면 됨 )
 * 1) 주소 , 전체는 URL => http://localhost:8080/login
 * 2) method : POST (로그인은 select지만 , post로 한다)
 * 3) 요청(request) body : username=값(String) & password=값(String) 
 * ㅡ> 전화번호를 날릴 때 String으로 날림 - 숫자로 날리면 값이 너무 크다 
 * 4) MIME타입 : x-www-form-urlencoded
 * 5) 응답 : view를 응답함. index페이지
 * Param은 쿼리스트링을 의미함 
 */

@Getter
@Setter
public class LoginDTO {
    private String username;
    private String password;
}
