package shop.mtcoding.blog.repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import shop.mtcoding.blog.dto.JoinDTO;

// 지금까지 IoC컨테이너에 뜬 것들
// ㅡ> 내가 직접 띄운 것 : BoardController, UserController, UserRepository
// ㅡ> 스프링에서 띄운 것 : EntityManager, HttpSession
@Repository
public class UserRepository {
    // public UserRepository() {
    // System.out.println("테스트 : UserRepository()");
    // } ㅡ> Repository의 어노테이션을 붙이면 메모리에 떴다는 증거 (알아서 new해줌)

    @Autowired
    private EntityManager em;
    // EntityManager가 : 이렇게 바인딩하게 해줌

    @Transactional
    public void save(JoinDTO joinDTO) {
        Query query = em
                .createNativeQuery("insert into user_tb (username,password,email) values(:username,:password,:email)");
        query.setParameter("username", joinDTO.getUsername());
        query.setParameter("password", joinDTO.getPassword());
        query.setParameter("email", joinDTO.getEmail());
        query.executeUpdate();

    }

}
