package Dao;

import Entity.Manager;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagerDAO {

    public List<Manager> listAllManagers() {
        List<Manager> managerList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM competition_manager_info ORDER BY manager_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Manager manager = new Manager();
                manager.setManagerId(rs.getString("manager_id"));
                manager.setName(rs.getString("name"));
                manager.setPhone(rs.getString("phone"));
                manager.setEmail(rs.getString("email"));
                managerList.add(manager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return managerList;
    }

    public Manager getManagerById(String managerId) {
        Manager manager = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM competition_manager_info WHERE manager_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, managerId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                manager = new Manager();
                manager.setManagerId(rs.getString("manager_id"));
                manager.setName(rs.getString("name"));
                manager.setPhone(rs.getString("phone"));
                manager.setEmail(rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return manager;
    }

    public boolean addManager(Manager manager) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO competition_manager_info (manager_id, user_id, name, phone, email) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, manager.getManagerId());
            pstmt.setInt(2, manager.getUserId());
            pstmt.setString(3, manager.getName());
            pstmt.setString(4, manager.getPhone());
            pstmt.setString(5, manager.getEmail());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public boolean updateManager(Manager manager) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE competition_manager_info SET name=?, phone=?, email=? WHERE manager_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, manager.getName());
            pstmt.setString(2, manager.getPhone());
            pstmt.setString(3, manager.getEmail());
            pstmt.setString(4, manager.getManagerId());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public boolean deleteManager(String managerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM competition_manager_info WHERE manager_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, managerId);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

}