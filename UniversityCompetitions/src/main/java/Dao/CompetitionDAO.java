package Dao;

import Entity.Competition;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompetitionDAO {

    public List<Competition> listAllCompetitions() {
        List<Competition> compList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM competition_info ORDER BY competition_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Competition comp = new Competition();
                comp.setCompetitionId(rs.getInt("competition_id"));
                comp.setName(rs.getString("name"));
                comp.setIntroduction(rs.getString("introduction"));
                comp.setLevel(rs.getString("level"));
                comp.setManagerId(rs.getString("manager_id"));
                comp.setApplyStartTime(rs.getTimestamp("apply_start_time"));
                comp.setApplyEndTime(rs.getTimestamp("apply_end_time"));
                comp.setRequirements(rs.getString("requirements"));
                compList.add(comp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return compList;
    }

    public Competition getCompetitionById(int compId) {
        Competition comp = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM competition_info WHERE competition_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, compId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                comp = new Competition();
                comp.setCompetitionId(rs.getInt("competition_id"));
                comp.setName(rs.getString("name"));
                comp.setIntroduction(rs.getString("introduction"));
                comp.setLevel(rs.getString("level"));
                comp.setManagerId(rs.getString("manager_id"));
                comp.setApplyStartTime(rs.getTimestamp("apply_start_time"));
                comp.setApplyEndTime(rs.getTimestamp("apply_end_time"));
                comp.setRequirements(rs.getString("requirements"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return comp;
    }

    public boolean addCompetition(Competition comp) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO competition_info (name, introduction, level, manager_id, apply_start_time, apply_end_time, requirements) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, comp.getName());
            pstmt.setString(2, comp.getIntroduction());
            pstmt.setString(3, comp.getLevel());
            pstmt.setString(4, comp.getManagerId());
            pstmt.setTimestamp(5, new java.sql.Timestamp(comp.getApplyStartTime().getTime()));
            pstmt.setTimestamp(6, new java.sql.Timestamp(comp.getApplyEndTime().getTime()));
            pstmt.setString(7, comp.getRequirements());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public boolean updateCompetition(Competition comp) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE competition_info SET name=?, introduction=?, level=?, manager_id=?, apply_start_time=?, apply_end_time=?, requirements=? " +
                    "WHERE competition_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, comp.getName());
            pstmt.setString(2, comp.getIntroduction());
            pstmt.setString(3, comp.getLevel());
            pstmt.setString(4, comp.getManagerId());
            pstmt.setTimestamp(5, new java.sql.Timestamp(comp.getApplyStartTime().getTime()));
            pstmt.setTimestamp(6, new java.sql.Timestamp(comp.getApplyEndTime().getTime()));
            pstmt.setString(7, comp.getRequirements());
            pstmt.setInt(8, comp.getCompetitionId());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public boolean deleteCompetition(int compId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM competition_info WHERE competition_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, compId);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public boolean hasCompetitionByMgrId(String mgrId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean hasComp = false;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT 1 FROM competition_info WHERE manager_id = ? LIMIT 1";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, mgrId);
            rs = pstmt.executeQuery();
            hasComp = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return hasComp;
    }

    public List<Competition> listAvailableCompetitions() {
        List<Competition> compList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM competition_info WHERE apply_end_time > NOW() ORDER BY competition_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Competition comp = new Competition();
                comp.setCompetitionId(rs.getInt("competition_id"));
                comp.setName(rs.getString("name"));
                comp.setIntroduction(rs.getString("introduction"));
                comp.setLevel(rs.getString("level"));
                comp.setManagerId(rs.getString("manager_id"));
                comp.setApplyStartTime(rs.getTimestamp("apply_start_time"));
                comp.setApplyEndTime(rs.getTimestamp("apply_end_time"));
                comp.setRequirements(rs.getString("requirements"));
                compList.add(comp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return compList;
    }
}