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
            String[] userInput = new String[0];
            if(scanner.hasNextLine()) {
                userInput = scanner.nextLine().trim().toLowerCase().split("\\s+"); 
            }
            String command = userInput.length >= 1 ? userInput[0] : "" ;
            String param = userInput.length > 1 ? param = userInput[1] : "";
            String extraParam = userInput.length > 2 ? extraParam = userInput[2] : "";

            switch(command){
                case "login":
                    if(currentUser != null) {
                        System.out.println("You are already loged in!");
                    }else {
                    System.out.print("Username: ");
                    String loginUsername = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String loginPassword = scanner.nextLine().trim();
                    currentUser = userService.login(loginUsername, loginPassword);
                    }
                    break;
                case "signup":
                    if(currentUser != null) {
                        System.out.println("You are already loged in!");
                    }else {
                        System.out.print("Username: ");
                        String signupUsername = scanner.nextLine().trim();
                        System.out.print("Password: ");
                        String signupPassword = scanner.nextLine().trim();
                            currentUser = userService.signup(signupUsername, signupPassword);
                    }
                    break;
                case "help":
                    break;
                case "exit":
                    active = false;
                    break;
                case "touch":
                    if(currentUser == null) {
                        System.out.println("please login first!");
                    }else {
                        if (userInput.length > 2) {
                            System.out.println("Too much arguments.");
                        }else if(param.isEmpty())
                            System.out.println("Please add the file name");
                        else
                            fileService.createFile(param ,currentUser.getUsername());
                    }
                    break;
                case "ls":
                    if(currentUser == null) {
                        System.out.println("please login first!");
                    }else {
                    fileService.showFiles();
                    }
                    break;
                case "cat":
                    if(currentUser == null) {
                        System.out.println("please login first!");
                    }else {
                    fileService.catFile(currentUser, param);
                    }
                    break;
                case "nano":
                    fileService.editFile(currentUser, param, scanner);
                    break;
                case "chmod":
                    fileService.editFilePermissions(currentUser, extraParam, param);
                    break;
                default:
                    System.out.println("command does not exist.");
                    break;
            }
        }
    }
}