package shop.mtcoding.blog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

// User(1) - Reply(N)
// Board(1) - Reply(N)
// 1:1은 FK 어디 들어가도 상관없음

@Getter
@Setter
@Table(name = "reply_tb")
@Entity
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String comment; // 댓글 내용

    @JoinColumn(name = "user_id") // JoinColumn을 사용해서 네임 지정 가능
    @ManyToOne
    private User user; // FK , user_id
    @ManyToOne
    private Board board; // FK , board_id

}
