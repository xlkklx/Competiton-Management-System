package Service;

import Dao.StudentDAO;
import Dao.UserDAO;
import Entity.Student;
import Entity.User;
import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private StudentDAO studentDAO = new StudentDAO();
    private UserDAO userDAO = new UserDAO();
    private EnrollmentService enrollmentService = new EnrollmentService();

    public List<Student> listAllStudents() {
        return studentDAO.listAllStudents();
    }

    public String addStudent(Student student) {
        if (student == null) {
            return "学生信息不能为空！";
        }
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            return "学号不能为空！";
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return "姓名不能为空！";
        }
        if (studentDAO.getStudentByStudentId(student.getStudentId()) != null) {
            return "该学号已存在！";
        }
        User user = new User();
        user.setUsername(student.getStudentId());
        user.setPassword("123456");
        user.setName(student.getName());
        user.setRole("student");
        user.setStatus(1);
        Integer userId = userDAO.addUser(user);
        if (userId == null) {
            return "用户账号创建失败！";
        }
        student.setUserId(userId);
        boolean success = studentDAO.addStudent(student);
        if (!success) {
            userDAO.deleteUser(student.getStudentId());
            return "学生添加失败！";
        }
        return "学生添加成功（默认密码：123456）！";
    }

    public List<Student> queryStudents(String id, String name) {
        List<Student> all = studentDAO.listAllStudents();
        List<Student> filter = new ArrayList<>();

        for (Student s : all) {
            boolean idMatch = id == null || id.isEmpty() || s.getStudentId().equals(id);
            boolean nameMatch = name == null || name.isEmpty() || s.getName().contains(name);
            if (idMatch && nameMatch) {
                filter.add(s);
            }
        }
        return filter;
    }

    public Student getStudentByStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return null;
        }
        return studentDAO.getStudentByStudentId(studentId);
    }

    public boolean updateStudent(Student student) {
        if (student == null || student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            return false;
        }
        if (getStudentByStudentId(student.getStudentId()) == null) {
            return false;
        }
        boolean success = studentDAO.updateStudent(student);
        if (success) {
            userDAO.updateUserName(student.getStudentId(), student.getName());
        }
        return success;
    }

    public boolean deleteStudent(String studentId) {
        if (enrollmentService.hasEnrollment(studentId)) {
            return false;
        }
        boolean success = studentDAO.deleteStudent(studentId);
        if (success) {
            userDAO.deleteUser(studentId);
        }
        return success;
    }
}