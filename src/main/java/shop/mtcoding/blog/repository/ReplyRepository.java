package shop.mtcoding.blog.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.qlrm.mapper.JpaResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import shop.mtcoding.blog.dto.BoardDetailDTO;
import shop.mtcoding.blog.dto.ReplyWriteDTO;
import shop.mtcoding.blog.model.Reply;

@Repository
public class ReplyRepository {

    @Autowired
    private EntityManager em;

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
    public void save(ReplyWriteDTO replyWriteDTO, Integer userId) {
        Query query = em
                .createNativeQuery(
                        "insert into reply_tb (board_id,comment,user_id) values(:boardId,:comment, :userId)");
        query.setParameter("boardId", replyWriteDTO.getBoardId());
        query.setParameter("comment", replyWriteDTO.getComment());
        query.setParameter("userId", userId);
        query.executeUpdate(); // 쿼리 전송
    }

    // public Reply userJoinReplyId(Integer boardId) {
    // Query query = em.createNativeQuery("select username, comment from\r\n" + //
    // "user_tb as ut left outer join reply_tb as rt\r\n" + //
    // "on ut.id = rt.user_id\r\n" + //
    // "where board_id = :boardId", Reply.class);

    // query.setParameter("boardId", boardId);
    // Reply replyJoinList = (Reply) query.getSingleResult();
    // return replyJoinList;
    // }
}