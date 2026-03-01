package Dao;
import Entity.Student;
import Util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<Student> listAllStudents() {
        List<Student> stuList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM student_info ORDER BY student_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Student stu = new Student();
                stu.setStudentId(rs.getString("student_id"));
                stu.setName(rs.getString("name"));
                stu.setCollege(rs.getString("college"));
                stu.setClassName(rs.getString("class_name"));
                stu.setGender(rs.getString("gender"));
                stu.setEmail(rs.getString("email"));
                stu.setPhone(rs.getString("phone"));
                stuList.add(stu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return stuList;
    }

    public Student getStudentByStudentId(String stuId) {
        Student stu = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM student_info WHERE student_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stuId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                stu = new Student();
                stu.setStudentId(rs.getString("student_id"));
                stu.setName(rs.getString("name"));
                stu.setCollege(rs.getString("college"));
                stu.setClassName(rs.getString("class_name"));
                stu.setGender(rs.getString("gender"));
                stu.setEmail(rs.getString("email"));
                stu.setPhone(rs.getString("phone"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return stu;
    }

    public boolean addStudent(Student stu) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO student_info (student_id, user_id, name, college, class_name, gender, email, phone) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stu.getStudentId());
            pstmt.setInt(2, stu.getUserId());
            pstmt.setString(3, stu.getName());
            pstmt.setString(4, stu.getCollege());
            pstmt.setString(5, stu.getClassName());
            pstmt.setString(6, stu.getGender());
            pstmt.setString(7, stu.getEmail());
            pstmt.setString(8, stu.getPhone());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public boolean updateStudent(Student stu) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE student_info SET name=?, college=?, class_name=?, gender=?, email=?, phone=? " +
                    "WHERE student_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stu.getName());
            pstmt.setString(2, stu.getCollege());
            pstmt.setString(3, stu.getClassName());
            pstmt.setString(4, stu.getGender());
            pstmt.setString(5, stu.getEmail());
            pstmt.setString(6, stu.getPhone());
            pstmt.setString(7, stu.getStudentId());
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }

    public boolean deleteStudent(String stuId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM student_info WHERE student_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stuId);
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return result > 0;
    }
}