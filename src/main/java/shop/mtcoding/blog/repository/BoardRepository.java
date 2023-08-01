package shop.mtcoding.blog.repository;

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
        final int SIZE = 3;
        Query query = em.createNativeQuery("select * from board_tb order by id desc limit :page, :size", Board.class);
        query.setParameter("page", page * SIZE);
        query.setParameter("size", SIZE);

        return query.getResultList();
    }

    public List<Board> findCountAll() {
        Query query = em.createNativeQuery("select * from board_tb");
        List<Board> countAll = query.getResultList();
        return countAll;
    }

    public Board boardWriteById(Integer id) {
        Query query = em.createNativeQuery("select * from board_tb where id = :id", Board.class);
        query.setParameter("id", id);
        Board boardWriteById = (Board) query.getSingleResult();
        return boardWriteById;
    }
}
