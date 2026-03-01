package View;

import Entity.*;
import Service.CompetitionService;
import Service.ManagerService;
import Service.StudentService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Date;


public class AdminMainFrame extends JFrame {
    private JLabel l_welcome;
    private JButton b_competition, b_manager, b_student, b_exit;
    private JPanel mainPanel;

    private CompetitionService competitionService = new CompetitionService();
    private ManagerService managerService = new ManagerService();
    private StudentService studentService = new StudentService();

    private User loginAdmin;

    public AdminMainFrame(User loginAdmin) {
        this.loginAdmin = loginAdmin;
        setTitle("高校竞赛管理系统-管理员端");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        initWelcomeArea();
        initModuleButtons();
        initMainPanel();
        showCompetitionModule();
        setVisible(true);
    }

    private void initWelcomeArea() {
        l_welcome = new JLabel("您好，" + loginAdmin.getName() + "（管理员）");
        l_welcome.setFont(new Font("宋体", Font.BOLD, 14));
        l_welcome.setBounds(20, 10, 400, 20);
        add(l_welcome);
    }

    private void initModuleButtons() {
        b_competition = new JButton("竞赛信息管理");
        b_competition.setBounds(20, 40, 120, 30);
        add(b_competition);

        b_manager = new JButton("赛事负责人管理");
        b_manager.setBounds(20, 80, 120, 30);
        add(b_manager);

        b_student = new JButton("学生管理");
        b_student.setBounds(20, 120, 120, 30);
        add(b_student);

        b_exit = new JButton("退出系统");
        b_exit.setBounds(20, 520, 120, 30);
        add(b_exit);

        b_competition.addActionListener(e -> showCompetitionModule());
        b_manager.addActionListener(e -> showManagerModule());
        b_student.addActionListener(e -> showStudentModule());
        b_exit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "确定退出系统吗？", "确认", JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });
    }

    private void initMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setBounds(150, 40, 720, 520);
        mainPanel.setLayout(null);
        add(mainPanel);
    }

    // ====================== 竞赛信息模块 ======================
    private void showCompetitionModule() {
        mainPanel.removeAll();
        mainPanel.repaint();

        JLabel l_compId = new JLabel("竞赛ID：");
        l_compId.setBounds(20, 10, 60, 30);
        JTextField t_compId = new JTextField();
        t_compId.setBounds(80, 10, 100, 30);

        JLabel l_compLevel = new JLabel("竞赛级别：");
        l_compLevel.setBounds(200, 10, 60, 30);
        JComboBox<String> c_compLevel = new JComboBox<>(new String[]{"-全部-", "校级", "省级", "国家级"});
        c_compLevel.setBounds(260, 10, 100, 30);

        JButton b_queryComp = new JButton("查询");
        b_queryComp.setBounds(380, 10, 80, 30);
        JButton b_refreshComp = new JButton("刷新");
        b_refreshComp.setBounds(470, 10, 80, 30);

        String[] compColumn = {"竞赛ID", "竞赛名称", "竞赛介绍", "级别", "负责人", "报名开始时间", "报名结束时间", "参赛要求"};
        DefaultTableModel compModel = new DefaultTableModel(compColumn, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable compTable = new JTable(compModel);
        compTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        compTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        compTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        compTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        compTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        compTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        compTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        compTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        JScrollPane compScroll = new JScrollPane(compTable);
        compScroll.setBounds(20, 50, 680, 350);

        JButton b_addComp = new JButton("新增竞赛");
        b_addComp.setBounds(80, 410, 100, 30);
        JButton b_editComp = new JButton("修改竞赛");
        b_editComp.setBounds(200, 410, 100, 30);
        JButton b_delComp = new JButton("删除竞赛");
        b_delComp.setBounds(320, 410, 100, 30);

        mainPanel.add(l_compId);
        mainPanel.add(t_compId);
        mainPanel.add(l_compLevel);
        mainPanel.add(c_compLevel);
        mainPanel.add(b_queryComp);
        mainPanel.add(b_refreshComp);
        mainPanel.add(compScroll);
        mainPanel.add(b_addComp);
        mainPanel.add(b_editComp);
        mainPanel.add(b_delComp);

        loadCompetitionData(compModel);

        b_refreshComp.addActionListener(e -> {
            t_compId.setText("");
            c_compLevel.setSelectedIndex(0);
            loadCompetitionData(compModel);
        });

        b_queryComp.addActionListener(e -> {
            String idStr = t_compId.getText().trim();
            String level = (String) c_compLevel.getSelectedItem();

            List<Object[]> competitionData = competitionService.queryCompetitionsWithManagerName(idStr, level);

            compModel.setRowCount(0);
            for (Object[] row : competitionData) {
                compModel.addRow(row);
            }
            JOptionPane.showMessageDialog(this, "查询到" + competitionData.size() + "条竞赛");
        });

        b_addComp.addActionListener(e -> {
            List<Manager> managers = managerService.listAllManagers();
            if (managers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "暂无可用负责人，请先添加赛事负责人！");
                return;
            }
            String[] managerNames = new String[managers.size()];
            String[] managerIds = new String[managers.size()];
            for (int i = 0; i < managers.size(); i++) {
                managerNames[i] = managers.get(i).getName();
                managerIds[i] = managers.get(i).getManagerId();
            }
            JComboBox<String> cbManager = new JComboBox<>(managerNames);

            JTextField tfName = new JTextField();
            JTextArea taIntro = new JTextArea(3, 20);
            JScrollPane scrollIntro = new JScrollPane(taIntro);
            JComboBox<String> cbLevel = new JComboBox<>(new String[]{"校级", "省级", "国家级"});
            JTextField tfStartTime = new JTextField("2026-01-01 00:00:00");
            JTextField tfEndTime = new JTextField("2026-06-30 00:00:00");
            JTextField tfRequire = new JTextField();

            Object[] msg = {
                    "竞赛名称：", tfName,
                    "竞赛介绍：", scrollIntro,
                    "竞赛级别：", cbLevel,
                    "负责人：", cbManager,
                    "报名开始时间：", tfStartTime,
                    "报名结束时间：", tfEndTime,
                    "参赛要求：", tfRequire
            };

            int option = JOptionPane.showConfirmDialog(this, msg, "新增竞赛", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            Competition comp = new Competition();
            comp.setName(tfName.getText().trim());
            comp.setIntroduction(taIntro.getText().trim());
            comp.setLevel((String) cbLevel.getSelectedItem());
            comp.setManagerId(managerIds[cbManager.getSelectedIndex()]);
            comp.setApplyStartTime(new Date());
            comp.setApplyEndTime(new Date());
            comp.setRequirements(tfRequire.getText().trim());

            String result = competitionService.addCompetition(comp);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadCompetitionData(compModel);
            }
        });

        b_editComp.addActionListener(e -> {
            int row = compTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要修改的竞赛！");
                return;
            }
            int compId = (int) compModel.getValueAt(row, 0);
            Competition old = competitionService.getCompetitionById(compId);
            if (old == null) return;

            List<Manager> managers = managerService.listAllManagers();
            String[] managerNames = new String[managers.size()];
            String[] managerIds = new String[managers.size()];
            int selectMgrIdx = 0;
            for (int i = 0; i < managers.size(); i++) {
                managerNames[i] = managers.get(i).getName();
                managerIds[i] = managers.get(i).getManagerId();
                if (managerIds[i].equals(old.getManagerId())) selectMgrIdx = i;
            }
            JComboBox<String> cbManager = new JComboBox<>(managerNames);
            cbManager.setSelectedIndex(selectMgrIdx);

            JTextField tfName = new JTextField(old.getName());
            JTextArea taIntro = new JTextArea(old.getIntroduction(), 3, 20);
            JScrollPane scrollIntro = new JScrollPane(taIntro);
            JComboBox<String> cbLevel = new JComboBox<>(new String[]{"校级", "省级", "国家级"});
            cbLevel.setSelectedItem(old.getLevel());
            JTextField tfStartTime = new JTextField(old.getApplyStartTime().toString());
            JTextField tfEndTime = new JTextField(old.getApplyEndTime().toString());
            JTextField tfRequire = new JTextField(old.getRequirements());

            Object[] msg = {
                    "竞赛名称：", tfName,
                    "竞赛介绍：", scrollIntro,
                    "竞赛级别：", cbLevel,
                    "负责人：", cbManager,
                    "报名开始时间：", tfStartTime,
                    "报名结束时间：", tfEndTime,
                    "参赛要求：", tfRequire
            };

            int option = JOptionPane.showConfirmDialog(this, msg, "修改竞赛", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            old.setName(tfName.getText().trim());
            old.setIntroduction(taIntro.getText().trim());
            old.setLevel((String) cbLevel.getSelectedItem());
            old.setManagerId(managerIds[cbManager.getSelectedIndex()]);
            old.setRequirements(tfRequire.getText().trim());

            String result = competitionService.updateCompetition(old);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadCompetitionData(compModel);
            }
        });

        b_delComp.addActionListener(e -> {
            int row = compTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要删除的竞赛！");
                return;
            }
            int compId = (int) compModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "确定删除ID为" + compId + "的竞赛吗？", "删除确认", JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            String result = competitionService.deleteCompetition(compId);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadCompetitionData(compModel);
            }
        });
    }

    private void loadCompetitionData(DefaultTableModel model) {
        List<Object[]> competitionData = competitionService.getAllCompetitionsWithManagerName();
        model.setRowCount(0);
        for (Object[] row : competitionData) {
            model.addRow(row);
        }
    }
    // ====================== 赛事负责人模块 ======================
    private void showManagerModule() {
        mainPanel.removeAll();
        mainPanel.repaint();

        JLabel l_mgrId = new JLabel("负责人ID：");
        l_mgrId.setBounds(20, 10, 60, 30);
        JTextField t_mgrId = new JTextField();
        t_mgrId.setBounds(80, 10, 100, 30);

        JLabel l_mgrName = new JLabel("负责人姓名：");
        l_mgrName.setBounds(200, 10, 80, 30);
        JTextField t_mgrName = new JTextField();
        t_mgrName.setBounds(290, 10, 100, 30);

        JButton b_queryMgr = new JButton("查询");
        b_queryMgr.setBounds(410, 10, 80, 30);
        JButton b_refreshMgr = new JButton("刷新");
        b_refreshMgr.setBounds(500, 10, 80, 30);

        String[] mgrColumn = {"负责人ID", "姓名", "联系方式", "邮箱"};
        DefaultTableModel mgrModel = new DefaultTableModel(mgrColumn, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable mgrTable = new JTable(mgrModel);
        JScrollPane mgrScroll = new JScrollPane(mgrTable);
        mgrScroll.setBounds(20, 50, 680, 350);

        JButton b_addMgr = new JButton("新增负责人");
        b_addMgr.setBounds(80, 410, 100, 30);
        JButton b_editMgr = new JButton("修改负责人");
        b_editMgr.setBounds(200, 410, 100, 30);
        JButton b_delMgr = new JButton("删除负责人");
        b_delMgr.setBounds(320, 410, 100, 30);

        mainPanel.add(l_mgrId);
        mainPanel.add(t_mgrId);
        mainPanel.add(l_mgrName);
        mainPanel.add(t_mgrName);
        mainPanel.add(b_queryMgr);
        mainPanel.add(b_refreshMgr);
        mainPanel.add(mgrScroll);
        mainPanel.add(b_addMgr);
        mainPanel.add(b_editMgr);
        mainPanel.add(b_delMgr);

        loadManagerData(mgrModel);

        b_refreshMgr.addActionListener(e -> {
            t_mgrId.setText("");
            t_mgrName.setText("");
            loadManagerData(mgrModel);
        });

        b_queryMgr.addActionListener(e -> {
            String id = t_mgrId.getText().trim();
            String name = t_mgrName.getText().trim();

            List<Manager> filter = managerService.queryManagers(id, name);

            mgrModel.setRowCount(0);
            fillMgrTable(mgrModel, filter);
            JOptionPane.showMessageDialog(this, "查询到" + filter.size() + "位负责人");
        });

        b_addMgr.addActionListener(e -> {
            JTextField tfId = new JTextField();
            JTextField tfName = new JTextField();
            JTextField tfPhone = new JTextField();
            JTextField tfEmail = new JTextField();

            Object[] msg = {
                    "负责人ID：", tfId,
                    "姓名：", tfName,
                    "联系方式：", tfPhone,
                    "邮箱：",tfEmail
            };

            int option = JOptionPane.showConfirmDialog(this, msg, "新增负责人", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            Manager mgr = new Manager();
            mgr.setManagerId(tfId.getText().trim());
            mgr.setName(tfName.getText().trim());
            mgr.setPhone(tfPhone.getText().trim());
            mgr.setEmail(tfEmail.getText().trim());

            String result = managerService.addManager(mgr);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadManagerData(mgrModel);
            }
        });

        b_editMgr.addActionListener(e -> {
            int row = mgrTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要修改的负责人！");
                return;
            }
            String mgrId = (String) mgrModel.getValueAt(row, 0);
            Manager old = managerService.getManagerById(mgrId);
            if (old == null) return;

            JTextField tfName = new JTextField(old.getName());
            JTextField tfPhone = new JTextField(old.getPhone());
            JTextField tfEmail = new JTextField(old.getEmail());

            Object[] msg = {
                    "姓名：", tfName,
                    "联系方式：", tfPhone,
                    "邮箱：",tfEmail

            };

            int option = JOptionPane.showConfirmDialog(this, msg, "修改负责人", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            old.setName(tfName.getText().trim());
            old.setPhone(tfPhone.getText().trim());
            old.setEmail(tfEmail.getText().trim());

            String result = managerService.updateManager(old);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadManagerData(mgrModel);
            }
        });

        b_delMgr.addActionListener(e -> {
            int row = mgrTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要删除的负责人！");
                return;
            }
            String mgrId = (String) mgrModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "确定删除ID为" + mgrId + "的负责人吗？", "删除确认", JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            String result = managerService.deleteManager(mgrId, competitionService);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadManagerData(mgrModel);
            }
        });
    }

    private void loadManagerData(DefaultTableModel model) {
        List<Manager> list = managerService.listAllManagers();
        fillMgrTable(model, list);
    }

    private void fillMgrTable(DefaultTableModel model, List<Manager> list) {
        model.setRowCount(0);
        for (Manager m : list) {
            model.addRow(new Object[]{
                    m.getManagerId(),
                    m.getName(),
                    m.getPhone(),
                    m.getEmail()
            });
        }
    }

    // ====================== 学生模块 ======================
    private void showStudentModule() {
        mainPanel.removeAll();
        mainPanel.repaint();

        JLabel l_stuId = new JLabel("学号：");
        l_stuId.setBounds(20, 10, 60, 30);
        JTextField t_stuId = new JTextField();
        t_stuId.setBounds(80, 10, 100, 30);

        JLabel l_stuName = new JLabel("学生姓名：");
        l_stuName.setBounds(200, 10, 80, 30);
        JTextField t_stuName = new JTextField();
        t_stuName.setBounds(290, 10, 100, 30);

        JButton b_queryStu = new JButton("查询");
        b_queryStu.setBounds(410, 10, 80, 30);
        JButton b_refreshStu = new JButton("刷新");
        b_refreshStu.setBounds(500, 10, 80, 30);

        String[] stuColumn = {"学号", "姓名", "学院", "班级", "性别", "邮箱", "电话"};
        DefaultTableModel stuModel = new DefaultTableModel(stuColumn, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable stuTable = new JTable(stuModel);
        JScrollPane stuScroll = new JScrollPane(stuTable);
        stuScroll.setBounds(20, 50, 680, 350);

        JButton b_addStu = new JButton("新增学生");
        b_addStu.setBounds(60, 410, 100, 30);
        JButton b_editStu = new JButton("修改学生");
        b_editStu.setBounds(180, 410, 100, 30);
        JButton b_delStu = new JButton("删除学生");
        b_delStu.setBounds(300, 410, 100, 30);

        mainPanel.add(l_stuId);
        mainPanel.add(t_stuId);
        mainPanel.add(l_stuName);
        mainPanel.add(t_stuName);
        mainPanel.add(b_queryStu);
        mainPanel.add(b_refreshStu);
        mainPanel.add(stuScroll);
        mainPanel.add(b_addStu);
        mainPanel.add(b_editStu);
        mainPanel.add(b_delStu);

        loadStudentData(stuModel);

        b_refreshStu.addActionListener(e -> {
            t_stuId.setText("");
            t_stuName.setText("");
            loadStudentData(stuModel);
        });

        b_queryStu.addActionListener(e -> {
            String id = t_stuId.getText().trim();
            String name = t_stuName.getText().trim();

            List<Student> filter = studentService.queryStudents(id, name);

            stuModel.setRowCount(0);
            fillStuTable(stuModel, filter);
            JOptionPane.showMessageDialog(this, "查询到" + filter.size() + "名学生");
        });

        b_addStu.addActionListener(e -> {
            JTextField tfId = new JTextField();
            JTextField tfName = new JTextField();
            JTextField tfCollege = new JTextField();
            JTextField tfClass = new JTextField();
            JComboBox<String> cbGender = new JComboBox<>(new String[]{"男", "女"});
            JTextField tfEmail = new JTextField();
            JTextField tfPhone = new JTextField();

            Object[] msg = {
                    "学号：", tfId,
                    "姓名：", tfName,
                    "学院：", tfCollege,
                    "班级：", tfClass,
                    "性别：", cbGender,
                    "邮箱：", tfEmail,
                    "电话：", tfPhone
            };

            int option = JOptionPane.showConfirmDialog(this, msg, "新增学生", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            Student stu = new Student();
            stu.setStudentId(tfId.getText().trim());
            stu.setName(tfName.getText().trim());
            stu.setCollege(tfCollege.getText().trim());
            stu.setClassName(tfClass.getText().trim());
            stu.setGender((String) cbGender.getSelectedItem());
            stu.setEmail(tfEmail.getText().trim());
            stu.setPhone(tfPhone.getText().trim());

            String result = studentService.addStudent(stu);
            if (result.contains("成功")) {
                JOptionPane.showMessageDialog(this, result);
                loadStudentData(stuModel);
            } else {
                JOptionPane.showMessageDialog(this, result);
            }
        });

        b_editStu.addActionListener(e -> {
            int row = stuTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要修改的学生！");
                return;
            }
            String stuId = (String) stuModel.getValueAt(row, 0);
            Student old = studentService.getStudentByStudentId(stuId);
            if (old == null) return;

            JTextField tfName = new JTextField(old.getName());
            JTextField tfCollege = new JTextField(old.getCollege());
            JTextField tfClass = new JTextField(old.getClassName());
            JComboBox<String> cbGender = new JComboBox<>(new String[]{"男", "女"});
            cbGender.setSelectedItem(old.getGender());
            JTextField tfEmail = new JTextField(old.getEmail());
            JTextField tfPhone = new JTextField(old.getPhone());

            Object[] msg = {
                    "姓名：", tfName,
                    "学院：", tfCollege,
                    "班级：", tfClass,
                    "性别：", cbGender,
                    "邮箱：", tfEmail,
                    "电话：", tfPhone
            };

            int option = JOptionPane.showConfirmDialog(this, msg, "修改学生", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            old.setName(tfName.getText().trim());
            old.setCollege(tfCollege.getText().trim());
            old.setClassName(tfClass.getText().trim());
            old.setGender((String) cbGender.getSelectedItem());
            old.setEmail(tfEmail.getText().trim());
            old.setPhone(tfPhone.getText().trim());

            boolean success = studentService.updateStudent(old);
            if (success) {
                JOptionPane.showMessageDialog(this, "学生修改成功！");
                loadStudentData(stuModel);
            } else {
                JOptionPane.showMessageDialog(this, "学生修改失败！");
            }
        });

        b_delStu.addActionListener(e -> {
            int row = stuTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要删除的学生！");
                return;
            }
            String stuId = (String) stuModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "确定删除学号为" + stuId + "的学生吗？", "删除确认", JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            boolean success = studentService.deleteStudent(stuId);
            if (success) {
                JOptionPane.showMessageDialog(this, "学生删除成功！");
                loadStudentData(stuModel);
            } else {
                JOptionPane.showMessageDialog(this, "学生删除失败！");
            }
        });
    }

    private void loadStudentData(DefaultTableModel model) {
        List<Student> list = studentService.listAllStudents();
        fillStuTable(model, list);
    }

    private void fillStuTable(DefaultTableModel model, List<Student> list) {
        model.setRowCount(0);
        for (Student s : list) {
            model.addRow(new Object[]{
                    s.getStudentId(),
                    s.getName(),
                    s.getCollege(),
                    s.getClassName(),
                    s.getGender(),
                    s.getEmail(),
                    s.getPhone()
            });
        }
    }
    public static void main(String[] args) {
        User admin = new User();
        admin.setName("系统管理员");
        new AdminMainFrame(admin);
    }

}