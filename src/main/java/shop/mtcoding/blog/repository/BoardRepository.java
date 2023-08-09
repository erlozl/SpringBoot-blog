package shop.mtcoding.blog.repository;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.qlrm.mapper.JpaResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import shop.mtcoding.blog.dto.BoardDetailDTO;
import shop.mtcoding.blog.dto.UpdateDTO;
import shop.mtcoding.blog.dto.WriteDTO;
import shop.mtcoding.blog.model.Board;

@Repository
public class BoardRepository {
    @Autowired
    private EntityManager em;

    // 동적쿼리 (상황에 따라 달라지는)
    // 쿼리안에서 처리하고 들고오는 게 좋음
    public List<BoardDetailDTO> findByIdJoinReply(Integer boardId, Integer sessionUserId) {
        String sql = "select ";
        sql += "b.id board_id, ";
        sql += "b.content board_content, ";
        sql += "b.title board_title, ";
        sql += "b.user_id board_user_id, ";
        sql += "r.id reply_id, ";
        sql += "r.comment reply_comment, ";
        sql += "r.user_id reply_user_id, ";
        sql += "ru.username reply_user_username, ";
        if (sessionUserId == null) {
            sql += "false reply_owner ";
        } else {
            sql += "case when r.user_id = :sessionUserId then true else false end reply_owner ";
        }

        sql += "from board_tb b left outer join reply_tb r ";
        sql += "on b.id = r.board_id ";
        sql += "left outer join user_tb ru ";
        sql += "on r.user_id = ru.id ";
        sql += "where b.id = :boardId ";
        sql += "order by r.id desc";
        Query query = em.createNativeQuery(sql);
        query.setParameter("boardId", boardId);
        if (sessionUserId != null) {
            query.setParameter("sessionUserId", sessionUserId);
        }

        JpaResultMapper mapper = new JpaResultMapper();
        List<BoardDetailDTO> dtos = mapper.list(query, BoardDetailDTO.class);
        return dtos;
    }

    // Query query = em.createNativeQuery("select * from reply_tb where board_id =
    // :boardId", Reply.class);
    // query.setParameter("boardId", boardId);
    // return query.getResultList();
    // jpa가 알아서 fk면 join해서 주면 됨
    // 단점 : 모든 게시판의 넘버가 나와서 가독성이 좋지 않고, select를 두번 하기 때문에 시간이 오래 걸릴 수 있다.
    //

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

    public Board findById(Integer id) {
        Query query = em.createNativeQuery("select * from board_tb where id = :id", Board.class);
        query.setParameter("id", id);
        Board boardWriteById = (Board) query.getSingleResult();
        // getSingle인 이유는 pk로 받았으니까
        return boardWriteById;
    }

    @Transactional
    public void deleteById(Integer id) {
        Query query = em.createNativeQuery("delete from board_tb where id= :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Transactional
    public void update(UpdateDTO updateDTO, Integer id) {
        Query query = em.createNativeQuery("update board_tb set title = :title, content = :content where id= :id");
        query.setParameter("id", id);
        query.setParameter("title", updateDTO.getTitle());
        query.setParameter("content", updateDTO.getContent());
        query.executeUpdate();
    }
}
