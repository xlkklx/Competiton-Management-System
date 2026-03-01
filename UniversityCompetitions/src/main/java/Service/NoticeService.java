package Service;

import Dao.NoticeDAO;
import Dao.CompetitionDAO;
import Entity.Notice;
import Entity.Competition;
import java.util.List;
import java.util.ArrayList;

public class NoticeService {
    private NoticeDAO noticeDAO = new NoticeDAO();
    private CompetitionDAO competitionDAO = new CompetitionDAO();

    public List<Notice> listAllNoticeByCompId(Integer compId) {
        if (compId == null || compId <= 0) {
            return List.of();
        }
        return noticeDAO.listByCompetitionId(compId);
    }

    public String publishNotice(Notice notice) {
        if (notice == null) {
            return "通知数据不能为空！";
        }
        if (notice.getCompetitionId() == null || notice.getCompetitionId() <= 0) {
            return "无效的竞赛ID！";
        }
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            return "通知标题不能为空！";
        }
        if (notice.getTitle().length() > 100) {
            return "通知标题长度不能超过100字！";
        }
        if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
            return "通知内容不能为空！";
        }
        if (notice.getContent().length() > 2000) {
            return "通知内容长度不能超过2000字！";
        }
        if (notice.getPublisher() == null || notice.getPublisher().trim().isEmpty()) {
            return "发布人不能为空！";
        }
         notice.setStatus(1);

        boolean success = noticeDAO.addNotice(notice);
        return success ? "通知发布成功！" : "通知发布失败，请重试！";
    }

    public String updateNotice(Notice notice) {
        if (notice == null || notice.getNoticeId() == null || notice.getNoticeId() <= 0) {
            return "无效的通知ID！";
        }
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            return "通知标题不能为空！";
        }
        if (notice.getTitle().length() > 100) {
            return "通知标题长度不能超过100字！";
        }
        if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
            return "通知内容不能为空！";
        }
        if (notice.getContent().length() > 2000) {
            return "通知内容长度不能超过2000字！";
        }
        boolean success = noticeDAO.updateNotice(notice);
        return success ? "通知修改成功！" : "通知修改失败，请重试！";
    }

    public String removeNotice(Integer noticeId) {
        if (noticeId == null || noticeId <= 0) {
            return "无效的通知ID！";
        }
        boolean success = noticeDAO.deleteNotice(noticeId);
        return success ? "通知下架成功！" : "通知下架失败，请重试！";
    }

    public List<Object[]> getAllNoticesWithCompetitionName() {
        List<Notice> notices = noticeDAO.listAllNotices();
        List<Object[]> noticeData = new ArrayList<>();
        
        for (Notice notice : notices) {
            Competition competition = competitionDAO.getCompetitionById(notice.getCompetitionId());
            String competitionName = competition != null ? competition.getName() : "未知竞赛";
            
            Object[] row = new Object[4];
            row[0] = notice.getNoticeId();
            row[1] = competitionName;
            row[2] = notice.getTitle();
            row[3] = notice.getContent();
            
            noticeData.add(row);
        }
        
        return noticeData;
    }
}