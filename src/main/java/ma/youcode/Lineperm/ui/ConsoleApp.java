package ma.youcode.Lineperm.ui;

import java.util.Scanner;

import ma.youcode.Lineperm.models.User;
import ma.youcode.Lineperm.services.FileService;
import ma.youcode.Lineperm.services.UserService;

public class ConsoleApp {
    UserService userService = new UserService();
    FileService fileService = new FileService();
    Scanner scanner = new Scanner(System.in);

    public void demarrer() {
        User currentUser = null;
        boolean active = true;

        System.out.println("============================================================");
        System.out.println("  LinPerm - gestion de fichiers & droits");
        System.out.println("============================================================");
        System.out.println("Non connecté. Commandes : signup | login | help | exit");
        System.out.println("");

        while(active) {
            if(currentUser == null) {
                System.out.print("linperm>");
            }else {
                System.out.print(currentUser.getUsername() + "@" + "linperm>");
            }
            String command = scanner.nextLine().trim().toLowerCase();
            switch(command){
                case "login":
                    if(currentUser != null) {
                        System.out.pirintln("You are already loged in!");
                    }
                    System.out.print("Username: ");
                    String loginUsername = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String loginPassword = scanner.nextLine().trim();
                    currentUser = userService.login(loginUsername, loginPassword);
                    break;
                case "signup":
                    System.out.print("Username: ");
                    String signupUsername = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String signupPassword = scanner.nextLine().trim();
                        currentUser = userService.signup(signupUsername, signupPassword);
                    break;
                case "help":
                    break;
                case "exit":
                    active = false;
                    break;
                case null:
                    System.out.print("Please enter a command.");
                    break;
                case default:
                    System.out.print("command does not exist.");
            }
        }
    }
}