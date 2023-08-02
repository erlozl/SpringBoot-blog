package shop.mtcoding.blog.repository;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import shop.mtcoding.blog.dto.WriteDTO;
import shop.mtcoding.blog.model.Board;

@Repository
public class BoardRepository {
    @Autowired
    private EntityManager em;

    @Transactional
    public void save(WriteDTO writeDTO, Integer userId) {
        Query query = em
                .createNativeQuery(
                        "insert into board_tb (title,content,user_id,created_at) values(:title,:content,:userId, now())");
        query.setParameter("title", writeDTO.getTitle());
        query.setParameter("content", writeDTO.getContent());
        query.setParameter("userId", userId);
        query.executeUpdate();
    }

    // localhost:8080?page=0 , 페이징 쿼리라고 함
    public List<Board> findAll(int page) {
        // 페이징해서 조회 => limit, 최신글이 나와야 하기 때문에 order by desc 내림차순 ,
        // 여기서 page는 변수, 3은 상수는 대문자 (변하지 않는)
        // 0(1부터 시작하지 않음)은 limit의 시작 인덱스 번호, 3은 페이징 할 개수
        final int SIZE = 3; // 개수
        Query query = em.createNativeQuery("select * from board_tb order by id desc limit :page, :size", Board.class);
        // limit은 가장 마지막에 넣어야 한다
        query.setParameter("page", page * SIZE);
        query.setParameter("size", SIZE);

        return query.getResultList();
    }

    public List<Board> findCountAll() {
        Query query = em.createNativeQuery("select * from board_tb");
        List<Board> countAll = query.getResultList();
        return countAll;
    }

    // select id, title from board_tb
    // resultClass 안 붙이고 직접 파싱하려면
    // Object[]로 리턴됨
    // Object[0] = 1
    // Object[1] = 제목 1
    // @Entity(Board,User) 타입이 아니어도, 기본 자료형도 리턴 안됨
    public int count() {
        Query query = em.createNativeQuery("select count(*) from board_tb");
        // 원래는 Object 배열로 리턴 받는다. Object 배열은 칼럼의 연속
        // 그룹함수를 사용해서 하나의 컬럼을 조회하면, Object로 리턴된다
        BigInteger count = (BigInteger) query.getSingleResult();
        return count.intValue();
    } // 총 5개 리턴

    public Board boardWriteById(Integer id) {
        Query query = em.createNativeQuery("select * from board_tb where id = :id", Board.class);
        query.setParameter("id", id);
        Board boardWriteById = (Board) query.getSingleResult();
        return boardWriteById;
    }
}
