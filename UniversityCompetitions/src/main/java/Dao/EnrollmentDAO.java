package Dao;

import Entity.Enrollment;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    public boolean addEnrollment(Enrollment enrollment) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO enrollment_info (student_id, competition_id) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCompetitionId());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public List<Enrollment> listByStudentId(String studentId) {
        List<Enrollment> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM enrollment_info WHERE student_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Enrollment e = new Enrollment();
                e.setEnrollmentId(rs.getInt("enrollment_id"));
                e.setStudentId(rs.getString("student_id"));
                e.setCompetitionId(rs.getInt("competition_id"));
                e.setEnrollTime(rs.getTimestamp("enroll_time"));
                e.setStatus(rs.getString("status"));
                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    public int countByCompId(int compId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM enrollment_info WHERE competition_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, compId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return count;
    }

    public int countByStudentId(String studentId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM enrollment_info WHERE student_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return count;
    }

    public boolean checkEnrollment(String studentId, int compId) {
        String sql = "SELECT 1 FROM enrollment_info WHERE student_id=? AND competition_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setInt(2, compId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Enrollment> listByCompIdWithStudentInfo(int compId) {
        List<Enrollment> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT e.*, s.name, s.college FROM enrollment_info e " +
                    "LEFT JOIN student_info s ON e.student_id = s.student_id " +
                    "WHERE e.competition_id=? ORDER BY e.enroll_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, compId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
                enrollment.setStudentId(rs.getString("student_id"));
                enrollment.setCompetitionId(rs.getInt("competition_id"));
                enrollment.setEnrollTime(rs.getTimestamp("enroll_time"));
                enrollment.setStatus(rs.getString("status"));
                enrollment.setRemark(rs.getString("reject_reason"));
                enrollment.setStudentName(rs.getString("name"));
                enrollment.setStudentCollege(rs.getString("college"));
                list.add(enrollment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    public boolean updateAuditStatus(int enrollId, String status, String remark) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE enrollment_info SET status=?, reject_reason=? WHERE enrollment_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setString(2, remark);
            pstmt.setInt(3, enrollId);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public List<String[]> listAuditedStudents(int compId) {
        List<String[]> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT e.student_id, s.name FROM enrollment_info e " +
                    "LEFT JOIN student_info s ON e.student_id = s.student_id " +
                    "WHERE e.competition_id=? AND e.status='通过'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, compId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new String[]{rs.getString("student_id"), rs.getString("name")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
}