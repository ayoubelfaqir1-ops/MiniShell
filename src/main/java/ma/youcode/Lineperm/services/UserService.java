package ma.youcode.Lineperm.services;

import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.List;
import ma.youcode.Lineperm.models.File;
import ma.youcode.Lineperm.Daos.FileDao;
import ma.youcode.Lineperm.models.User;
import ma.youcode.Lineperm.Daos.UserDao;

public class UserService {
    private FileDao fileDao = new FileDao();
    private UserDao UserDao = new UserDao();
    private FileDao ActionDao = new FileDao();

    public User login(String username, String password) {

        List<User> users = UserDao.getAll();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                String hashedPassword = user.getPassword();

                if (BCrypt.checkpw(password, hashedPassword)) {
                    return user;
                }
                System.out.println("The password is incorrect");
                return null;
            }
        }
        System.out.println("No User with those crediantels!");
        return null;
    }

    public User signup(String username, String password) {
        if (username.isEmpty()) {
            System.out.println("The username should not be empty.");
            return null;
        }
        if (username.contains(" ")) {
            System.out.println("The username should not contain space.");
            return null;
        }
        if (username.contains(":")) {
            System.out.println("The username should not contain ':' character.");
            return null;
        }

        if (password.isEmpty()) {
            System.out.println("The password should not be empty.");
            return null;
        }
        if (password.contains(" ")) {
            System.out.println("The password should not contain space.");
            return null;
        }
        if (password.contains(":")) {
            System.out.println("The password should not contain ':' character.");
            return null;
        }

        List<User> users = UserDao.getAll();

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                System.out.println("The username you entered already exist please enter a new one");
                return null;
            }
        }

        User registredUser = new User(username, BCrypt.hashpw(password, BCrypt.gensalt()));

        boolean savedUser = UserDao.save(registredUser);

        if (savedUser) {
            System.out.println("You have successfully registered.");
            return registredUser;
        }else {
            System.out.println("Error while registering.");
            return null;
        }
    }
}
