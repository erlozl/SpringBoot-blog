package shop.mtcoding.blog.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * 글쓰기 API ( 프론트엔드 개발자한테 이 문서를 전해주면 됨 )
 * 1) 주소 , 전체는 URL => http://localhost:8080/board/save
 * 2) method : POST
 * 3) 요청(request) body : title=값(String) & content=값(String) 
 * 4) MIME타입 : x-www-form-urlencoded
 * 5) 응답 : view를 응답함. index페이지
 */
@Getter
@Setter
public class WriteDTO {
    private String title;
    private String content;
}
