package shop.mtcoding.blog.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "board_tb")
@Entity // ddl-auto가 create
public class Board {
    @Id
    // 정해놓은 데이터 베이스 전략대로 바뀜
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 화면을 봤을 때 무엇이 필요한지 확인하기
    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = true, length = 1000)
    private String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Timestamp createdAt; // 생성일

    @ManyToOne
    private User user;
}
