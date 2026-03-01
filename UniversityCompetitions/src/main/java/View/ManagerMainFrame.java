package View;

import Entity.*;
import Service.CompetitionService;
import Service.EnrollmentService;
import Service.NoticeService;
import Service.ScoreService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManagerMainFrame extends JFrame {
    private JLabel l_welcome;
    private JButton b_audit, b_notice, b_score, b_exit;
    private JPanel mainPanel;

    private CompetitionService competitionService = new CompetitionService();
    private EnrollmentService enrollmentService = new EnrollmentService();
    private NoticeService noticeService = new NoticeService();
    private ScoreService scoreService = new ScoreService();

    private Manager loginManager;

    public ManagerMainFrame(Manager loginManager) {
        this.loginManager = loginManager;
        setTitle("高校竞赛管理系统-负责人端");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        initWelcomeArea();
        initFunctionButtons();
        initMainPanel();
        showAuditModule();
        setVisible(true);
    }

    private void initWelcomeArea() {
        l_welcome = new JLabel("您好，" + loginManager.getName() + "（赛事负责人）");
        l_welcome.setFont(new Font("宋体", Font.BOLD, 14));
        l_welcome.setBounds(20, 10, 400, 20);
        add(l_welcome);
    }

    private void initFunctionButtons() {
        b_audit = new JButton("报名审核");
        b_audit.setBounds(20, 40, 120, 30);
        add(b_audit);

        b_notice = new JButton("通知发布");
        b_notice.setBounds(20, 80, 120, 30);
        add(b_notice);

        b_score = new JButton("成绩维护");
        b_score.setBounds(20, 120, 120, 30);
        add(b_score);

        b_exit = new JButton("退出系统");
        b_exit.setBounds(20, 580, 120, 30);
        add(b_exit);

        b_audit.addActionListener(e -> showAuditModule());
        b_notice.addActionListener(e -> showNoticeModule());
        b_score.addActionListener(e -> showScoreModule());
        b_exit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "确定退出系统吗？", "确认", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });
    }

    private void initMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setBounds(150, 40, 780, 580);
        mainPanel.setLayout(null);
        add(mainPanel);
    }

    // ====================== 报名审核模块 ======================
    private void showAuditModule() {
        mainPanel.removeAll();
        mainPanel.repaint();

        JLabel l_comp = new JLabel("选择竞赛：");
        l_comp.setBounds(20, 10, 80, 30);
        List<Competition> myComps = getMyCompetitions();
        if (myComps.isEmpty()) {
            JOptionPane.showMessageDialog(this, "你暂无负责的竞赛！");
            return;
        }
        String[] compNames = new String[myComps.size()];
        Integer[] compIds = new Integer[myComps.size()];
        for (int i = 0; i < myComps.size(); i++) {
            compNames[i] = myComps.get(i).getName();
            compIds[i] = myComps.get(i).getCompetitionId();
        }
        JComboBox<String> cbComp = new JComboBox<>(compNames);
        cbComp.setBounds(100, 10, 200, 30);
        JButton b_refresh = new JButton("刷新");
        b_refresh.setBounds(320, 10, 80, 30);

        String[] auditColumn = {"报名ID", "学生学号", "学生姓名", "学院", "审核状态", "审核备注"};
        DefaultTableModel auditModel = new DefaultTableModel(auditColumn, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable auditTable = new JTable(auditModel);
        JScrollPane auditScroll = new JScrollPane(auditTable);
        auditScroll.setBounds(20, 50, 740, 400);

        JButton b_pass = new JButton("审核通过");
        b_pass.setBounds(100, 460, 100, 30);
        JButton b_refuse = new JButton("审核拒绝");
        b_refuse.setBounds(220, 460, 100, 30);

        mainPanel.add(l_comp);
        mainPanel.add(cbComp);
        mainPanel.add(b_refresh);
        mainPanel.add(auditScroll);
        mainPanel.add(b_pass);
        mainPanel.add(b_refuse);

        loadAuditData(auditModel, compIds[0]);

        cbComp.addActionListener(e -> {
            int idx = cbComp.getSelectedIndex();
            loadAuditData(auditModel, compIds[idx]);
        });

        b_refresh.addActionListener(e -> {
            int idx = cbComp.getSelectedIndex();
            loadAuditData(auditModel, compIds[idx]);
        });

        b_pass.addActionListener(e -> {
            int row = auditTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要审核的报名记录！");
                return;
            }
            int enrollId = (int) auditModel.getValueAt(row, 0);
            String remark = JOptionPane.showInputDialog(this, "请输入审核备注（选填）：", "审核通过", JOptionPane.PLAIN_MESSAGE);
            if (remark == null) return;

            boolean success = updateEnrollmentAuditStatus(enrollId, 1, remark == null ? "" : remark);
            if (success) {
                int idx = cbComp.getSelectedIndex();
                loadAuditData(auditModel, compIds[idx]);
                JOptionPane.showMessageDialog(this, "审核通过！");
            } else {
                JOptionPane.showMessageDialog(this, "审核失败，请重试！");
            }
        });

        b_refuse.addActionListener(e -> {
            int row = auditTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要审核的报名记录！");
                return;
            }
            int enrollId = (int) auditModel.getValueAt(row, 0);
            String remark = JOptionPane.showInputDialog(this, "请输入拒绝原因：", "审核拒绝", JOptionPane.PLAIN_MESSAGE);
            if (remark == null || remark.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "拒绝原因不能为空！");
                return;
            }

            boolean success = updateEnrollmentAuditStatus(enrollId, 2, remark);
            if (success) {
                int idx = cbComp.getSelectedIndex();
                loadAuditData(auditModel, compIds[idx]);
                JOptionPane.showMessageDialog(this, "审核拒绝！");
            } else {
                JOptionPane.showMessageDialog(this, "审核失败，请重试！");
            }
        });
    }

    private List<Competition> getMyCompetitions() {
        return competitionService.getCompetitionsByManagerId(loginManager.getManagerId());
    }

    private void loadAuditData(DefaultTableModel model, Integer compId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCompId(compId);
        model.setRowCount(0);

        for (Enrollment enrollment : enrollments) {
            String auditStatus = enrollment.getStatus();
            String rejectReason = enrollment.getRemark() != null ? enrollment.getRemark() : "";
            String studentName = enrollment.getStudentName();
            String studentCollege = enrollment.getStudentCollege();

            model.addRow(new Object[]{
                    enrollment.getEnrollmentId(),
                    enrollment.getStudentId(),
                    studentName,
                    studentCollege,
                    auditStatus,
                    rejectReason
            });
        }
    }

    private boolean updateEnrollmentAuditStatus(int enrollId, int status, String remark) {
        return enrollmentService.updateEnrollmentAuditStatus(enrollId, status, remark);
    }

    // ====================== 通知发布模块 ======================
    private void showNoticeModule() {
        mainPanel.removeAll();
        mainPanel.repaint();

        JLabel l_comp = new JLabel("选择竞赛：");
        l_comp.setBounds(20, 10, 80, 30);
        List<Competition> myComps = getMyCompetitions();
        if (myComps.isEmpty()) {
            JOptionPane.showMessageDialog(this, "你暂无负责的竞赛！");
            return;
        }
        String[] compNames = new String[myComps.size()];
        Integer[] compIds = new Integer[myComps.size()];
        for (int i = 0; i < myComps.size(); i++) {
            compNames[i] = myComps.get(i).getName();
            compIds[i] = myComps.get(i).getCompetitionId();
        }
        JComboBox<String> cbComp = new JComboBox<>(compNames);
        cbComp.setBounds(100, 10, 200, 30);
        JButton b_refresh = new JButton("刷新");
        b_refresh.setBounds(320, 10, 80, 30);

        String[] noticeColumn = {"通知ID", "标题","内容",  "状态"};
        DefaultTableModel noticeModel = new DefaultTableModel(noticeColumn, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable noticeTable = new JTable(noticeModel);
        JScrollPane noticeScroll = new JScrollPane(noticeTable);
        noticeScroll.setBounds(20, 50, 740, 350);

        JButton b_addNotice = new JButton("发布通知");
        b_addNotice.setBounds(80, 410, 100, 30);
        JButton b_editNotice = new JButton("修改通知");
        b_editNotice.setBounds(200, 410, 100, 30);
        JButton b_downNotice = new JButton("下架通知");
        b_downNotice.setBounds(320, 410, 100, 30);

        mainPanel.add(l_comp);
        mainPanel.add(cbComp);
        mainPanel.add(b_refresh);
        mainPanel.add(noticeScroll);
        mainPanel.add(b_addNotice);
        mainPanel.add(b_editNotice);
        mainPanel.add(b_downNotice);

        loadNoticeData(noticeModel, compIds[0]);

        cbComp.addActionListener(e -> {
            int idx = cbComp.getSelectedIndex();
            loadNoticeData(noticeModel, compIds[idx]);
        });

        b_refresh.addActionListener(e -> {
            int idx = cbComp.getSelectedIndex();
            loadNoticeData(noticeModel, compIds[idx]);
        });

        b_addNotice.addActionListener(e -> {
            int idx = cbComp.getSelectedIndex();
            Integer compId = compIds[idx];

            JTextField tfTitle = new JTextField();
            JTextArea taContent = new JTextArea(10, 30);
            taContent.setLineWrap(true);
            JScrollPane contentScroll = new JScrollPane(taContent);

            Object[] msg = {
                    "通知标题：", tfTitle,
                    "通知内容（时间、地点、注意事项）：", contentScroll
            };

            int option = JOptionPane.showConfirmDialog(this, msg, "发布竞赛通知", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            String title = tfTitle.getText().trim();
            String content = taContent.getText().trim();

            Notice notice = new Notice();
            notice.setCompetitionId(compId);
            notice.setTitle(title);
            notice.setContent(content);
            notice.setPublisher(loginManager.getManagerId());
            notice.setStatus(1);

            String result = noticeService.publishNotice(notice);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadNoticeData(noticeModel, compId);
            }
        });

        b_editNotice.addActionListener(e -> {
            int row = noticeTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要修改的通知！");
                return;
            }
            int noticeId = (int) noticeModel.getValueAt(row, 0);
            int idx = cbComp.getSelectedIndex();
            Integer compId = compIds[idx];
            List<Notice> notices = noticeService.listAllNoticeByCompId(compId);
            Notice target = null;
            for (Notice n : notices) {
                if (n.getNoticeId().equals(noticeId)) {
                    target = n;
                    break;
                }
            }
            if (target == null) {
                JOptionPane.showMessageDialog(this, "未找到该通知！");
                return;
            }

            JTextField tfTitle = new JTextField(target.getTitle());
            JTextArea taContent = new JTextArea(target.getContent(), 10, 30);
            taContent.setLineWrap(true);
            JScrollPane contentScroll = new JScrollPane(taContent);
            JComboBox<String> cbStatus = new JComboBox<>(new String[]{"上架", "下架"});
            cbStatus.setSelectedIndex(target.getStatus() == 1 ? 0 : 1);

            Object[] msg = {
                    "通知标题：", tfTitle,
                    "通知内容：", contentScroll,
                    "状态：", cbStatus
            };

            int option = JOptionPane.showConfirmDialog(this, msg, "修改通知", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            String title = tfTitle.getText().trim();
            String content = taContent.getText().trim();

            target.setTitle(title);
            target.setContent(content);
            target.setStatus(cbStatus.getSelectedIndex() == 0 ? 1 : 0);
            String result = noticeService.updateNotice(target);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadNoticeData(noticeModel, compId);
            }
        });

        b_downNotice.addActionListener(e -> {
            int row = noticeTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要下架的通知！");
                return;
            }
            int noticeId = (int) noticeModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "确定下架该通知吗？", "确认", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            int idx = cbComp.getSelectedIndex();
            Integer compId = compIds[idx];
            String result = noticeService.removeNotice(noticeId);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadNoticeData(noticeModel, compId);
            }
        });
    }

    private void loadNoticeData(DefaultTableModel model, Integer compId) {
        List<Notice> notices = noticeService.listAllNoticeByCompId(compId);
        model.setRowCount(0);
        for (Notice n : notices) {
            model.addRow(new Object[]{
                    n.getNoticeId(),
                    n.getTitle(),
                    n.getContent(),
                    n.getStatus() == 1 ? "上架" : "下架"
            });
        }
    }

    // ====================== 成绩维护模块 ======================
    private void showScoreModule() {
        mainPanel.removeAll();
        mainPanel.repaint();

        JLabel l_comp = new JLabel("选择竞赛：");
        l_comp.setBounds(20, 10, 80, 30);
        List<Competition> myComps = getMyCompetitions();
        if (myComps.isEmpty()) {
            JOptionPane.showMessageDialog(this, "你暂无负责的竞赛！");
            return;
        }
        String[] compNames = new String[myComps.size()];
        Integer[] compIds = new Integer[myComps.size()];
        for (int i = 0; i < myComps.size(); i++) {
            compNames[i] = myComps.get(i).getName();
            compIds[i] = myComps.get(i).getCompetitionId();
        }
        JComboBox<String> cbComp = new JComboBox<>(compNames);
        cbComp.setBounds(100, 10, 200, 30);
        JButton b_refresh = new JButton("刷新");
        b_refresh.setBounds(320, 10, 80, 30);

        String[] scoreColumn = {"成绩ID", "学生学号", "学生姓名", "成绩", "排名/奖项"};
        DefaultTableModel scoreModel = new DefaultTableModel(scoreColumn, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable scoreTable = new JTable(scoreModel);
        JScrollPane scoreScroll = new JScrollPane(scoreTable);
        scoreScroll.setBounds(20, 50, 740, 350);

        JButton b_addScore = new JButton("录入成绩");
        b_addScore.setBounds(80, 410, 100, 30);
        JButton b_editScore = new JButton("修改成绩");
        b_editScore.setBounds(200, 410, 100, 30);
        JButton b_delScore = new JButton("删除成绩");
        b_delScore.setBounds(320, 410, 100, 30);

        mainPanel.add(l_comp);
        mainPanel.add(cbComp);
        mainPanel.add(b_refresh);
        mainPanel.add(scoreScroll);
        mainPanel.add(b_addScore);
        mainPanel.add(b_editScore);
        mainPanel.add(b_delScore);

        loadScoreData(scoreModel, compIds[0]);

        // ====================== 事件绑定 ======================
        cbComp.addActionListener(e -> {
            int idx = cbComp.getSelectedIndex();
            loadScoreData(scoreModel, compIds[idx]);
        });

        b_refresh.addActionListener(e -> {
            int idx = cbComp.getSelectedIndex();
            loadScoreData(scoreModel, compIds[idx]);
        });

        b_addScore.addActionListener(e -> {
            int idx = cbComp.getSelectedIndex();
            Integer compId = compIds[idx];

            List<String[]> stuList = enrollmentService.getAuditedStudents(compId);
            if (stuList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "该竞赛暂无审核通过的学生！");
                return;
            }

            String[] stuOptions = new String[stuList.size()];
            for (int i = 0; i < stuList.size(); i++) {
                stuOptions[i] = stuList.get(i)[0] + " - " + stuList.get(i)[1];
            }
            JComboBox<String> cbStu = new JComboBox<>(stuOptions);

            JTextField tfScore = new JTextField();
            JTextField tfRank = new JTextField();

            Object[] msg = {
                    "选择学生：", cbStu,
                    "成绩：", tfScore,
                    "排名/奖项：", tfRank
            };

            int option = JOptionPane.showConfirmDialog(this, msg, "录入竞赛成绩", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            String stuInfo = (String) cbStu.getSelectedItem();
            String stuId = stuInfo.split(" - ")[0];
            String stuName = stuInfo.split(" - ")[1];

            String scoreStr = tfScore.getText().trim();
            if (scoreStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "成绩不能为空！");
                return;
            }

            String rankStr = tfRank.getText().trim();
            Integer rank = null;
            if (!rankStr.isEmpty()) {
                try {
                    rank = Integer.parseInt(rankStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "排名必须是数字！");
                    return;
                }
            } else {
                rank = 0;
            }

            Score scoreEntity = new Score();
            scoreEntity.setCompetitionId(compId);
            scoreEntity.setStudentId(stuId);
            scoreEntity.setStudentName(stuName);
            scoreEntity.setScore(scoreStr);
            scoreEntity.setRank(rank);

            String result = scoreService.saveOrUpdateScore(scoreEntity);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadScoreData(scoreModel, compId);
            }
        });

        b_editScore.addActionListener(e -> {
            int row = scoreTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要修改的成绩！");
                return;
            }
            int scoreId = (int) scoreModel.getValueAt(row, 0);
            String stuId = (String) scoreModel.getValueAt(row, 1);
            String stuName = (String) scoreModel.getValueAt(row, 2);
            int idx = cbComp.getSelectedIndex();
            Integer compId = compIds[idx];
            JTextField tfScore = new JTextField(scoreModel.getValueAt(row, 3).toString());
            JTextField tfRank = new JTextField(scoreModel.getValueAt(row, 4).toString());

            Object[] msg = {
                    "学生：", new JLabel(stuId + " - " + stuName),
                    "成绩：", tfScore,
                    "排名/奖项：", tfRank
            };

            int option = JOptionPane.showConfirmDialog(this, msg, "修改成绩", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION) return;

            String scoreStr = tfScore.getText().trim();
            if (scoreStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "成绩不能为空！");
                return;
            }

            String rankStr = tfRank.getText().trim();
            Integer rank = null;
            if (!rankStr.isEmpty()) {
                try {
                    rank = Integer.parseInt(rankStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "排名必须是数字！");
                    return;
                }
            } else {
                rank = 0;
            }

            Score scoreEntity = new Score();
            scoreEntity.setScoreId(scoreId);
            scoreEntity.setCompetitionId(compId);
            scoreEntity.setStudentId(stuId);
            scoreEntity.setStudentName(stuName);
            scoreEntity.setScore(scoreStr);
            scoreEntity.setRank(rank);

            String result = scoreService.saveOrUpdateScore(scoreEntity);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadScoreData(scoreModel, compId);
            }
        });

        b_delScore.addActionListener(e -> {
            int row = scoreTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "请选中要删除的成绩！");
                return;
            }
            int scoreId = (int) scoreModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "确定删除该成绩吗？", "确认", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            int idx = cbComp.getSelectedIndex();
            Integer compId = compIds[idx];
            String result = scoreService.deleteScore(scoreId);
            JOptionPane.showMessageDialog(this, result);
            if (result.contains("成功")) {
                loadScoreData(scoreModel, compId);
            }
        });
    }

    private void loadScoreData(DefaultTableModel model, Integer compId) {
        List<Score> scores = scoreService.listScoreByCompId(compId);
        model.setRowCount(0);
        for (Score s : scores) {
            model.addRow(new Object[]{
                    s.getScoreId(),
                    s.getStudentId(),
                    s.getStudentName(),
                    s.getScore(),
                    s.getRank()
            });
        }
    }
    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.setManagerId("manager01");
        manager.setName("张老师");
        new ManagerMainFrame(manager);
    }
}
