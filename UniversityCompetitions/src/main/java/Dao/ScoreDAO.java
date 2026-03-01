package Dao;
import Entity.Score;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAO {

    public List<Score> listByCompetitionId(Integer compId) {
        List<Score> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM score_record WHERE competition_id=? ORDER BY `rank` ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, compId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Score score = new Score();
                score.setScoreId(rs.getInt("score_id"));
                score.setStudentId(rs.getString("student_id"));
                score.setStudentName(rs.getString("student_name"));
                score.setCompetitionId(rs.getInt("competition_id"));
                score.setScore(rs.getString("score"));
                score.setRank(rs.getInt("rank"));
                list.add(score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    public boolean saveOrUpdateScore(Score score) {
        if (getScoreByCompAndStu(score.getCompetitionId(), score.getStudentId()) != null) {
            return updateScore(score);
        } else {
            return addScore(score);
        }
    }

    private boolean addScore(Score score) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO score_record (competition_id, student_id, student_name, score, rank) " +
                    "VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, score.getCompetitionId());
            pstmt.setString(2, score.getStudentId());
            pstmt.setString(3, score.getStudentName());
            pstmt.setString(4, score.getScore());
            pstmt.setInt(5, score.getRank() == null ? 0 : score.getRank());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    private boolean updateScore(Score score) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE score_record SET score=?, rank=? " +
                    "WHERE competition_id=? AND student_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, score.getScore());
            pstmt.setInt(2, score.getRank() == null ? 0 : score.getRank());
            pstmt.setInt(3, score.getCompetitionId());
            pstmt.setString(4, score.getStudentId());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    private Score getScoreByCompAndStu(Integer compId, String stuId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Score score = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM score_record WHERE competition_id=? AND student_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, compId);
            pstmt.setString(2, stuId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                score = new Score();
                score.setScoreId(rs.getInt("score_id"));
                score.setStudentId(rs.getString("student_id"));
                score.setStudentName(rs.getString("student_name"));
                score.setCompetitionId(rs.getInt("competition_id"));
                score.setScore(rs.getString("score"));
                score.setRank(rs.getInt("rank"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return score;
    }

    public boolean deleteScore(Integer scoreId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM score_record WHERE score_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, scoreId);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public List<Score> listByStudentId(String studentId) {
        List<Score> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM score_record WHERE student_id=? ORDER BY competition_id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Score score = new Score();
                score.setScoreId(rs.getInt("score_id"));
                score.setStudentId(rs.getString("student_id"));
                score.setStudentName(rs.getString("student_name"));
                score.setCompetitionId(rs.getInt("competition_id"));
                score.setScore(rs.getString("score"));
                score.setRank(rs.getInt("rank"));
                list.add(score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
}