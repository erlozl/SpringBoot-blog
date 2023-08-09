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

    public Reply findById(int id) {
        Query query = em.createNativeQuery("select * from reply_tb where id = :id", Reply.class);
        query.setParameter("id", id);
        return (Reply) query.getSingleResult();
    }

    @Transactional
    public void deleteById(Integer id) {
        Query query = em.createNativeQuery("delete from reply_tb where id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

}