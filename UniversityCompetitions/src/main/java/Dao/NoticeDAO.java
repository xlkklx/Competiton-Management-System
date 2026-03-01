package Dao;

import Entity.Notice;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    public List<Notice> listByCompetitionId(Integer compId) {
        List<Notice> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM notice_info WHERE competition_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, compId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Notice notice = new Notice();
                notice.setNoticeId(rs.getInt("notice_id"));
                notice.setCompetitionId(rs.getInt("competition_id"));
                notice.setTitle(rs.getString("title"));
                notice.setContent(rs.getString("content"));
                notice.setPublisher(rs.getString("publisher"));
                notice.setStatus(rs.getInt("status"));
                list.add(notice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    public boolean addNotice(Notice notice) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO notice_info (competition_id, title, content, publisher, status) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, notice.getCompetitionId());
            pstmt.setString(2, notice.getTitle());
            pstmt.setString(3, notice.getContent());
            pstmt.setString(4, notice.getPublisher());
            pstmt.setInt(5, notice.getStatus());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public boolean updateNotice(Notice notice) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE notice_info SET title=?, content=?, status=? WHERE notice_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, notice.getTitle());
            pstmt.setString(2, notice.getContent());
            pstmt.setInt(3, notice.getStatus());
            pstmt.setInt(4, notice.getNoticeId());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public boolean deleteNotice(Integer noticeId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM notice_info WHERE notice_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, noticeId);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public List<Notice> listAllNotices() {
        List<Notice> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM notice_info WHERE status=1 ORDER BY notice_id DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Notice notice = new Notice();
                notice.setNoticeId(rs.getInt("notice_id"));
                notice.setCompetitionId(rs.getInt("competition_id"));
                notice.setTitle(rs.getString("title"));
                notice.setContent(rs.getString("content"));
                notice.setPublisher(rs.getString("publisher"));
                notice.setStatus(rs.getInt("status"));
                list.add(notice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
}