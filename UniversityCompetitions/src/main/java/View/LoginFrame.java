package View;

import Entity.User;
import Entity.Student;
import Entity.Manager;
import Service.UserService;
import Service.StudentService;
import Service.ManagerService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JLabel l_name, l_type, l_password;
    private JTextField t_name;
    private JComboBox<String> c_type;
    private JPasswordField p_password;
    private JButton b_login, b_reset;

    private UserService userService = new UserService();
    private StudentService studentService = new StudentService();
    private ManagerService managerService = new ManagerService();

    public LoginFrame() {
        this.setSize(400, 300);
        this.setTitle("高校竞赛管理系统-登录界面");
        this.setLocation(300, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
        this.setVisible(true);
    }

    public void init() {
        l_name = new JLabel("账号ID", JLabel.CENTER);
        l_type = new JLabel("用户类型", JLabel.CENTER);
        l_password = new JLabel("密码", JLabel.CENTER);

        t_name = new JTextField();
        c_type = new JComboBox<String>();
        c_type.addItem("学生");
        c_type.addItem("赛事负责人");
        c_type.addItem("管理员");

        p_password = new JPasswordField();
        b_login = new JButton("登录");
        b_reset = new JButton("重置");

        b_reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                t_name.setText("");
                p_password.setText("");
                c_type.setSelectedIndex(0);
                t_name.requestFocus();
            }
        });

        b_login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = t_name.getText().trim();
                String password = new String(p_password.getPassword()).trim();
                String userType = (String) c_type.getSelectedItem();

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "用户名不能为空！", "提示", JOptionPane.PLAIN_MESSAGE);
                    t_name.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "密码不能为空！", "提示", JOptionPane.PLAIN_MESSAGE);
                    p_password.requestFocus();
                    return;
                }

                User loginUser = userService.login(username, password);
                if (loginUser == null) {
                    JOptionPane.showMessageDialog(null, "用户名或密码错误！", "提示", JOptionPane.PLAIN_MESSAGE);
                    return;
                }

                String role = "";
                if ("学生".equals(userType)) role = "student";
                else if ("赛事负责人".equals(userType)) role = "manager";
                else if ("管理员".equals(userType)) role = "admin";

                if (!role.equals(loginUser.getRole())) {
                    JOptionPane.showMessageDialog(null, "用户类型选择错误！", "提示", JOptionPane.PLAIN_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(null, "登录成功！欢迎" + loginUser.getName(), "提示", JOptionPane.PLAIN_MESSAGE);
                LoginFrame.this.dispose();

                if ("学生".equals(userType)) {
                    Student student = studentService.getStudentByStudentId(loginUser.getUsername());
                    new StudentMainFrame(student);
                } else if ("赛事负责人".equals(userType)) {
                    Manager manager = managerService.getManagerById(loginUser.getUsername());
                    new ManagerMainFrame(manager);
                } else if ("管理员".equals(userType)) {
                    new AdminMainFrame(loginUser);
                }
            }
        });

        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(3, 2, 5, 5));
        p1.add(l_name);
        p1.add(t_name);
        p1.add(l_type);
        p1.add(c_type);
        p1.add(l_password);
        p1.add(p_password);
        this.setLayout(null);
        p1.setBounds(5, 5, 380, 180);
        this.add(p1);
        JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(1, 2, 5, 5));
        p2.add(b_reset);
        p2.add(b_login);
        p2.setBounds(5, 190, 380, 65);
        this.add(p2);
    }
    public static void main(String[] args) {
        new LoginFrame();
    }
}