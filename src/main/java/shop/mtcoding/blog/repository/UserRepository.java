package shop.mtcoding.blog.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import shop.mtcoding.blog.dto.JoinDTO;
import shop.mtcoding.blog.dto.LoginDTO;
import shop.mtcoding.blog.dto.UpdateDTO;
import shop.mtcoding.blog.dto.UserUpdateDTO;
import shop.mtcoding.blog.model.User;

// 지금까지 IoC컨테이너에 뜬 것들
// ㅡ> 내가 직접 띄운 것 : BoardController, UserController, UserRepository
// ㅡ> 스프링에서 띄운 것 : EntityManager, HttpSession
@Repository
public class UserRepository {
    // public UserRepository() {
    // System.out.println("테스트 : UserRepository()");
    // } ㅡ> Repository의 어노테이션을 붙이면 메모리에 떴다는 증거 (알아서 new해줌)

    // EntityManager가 : 이렇게 바인딩하게 해줌
    @Autowired
    private EntityManager em;

    // 안전한 코드 짜기 위함 - 알고 있는 것을 예방하는 것
    public User findByUsername(String username) {
        try {
            Query query = em.createNativeQuery("select * from user_tb where username = :username",
                    User.class);
            query.setParameter("username", username);
            return (User) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public User findByUsernameAndPassword(LoginDTO loginDTO) {
        Query query = em.createNativeQuery("select * from user_tb where username = :username and password = :password",
                User.class);
        query.setParameter("username", loginDTO.getUsername());
        query.setParameter("password", loginDTO.getPassword());

        return (User) query.getSingleResult();

    }

    // 일의 최소 단위 (상대적임) - 트랜잭션
    // 다 진행됐으면 저장 (커밋) / 하나라도 틀렸다면 되돌아가기 (롤백)

    @Transactional
    // 이걸 붙여야 고립성을 피할 수 있음
    // 트랜잭션이 안 붙으면? 동시에 무언가가 일어나기 때문에
    // 트랜잭션 걸려있으면 write 못함
    // ㅡ> 다른 작업(트랜잭션)이 해당 자원을 사용할 수 없다는 것을 의미
    // 고립성 - 독립적임
    // 트랜잭션이 안 걸려있다면 모두 다 같이 예매 가능,,개판
    public void save(JoinDTO joinDTO) {
        System.out.println("테스트 : 1");
        Query query = em
                .createNativeQuery("insert into user_tb (username,password,email) values(:username,:password,:email)");
        System.out.println("테스트 : 2");
        query.setParameter("username", joinDTO.getUsername());
        query.setParameter("password", joinDTO.getPassword());
        query.setParameter("email", joinDTO.getEmail());
        System.out.println("테스트 : 3");
        query.executeUpdate(); // 쿼리를 전송 ( DBMS )
        System.out.println("테스트 : 4");
    }

    @Transactional
    public void update(UserUpdateDTO userUpdateDTO, Integer id) {
        Query query = em.createNativeQuery(
                "update user_tb set password = :password where id = :id");
        query.setParameter("password", userUpdateDTO.getPassword());
        query.setParameter("id", id);
        query.executeUpdate();
    }

    public User findById(Integer id) {
        Query query = em.createNativeQuery("select * from user_tb where id= :id", User.class);
        query.setParameter("id", id);
        User userList = (User) query.getSingleResult();
        return userList;
    }
}