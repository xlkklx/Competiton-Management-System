package Service;

import Dao.CompetitionDAO;
import Dao.ManagerDAO;
import Dao.EnrollmentDAO;
import Entity.Competition;
import Entity.Manager;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class CompetitionService {
    private CompetitionDAO competitionDAO = new CompetitionDAO();
    private ManagerDAO managerDAO = new ManagerDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public Competition getCompetitionById(int compId) {
        return competitionDAO.getCompetitionById(compId);
    }

    public String addCompetition(Competition comp) {
        if (comp.getName() == null || comp.getName().trim().isEmpty()) {
            return "竞赛名称不能为空！";
        }
        Date startTime = comp.getApplyStartTime();
        Date endTime = comp.getApplyEndTime();
        if (startTime == null || endTime == null || endTime.before(startTime)) {
            return "报名结束时间必须晚于开始时间！";
        }
        boolean success = competitionDAO.addCompetition(comp);
        return success ? "竞赛新增成功！" : "竞赛新增失败，请重试！";
    }

    public String updateCompetition(Competition comp) {
        if (comp.getCompetitionId() <= 0) {
            return "无效的竞赛ID！";
        }
        Date startTime = comp.getApplyStartTime();
        Date endTime = comp.getApplyEndTime();
        if (startTime != null && endTime != null && endTime.before(startTime)) {
            return "报名结束时间必须晚于开始时间！";
        }
        boolean success = competitionDAO.updateCompetition(comp);
        return success ? "竞赛修改成功！" : "竞赛修改失败，请重试！";
    }

    public String deleteCompetition(int compId) {
        if (enrollmentDAO.countByCompId(compId) > 0) {
            return "该竞赛已有学生报名，无法删除！";
        }
        boolean success = competitionDAO.deleteCompetition(compId);
        return success ? "竞赛删除成功！" : "竞赛删除失败，请重试！";
    }

    public boolean hasCompetitionByMgrId(String mgrId) {
        if (mgrId == null || mgrId.trim().isEmpty()) {
            return false;
        }
        return competitionDAO.hasCompetitionByMgrId(mgrId);
    }

    public List<Object[]> queryCompetitionsWithManagerName(String idStr, String level) {
        List<Competition> all = competitionDAO.listAllCompetitions();
        List<Object[]> competitionData = new ArrayList<>();

        for (Competition c : all) {
            boolean idMatch = idStr == null || idStr.isEmpty() || c.getCompetitionId() == Integer.parseInt(idStr);
            boolean levelMatch = level == null || "-全部-".equals(level) || c.getLevel().equals(level);
            if (idMatch && levelMatch) {
                Manager m = managerDAO.getManagerById(c.getManagerId());
                String mgrName = m == null ? "未知" : m.getName();
                
                Object[] row = new Object[8];
                row[0] = c.getCompetitionId();
                row[1] = c.getName();
                row[2] = c.getIntroduction();
                row[3] = c.getLevel();
                row[4] = mgrName;
                row[5] = c.getApplyStartTime();
                row[6] = c.getApplyEndTime();
                row[7] = c.getRequirements();
                
                competitionData.add(row);
            }
        }
        return competitionData;
    }

    public List<Competition> getCompetitionsByManagerId(String managerId) {
        List<Competition> all = competitionDAO.listAllCompetitions();
        List<Competition> my = new ArrayList<>();
        for (Competition c : all) {
            if (managerId.equals(c.getManagerId())) {
                my.add(c);
            }
        }
        return my;
    }

    public List<Object[]> getAllCompetitionsWithManagerName() {
        List<Competition> competitions = competitionDAO.listAllCompetitions();
        List<Object[]> competitionData = new ArrayList<>();
        
        for (Competition c : competitions) {
            Manager m = managerDAO.getManagerById(c.getManagerId());
            String mgrName = m == null ? "未知" : m.getName();
            
            Object[] row = new Object[8];
            row[0] = c.getCompetitionId();
            row[1] = c.getName();
            row[2] = c.getIntroduction();
            row[3] = c.getLevel();
            row[4] = mgrName;
            row[5] = c.getApplyStartTime();
            row[6] = c.getApplyEndTime();
            row[7] = c.getRequirements();
            
            competitionData.add(row);
        }
         return competitionData;
    }

    public List<Competition> listAvailableCompetitions() {
        return competitionDAO.listAvailableCompetitions();
    }

    public List<Competition> queryCompetitions(String competitionIdStr, String selectedLevel) {
        List<Competition> allCompetitions = competitionDAO.listAvailableCompetitions();
        List<Competition> filteredCompetitions = new ArrayList<>();

        for (Competition competition : allCompetitions) {
            boolean idMatch = true;
            if (competitionIdStr != null && !competitionIdStr.isEmpty()) {
                try {
                    int competitionId = Integer.parseInt(competitionIdStr);
                    idMatch = competition.getCompetitionId() == competitionId;
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            boolean levelMatch = selectedLevel == null || "-请选择-".equals(selectedLevel) || competition.getLevel().equals(selectedLevel);

            if (idMatch && levelMatch) {
                filteredCompetitions.add(competition);
            }
        }
        return filteredCompetitions;
    }
}