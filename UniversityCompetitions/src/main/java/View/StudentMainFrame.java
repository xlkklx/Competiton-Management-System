package View;

import Entity.Competition;
import Entity.Student;
import Entity.Notice;
import Service.StudentService;
import Service.CompetitionService;
import Service.EnrollmentService;
import Service.NoticeService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StudentMainFrame extends JFrame {
    private JLabel l_welcome, l_competitionId, l_competitionLevel;
    private JTextField t_competitionId;
    private JComboBox<String> c_competitionLevel;
    private JButton b_query, b_enroll, b_infoManage, b_refresh, b_exit, b_viewEnrollProgress, b_viewNotice;
    private DefaultTableModel tableModel;
    private JTable table_competition;
    private JScrollPane scrollPane;

    private Student loginStudent;
    private StudentService studentService = new StudentService();
    private CompetitionService competitionService = new CompetitionService();
    private EnrollmentService enrollmentService = new EnrollmentService();
    private NoticeService noticeService = new NoticeService();

    public StudentMainFrame(Student student) {
        this.loginStudent = student;
        setTitle("高校竞赛管理系统-学生端");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        initWelcomeArea();
        initQueryArea();
        initCompetitionTable();
        initFunctionButtons();
        addEventListeners();

        setVisible(true);
    }

    private void initWelcomeArea() {
        l_welcome = new JLabel("您好，" + loginStudent.getName() + "（学号：" + loginStudent.getStudentId() + "）");
        l_welcome.setFont(new Font("宋体", Font.BOLD, 14));
        l_welcome.setBounds(20, 10, 400, 20);
        add(l_welcome);
    }

    private void initQueryArea() {
        l_competitionId = new JLabel("竞赛ID：");
        l_competitionId.setBounds(20, 40, 60, 30);
        add(l_competitionId);

        t_competitionId = new JTextField();
        t_competitionId.setBounds(80, 40, 120, 30);
        add(t_competitionId);

        l_competitionLevel = new JLabel("竞赛级别：");
        l_competitionLevel.setBounds(220, 40, 60, 30);
        add(l_competitionLevel);

        c_competitionLevel = new JComboBox<>(new String[]{"-请选择-", "校级", "省级", "国家级"});
        c_competitionLevel.setBounds(280, 40, 120, 30);
        add(c_competitionLevel);

        b_query = new JButton("查询");
        b_query.setBounds(420, 40, 80, 30);
        add(b_query);

        b_refresh = new JButton("刷新列表");
        b_refresh.setBounds(520, 40, 100, 30);
        add(b_refresh);
    }

    private void initCompetitionTable() {
        String[] columnNames = {"竞赛ID", "竞赛名称", "竞赛级别", "报名开始时间", "报名结束时间", "参赛要求"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table_competition = new JTable(tableModel);
        table_competition.getColumnModel().getColumn(0).setPreferredWidth(60);
        table_competition.getColumnModel().getColumn(1).setPreferredWidth(180);
        table_competition.getColumnModel().getColumn(2).setPreferredWidth(80);
        table_competition.getColumnModel().getColumn(3).setPreferredWidth(120);
        table_competition.getColumnModel().getColumn(4).setPreferredWidth(120);
        table_competition.getColumnModel().getColumn(5).setPreferredWidth(200);
        scrollPane = new JScrollPane(table_competition);
        scrollPane.setBounds(20, 80, 740, 280);
        add(scrollPane);

        fillTableData(competitionService.listAvailableCompetitions());
    }

    private void initFunctionButtons() {
        b_enroll = new JButton("报名选中竞赛");
        b_enroll.setBounds(80, 370, 120, 30);
        add(b_enroll);

        b_infoManage = new JButton("个人信息管理");
        b_infoManage.setBounds(210, 370, 120, 30);
        add(b_infoManage);

        b_viewEnrollProgress = new JButton("报名进度及成绩");
        b_viewEnrollProgress.setBounds(340, 370, 120, 30);
        add(b_viewEnrollProgress);

        b_viewNotice = new JButton("查看比赛通知");
        b_viewNotice.setBounds(470, 370, 120, 30);
        add(b_viewNotice);

        b_exit = new JButton("退出系统");
        b_exit.setBounds(600, 370, 120, 30);
        add(b_exit);
    }

    private void addEventListeners() {
        b_query.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performQuery();
            }
        });

        b_refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                t_competitionId.setText("");
                c_competitionLevel.setSelectedIndex(0);
                fillTableData(competitionService.listAvailableCompetitions());
                JOptionPane.showMessageDialog(null, "竞赛列表已刷新！", "提示", JOptionPane.PLAIN_MESSAGE);
            }
        });

        b_enroll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enrollCompetition();
            }
        });

        b_infoManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StudentInfoManageFrame(loginStudent, studentService);
            }
        });

        b_viewEnrollProgress.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StudentEnrollProgressFrame(loginStudent, studentService);
            }
        });

        b_viewNotice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StudentCompetitionNoticeFrame(loginStudent);
            }
        });

        b_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "确定退出系统吗？", "确认", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    StudentMainFrame.this.dispose();
                    System.exit(0);
                }
            }
        });
    }

    private void fillTableData(List<Competition> competitions) {
        tableModel.setRowCount(0);
        for (Competition competition : competitions) {
            Object[] rowData = {
                    competition.getCompetitionId(),
                    competition.getName(),
                    competition.getLevel(),
                    competition.getApplyStartTime(),
                    competition.getApplyEndTime(),
                    competition.getRequirements()
            };
            tableModel.addRow(rowData);
        }
    }

    private void performQuery() {
        String competitionIdStr = t_competitionId.getText().trim();
        String selectedLevel = (String) c_competitionLevel.getSelectedItem();

        List<Competition> filteredCompetitions = competitionService.queryCompetitions(competitionIdStr, selectedLevel);

        if (filteredCompetitions == null) {
            JOptionPane.showMessageDialog(null, "竞赛ID必须是数字！", "输入错误", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        fillTableData(filteredCompetitions);
        JOptionPane.showMessageDialog(null, "查询完成，共找到" + filteredCompetitions.size() + "条竞赛！", "提示", JOptionPane.PLAIN_MESSAGE);
    }

    private void enrollCompetition() {
        int selectedRow = table_competition.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要报名的竞赛！", "提示", JOptionPane.PLAIN_MESSAGE);
            return;
        }

        int competitionId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String competitionName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确认报名【" + competitionName + "（ID：" + competitionId + "）】吗？",
                "确认报名",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String result = enrollmentService.enrollCompetition(loginStudent.getStudentId(), competitionId);
        System.out.println("报名结果详情：" + result);
        JOptionPane.showMessageDialog(this, result, "报名结果", JOptionPane.PLAIN_MESSAGE);
    }

    // ====================== 报名进度及成绩展示窗体 ======================
    class StudentEnrollProgressFrame extends JFrame {
        private Student student;
        private StudentService studentService;
        private DefaultTableModel progressTableModel;
        private JTable progressTable;
        private JScrollPane progressScrollPane;

        public StudentEnrollProgressFrame(Student student, StudentService studentService) {
            this.student = student;
            this.studentService = studentService;

            setTitle("我的报名进度及成绩");
            setSize(700, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(null);
            initProgressTable();
            loadProgressData();
            setVisible(true);
        }

        private void initProgressTable() {
            String[] columnNames = {"竞赛ID", "竞赛名称", "审核进度", "比赛成绩"};
            progressTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            progressTable = new JTable(progressTableModel);
            progressTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            progressTable.getColumnModel().getColumn(1).setPreferredWidth(200);
            progressTable.getColumnModel().getColumn(2).setPreferredWidth(100);
            progressTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            progressTable.setRowHeight(25);
            progressScrollPane = new JScrollPane(progressTable);
            progressScrollPane.setBounds(20, 20, 650, 320);
            add(progressScrollPane);
        }

        private void loadProgressData() {
            progressTableModel.setRowCount(0);

            List<Object[]> progressData = enrollmentService.getEnrollmentProgress(student.getStudentId());

            for (Object[] rowData : progressData) {
                progressTableModel.addRow(rowData);
            }
            JOptionPane.showMessageDialog(this, "共查询到" + progressData.size() + "条报名记录！", "提示", JOptionPane.PLAIN_MESSAGE);
        }
    }

    // ======================比赛通知展示窗体 ======================
    class StudentCompetitionNoticeFrame extends JFrame {
        private Student student;
        private DefaultTableModel noticeTableModel;
        private JTable noticeTable;
        private JScrollPane noticeScrollPane;
        private JButton b_close;
        private StudentService studentService;
        private java.util.List<Notice> noticeList;

        public StudentCompetitionNoticeFrame(Student student) {
            this.student = student;
            this.studentService = new StudentService();

            setTitle("比赛通知中心");
            setSize(750, 450);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(null);
            initNoticeTable();
            initNoticeButtons();
            loadNoticeData();
            addNoticeButtonListeners();
            setVisible(true);
        }

        private void initNoticeTable() {
            String[] columnNames = {"通知ID", "竞赛名称", "标题", "内容"};
            noticeTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            noticeTable = new JTable(noticeTableModel);
            noticeTable.getColumnModel().getColumn(0).setPreferredWidth(50);
            noticeTable.getColumnModel().getColumn(1).setPreferredWidth(200);
            noticeTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            noticeTable.getColumnModel().getColumn(3).setPreferredWidth(250);
            noticeTable.setRowHeight(25);
            noticeScrollPane = new JScrollPane(noticeTable);
            noticeScrollPane.setBounds(20, 20, 700, 320);
            add(noticeScrollPane);
        }

        private void initNoticeButtons() {
            b_close = new JButton("关闭");
            b_close.setBounds(290, 350, 150, 30);
            add(b_close);
        }

        private void loadNoticeData() {
            noticeTableModel.setRowCount(0);
            List<Object[]> noticeData = noticeService.getAllNoticesWithCompetitionName();
            for (Object[] row : noticeData) {
                noticeTableModel.addRow(row);
            }
        }

        private void addNoticeButtonListeners() {
            b_close.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StudentCompetitionNoticeFrame.this.dispose();
                }
            });
        }
    }

    //=====================学生个人信息管理窗体==================
    class StudentInfoManageFrame extends JFrame {
        private Student student;
        private StudentService studentService;
        private DefaultTableModel infoTableModel;
        private JTable infoTable;
        private JScrollPane infoScrollPane;
        private JButton b_modify, b_close;

        private final String[] EDITABLE_FIELDS = {"班级", "邮箱", "电话", "性别", "学院"};
        public StudentInfoManageFrame(Student student, StudentService studentService) {
            this.student = student;
            this.studentService = studentService;

            setTitle("个人信息管理");
            setSize(450, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(null);
            initInfoTable();
            initButtons();
            addButtonListeners();
            setVisible(true);
        }

        private void initInfoTable() {
            String[] columnNames = {"信息项", "当前值"};
            infoTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            fillInfoData();

            infoTable = new JTable(infoTableModel);
            infoTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            infoTable.getColumnModel().getColumn(1).setPreferredWidth(300);
            infoTable.setFont(new Font("宋体", Font.PLAIN, 14));
            infoTable.setRowHeight(25);
            infoScrollPane = new JScrollPane(infoTable);
            infoScrollPane.setBounds(20, 20, 400, 280);
            add(infoScrollPane);
        }

        private void fillInfoData() {
            infoTableModel.setRowCount(0);
            infoTableModel.addRow(new Object[]{"学号", student.getStudentId()});
            infoTableModel.addRow(new Object[]{"姓名", student.getName()});
            infoTableModel.addRow(new Object[]{"学院", student.getCollege()});
            infoTableModel.addRow(new Object[]{"班级", student.getClassName()});
            infoTableModel.addRow(new Object[]{"性别", student.getGender()});
            infoTableModel.addRow(new Object[]{"邮箱", student.getEmail()});
            infoTableModel.addRow(new Object[]{"电话", student.getPhone()});
        }

        private void initButtons() {
            b_modify = new JButton("修改选中信息");
            b_modify.setBounds(80, 310, 120, 30);
            add(b_modify);

            b_close = new JButton("关闭");
            b_close.setBounds(220, 310, 120, 30);
            add(b_close);
        }

        private void addButtonListeners() {
            b_modify.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    modifySelectedInfo();
                }
            });

            b_close.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StudentInfoManageFrame.this.dispose();
                }
            });
        }

        private void modifySelectedInfo() {
            int selectedRow = infoTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请先选中要修改的信息项！", "提示", JOptionPane.PLAIN_MESSAGE);
                return;
            }

            String infoName = (String) infoTableModel.getValueAt(selectedRow, 0);
            String currentValue = (String) infoTableModel.getValueAt(selectedRow, 1);

            boolean isEditable = false;
            for (String field : EDITABLE_FIELDS) {
                if (field.equals(infoName)) {
                    isEditable = true;
                    break;
                }
            }
            if (!isEditable) {
                JOptionPane.showMessageDialog(this, "【" + infoName + "】为不可修改字段！", "提示", JOptionPane.PLAIN_MESSAGE);
                return;
            }

            String newValue = JOptionPane.showInputDialog(
                    this,
                    "请输入【" + infoName + "】的新值（当前值：" + currentValue + "）：",
                    "修改信息",
                    JOptionPane.QUESTION_MESSAGE
            );
            if (newValue == null || newValue.trim().isEmpty()) {
                return;
            }
            newValue = newValue.trim();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确认将【" + infoName + "】从「" + currentValue + "」修改为「" + newValue + "」吗？",
                    "确认修改",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            switch (infoName) {
                case "班级":
                    student.setClassName(newValue);
                    break;
                case "邮箱":
                    student.setEmail(newValue);
                    break;
                case "电话":
                    student.setPhone(newValue);
                    break;
                case "性别":
                    student.setGender(newValue);
                    break;
                case "学院":
                    student.setCollege(newValue);
                    break;
            }

            boolean success = studentService.updateStudent(student);
            if (success) {
                infoTableModel.setValueAt(newValue, selectedRow, 1);
                JOptionPane.showMessageDialog(this, "【" + infoName + "】修改成功！", "成功", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "信息修改失败，请重试！", "失败", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }
    public static void main(String[] args) {
        Student student = new Student();
        student.setStudentId("2413040434");
        student.setName("孔令暄");
        new StudentMainFrame(student);
    }
}