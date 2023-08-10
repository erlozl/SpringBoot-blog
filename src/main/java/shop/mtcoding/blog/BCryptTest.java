package shop.mtcoding.blog;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptTest {

    public static void main(String[] args) {

        String encPassword = BCrypt.hashpw("1234", BCrypt.gensalt());
        // 1. "1234"라는 평문 비밀번호를 해싱
        // 2. BCrypt.gensalt() 함수는 솔트를 생성합니다.
        // ㅡ> 솔트는 해시 함수의 입력에 추가되어 해싱된 비밀번호의 보안을 강화
        // 3. BCrypt.hashpw("1234", salt) 함수는 생성된 솔트와 평문
        // 비밀번호를 입력으로 받아 해시된 비밀번호를 반환
        System.out.println("encPassword : " + encPassword);
        System.out.println(encPassword.length());

        boolean isValid = BCrypt.checkpw("12345", encPassword);
        System.out.println(isValid);

        // 솔트가 랜덤화시키기 때문에 매번 해쉬값이 다르다
    }
}
