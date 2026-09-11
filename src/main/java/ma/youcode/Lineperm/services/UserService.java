package ma.youcode.Lineperm.services;

import org.mindrot.jbcrypt.BCrypt;
import ma.youcode.Lineperm.models.User;
import java.io.IOException;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.*;

public class UserService {
    private final Path usersFile = Paths.get("src", "main", "Resources", "Users.txt");

    public User login(String username, String password) {
        try {
        List<String> lines = Files.readAllLines(usersFile);
            for(String line : lines) {
                String[] UserInfo = line.split(":");
                String UserPassword = UserInfo[1];
                if (UserInfo[0].equals(username)) {
                    String hashedPassword = UserInfo[1];

                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return new User(username, hashedPassword);
                    }

                    System.out.println("The password is incorrect");
                    return null;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
        System.out.println("No User with those crediantels!");
        return null;
    }

    public User signup(String username, String password) {
        if(username.isEmpty()) {
            System.out.println("The username should not be empty.");
            return null;
        }
        if(username.contains(" ")) {
            System.out.println("The username should not contain space.");
            return null;
        }
        if(username.contains(":")) {
            System.out.println("The username should not contain ':' character.");
            return null;
        }

        if(password.isEmpty()) {
            System.out.println("The password should not be empty.");
            return null;
        }
        if(password.contains(" ")) {
            System.out.println("The password should not contain space.");
            return null;
        }
        if(password.contains(":")) {
            System.out.println("The password should not contain ':' character.");
            return null;
        }
        try {
            List<String> lines = Files.readAllLines(usersFile);

            for(String line : lines) {

                String[] UserInfo = line.split(":");

                if (UserInfo[0].equals(username)) {
                    System.out.println("The username you entered already exist please enter a new one");
                    return null;
                }
            }
            
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            Files.writeString(
                usersFile,
                username + ":" + hashedPassword + System.lineSeparator(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
            
            System.out.println("You have successfully registered.");

            return new User(username, hashedPassword);
            
        } catch (IOException e) {
            System.out.println("Error accessing users file");
            return null;
        }
    }
}

