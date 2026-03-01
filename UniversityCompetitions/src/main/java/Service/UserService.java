package Service;
import Dao.UserDAO;
import Entity.User;

public class UserService {
    private UserDAO userDAO = new UserDAO();
     public User login(String username, String password) {
        return userDAO.login(username, password);
    }
}
