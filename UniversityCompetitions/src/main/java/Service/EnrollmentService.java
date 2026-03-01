package Service;

import Dao.EnrollmentDAO;
import Dao.CompetitionDAO;
import Dao.StudentDAO;
import Dao.ScoreDAO;
import Entity.Enrollment;
import Entity.Competition;
import Entity.Student;
import Entity.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.sql.Timestamp;

public class EnrollmentService {
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private CompetitionDAO competitionDAO = new CompetitionDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private ScoreDAO scoreDAO = new ScoreDAO();

    public List<Enrollment> getEnrollmentsByCompId(int compId) {
        return enrollmentDAO.listByCompIdWithStudentInfo(compId);
    }

    public boolean updateEnrollmentAuditStatus(int enrollId, int status, String remark) {
        String statusStr = switch (status) {
            case 1 -> "通过";
            case 2 -> "拒绝";
            default -> "待审核";
        };
        return enrollmentDAO.updateAuditStatus(enrollId, statusStr, remark);
    }

    public List<String[]> getAuditedStudents(Integer compId) {
        return enrollmentDAO.listAuditedStudents(compId);
    }

    public boolean hasEnrollment(String studentId) {
        return enrollmentDAO.countByStudentId(studentId) > 0;
    }

    public String enrollCompetition(String studentId, int compId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return "报名失败：学生学号不能为空！";
        }
        if (compId <= 0) {
            return "报名失败：无效的竞赛ID（必须为正整数）！";
        }

        Student student = studentDAO.getStudentByStudentId(studentId);
        if (student == null) {
            return "报名失败：该学生不存在，请检查学号！";
        }

        Competition competition = competitionDAO.getCompetitionById(compId);
        if (competition == null) {
            return "报名失败：竞赛ID=" + compId + "的竞赛不存在！";
        }

        Date now = new Date();
        if (now.before(competition.getApplyStartTime())) {
            return "报名失败：报名尚未开始！开始时间：" + competition.getApplyStartTime();
        }
        if (now.after(competition.getApplyEndTime())) {
            return "报名失败：报名已结束！结束时间：" + competition.getApplyEndTime();
        }

        boolean isEnrolled = enrollmentDAO.checkEnrollment(studentId, compId);
        if (isEnrolled) {
            return "报名失败：你已报名该竞赛（ID=" + compId + "），不可重复报名！";
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCompetitionId(compId);
        enrollment.setEnrollTime(new Timestamp(now.getTime()));
        enrollment.setStatus("待审核");

        try {
            boolean success = enrollmentDAO.addEnrollment(enrollment);
            if (success) {
                return "报名成功！竞赛名称：" + competition.getName() + "，请等待赛事负责人审核。";
            } else {
                return "报名提交失败：数据库操作异常，请稍后重试！";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "报名失败：系统异常，原因：" + e.getMessage();
        }
    }

    public String getAuditProgressText(String status) {
        if (status == null || status.isEmpty()) {
            return "待审核";
        }
        switch (status) {
            case "待审核":
                return "待审核";
            case "通过":
                return "审核通过";
            case "不通过":
                return "审核不通过";
            default:
                return status;
        }
    }

    public List<Object[]> getEnrollmentProgress(String studentId) {
        List<Enrollment> enrollments = enrollmentDAO.listByStudentId(studentId);
        List<Score> scores = scoreDAO.listByStudentId(studentId);
        List<Object[]> progressData = new ArrayList<>();
        
        for (Enrollment enrollment : enrollments) {
            Competition competition = competitionDAO.getCompetitionById(enrollment.getCompetitionId());
            
            Score score = null;
            for (Score s : scores) {
                if (s.getCompetitionId() == enrollment.getCompetitionId()) {
                    score = s;
                    break;
                }
            }
            
            Object[] row = new Object[4];
            row[0] = competition.getCompetitionId();
            row[1] = competition.getName();
            row[2] = getAuditProgressText(enrollment.getStatus());
            row[3] = score != null ? score.getScore() : "未评分";
            
            progressData.add(row);
        }
        
        return progressData;
    }
}