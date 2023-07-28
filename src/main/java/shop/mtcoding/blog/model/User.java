package shop.mtcoding.blog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "user_tb")
@Entity // ddl-auto가 create
public class User {
    @Id
    // 정해놓은 데이터 베이스 전략대로 바뀜
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 20, unique = true) // NOT NULL 제약 추가

    private String username;
    @Column(nullable = false, length = 20)

    private String password;
    @Column(nullable = false, length = 20)
    private String email;

}
