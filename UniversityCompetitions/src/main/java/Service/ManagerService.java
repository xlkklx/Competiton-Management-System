package Service;

import Dao.ManagerDAO;
import Dao.UserDAO;
import Entity.Manager;
import Entity.User;
import java.util.ArrayList;
import java.util.List;

public class ManagerService {
    private ManagerDAO managerDAO = new ManagerDAO();
    private UserDAO userDAO = new UserDAO();

    public List<Manager> listAllManagers() {
        return managerDAO.listAllManagers();
    }

    public Manager getManagerById(String mgrId) {
        return managerDAO.getManagerById(mgrId);
    }

    public String addManager(Manager manager) {
        if (manager.getManagerId() == null || manager.getManagerId().trim().isEmpty()) {
            return "负责人ID不能为空！";
        }
        if (manager.getName() == null || manager.getName().trim().isEmpty()) {
            return "负责人姓名不能为空！";
        }
        if (managerDAO.getManagerById(manager.getManagerId()) != null) {
            return "该负责人ID已存在！";
        }
        User user = new User();
        user.setUsername(manager.getManagerId());
        user.setPassword("123456");
        user.setName(manager.getName());
        user.setRole("manager");
        user.setStatus(1);
        Integer userId = userDAO.addUser(user);
        if (userId == null) {
            return "用户账号创建失败！";
        }
        manager.setUserId(userId);
        boolean mgrSuccess = managerDAO.addManager(manager);
        if (!mgrSuccess) {
            userDAO.deleteUser(manager.getManagerId());
            return "负责人信息新增失败！";
        }
        return "负责人新增成功（默认密码：123456）！";
    }

    public String updateManager(Manager manager) {
        if (manager.getManagerId() == null || manager.getManagerId().trim().isEmpty()) {
            return "无效的负责人ID！";
        }
        boolean mgrSuccess = managerDAO.updateManager(manager);
        if (!mgrSuccess) {
            return "负责人信息修改失败！";
        }
        boolean userSuccess = userDAO.updateUserName(manager.getManagerId(), manager.getName());
        if (!userSuccess) {
            return "负责人用户姓名同步失败！";
        }
        return "负责人信息修改成功！";
    }

    public String deleteManager(String mgrId, CompetitionService competitionService) {
        if (managerDAO.getManagerById(mgrId) == null) {
            return "该负责人不存在！";
        }
        if (competitionService.hasCompetitionByMgrId(mgrId)) {
            return "该负责人关联了竞赛，无法删除！";
        }
        boolean mgrSuccess = managerDAO.deleteManager(mgrId);
        if (!mgrSuccess) {
            return "负责人信息删除失败！";
        }
        boolean userSuccess = userDAO.deleteUser(mgrId);
        if (!userSuccess) {
            return "负责人用户账号删除失败！";
        }
        return "负责人删除成功！";
    }

    public List<Manager> queryManagers(String id, String name) {
        List<Manager> all = managerDAO.listAllManagers();
        List<Manager> filter = new ArrayList<>();

        for (Manager m : all) {
            boolean idMatch = id == null || id.isEmpty() || m.getManagerId().equals(id);
            boolean nameMatch = name == null || name.isEmpty() || m.getName().contains(name);
            if (idMatch && nameMatch) {
                filter.add(m);
            }
        }
        return filter;
    }
}