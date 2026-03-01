package Service;

import Dao.ScoreDAO;
import Entity.Score;
import java.util.List;

public class ScoreService {
    private ScoreDAO scoreDAO = new ScoreDAO();

    public List<Score> listScoreByCompId(Integer compId) {
        if (compId == null || compId <= 0) {
            return List.of();
        }
        return scoreDAO.listByCompetitionId(compId);
    }

    public String saveOrUpdateScore(Score score) {
        if (score == null) {
            return "成绩数据不能为空！";
        }
        if (score.getCompetitionId() == null || score.getCompetitionId() <= 0) {
            return "无效的竞赛ID！";
        }
        if (score.getStudentId() == null || score.getStudentId().trim().isEmpty()) {
            return "学生学号不能为空！";
        }
        if (score.getStudentName() == null || score.getStudentName().trim().isEmpty()) {
            return "学生姓名不能为空！";
        }
        if (score.getScore() == null || score.getScore().trim().isEmpty()) {
            return "成绩不能为空！";
        }
        if (score.getRank() != null && score.getRank() < 0) {
            return "排名不能为负数！";
        }

        boolean success = scoreDAO.saveOrUpdateScore(score);
        return success ? "成绩保存成功！" : "成绩保存失败，请重试！";
    }

    public String deleteScore(Integer scoreId) {
        if (scoreId == null || scoreId <= 0) {
            return "无效的成绩ID！";
        }

        boolean success = scoreDAO.deleteScore(scoreId);
        return success ? "成绩删除成功！" : "成绩删除失败，请重试！";
    }
}