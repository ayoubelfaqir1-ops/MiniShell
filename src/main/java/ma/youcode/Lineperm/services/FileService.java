package ma.youcode.Lineperm.services;

import ma.youcode.Lineperm.enums.ActionStatus;
import ma.youcode.Lineperm.enums.ActionType;
import ma.youcode.Lineperm.models.User;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import ma.youcode.Lineperm.Daos.ActionsDao;
import ma.youcode.Lineperm.Daos.FileDao;
import ma.youcode.Lineperm.models.Action;
import ma.youcode.Lineperm.models.File;

public class FileService {

    private final FileDao fileDao;
    private final ActionsDao actionsDao;
    private final logAnalyzerService logService;
    private final DateTimeFormatter formatter24 = DateTimeFormatter.ofPattern("HH:mm");

    public FileService(logAnalyzerService logSerive) {
        this.fileDao = new FileDao();
        this.actionsDao = new ActionsDao();
        this.logService = logSerive;
    }

    public void showFiles(User user) {
        List<File> files = fileDao.getAll();
        if (files.size() >= 1) {
            for (File file : files) {
                System.out.println(file.getPermissions() + " " + file.getOwner() + " " + file.getName() + ".txt");
            }

        } else {
            System.out.println("No files!");
        }
        logService.addAction("ALL", user.getUsername(), ActionType.LECTURE, ActionStatus.OK);
    }

    public void createFile(String name, String userName) {
        if (name.contains(" ") || name.contains(":")) {
            System.out.println("The name should not contain spaces or ':' characters.");
            logService.addAction(name + ".txt", userName, ActionType.CREATION, ActionStatus.REFUSE);
            return;
        }

        List<File> files = fileDao.getAll();

        for (File file : files) {
            if (file.getName().equals(name)) {
                System.out.println("a file already exist with that name.");
                logService.addAction(name + ".txt", userName, ActionType.CREATION, ActionStatus.REFUSE);
            }
        }

        File createdFile = new File(name, userName, "rwd|---");
        boolean saved = fileDao.save(createdFile);

        if (saved) {
            System.out.println("The file " + name + " has been created succefuly");
            logService.addAction(name + ".txt", userName, ActionType.CREATION, ActionStatus.OK);
        } else {
            System.out.println("Failed to create the file " + name);
        }
    }

    public void catFile(User user, String name) {
        File file = fileDao.getByName(name).orElse(null);
        if (file != null && ControlAcces.canDo(name, "r", user)) {
            System.out.println(name + ".txt" + ":");
            System.out.println(file.getContent());
            logService.addAction(name + ".txt", user.getUsername(), ActionType.LECTURE, ActionStatus.OK);
        } else if (name == "") {
            System.out.println("please enter the name of file.");
        } else {
            System.out.println("failed to find file:" + name);
        }
        logService.addAction(name + ".txt", user.getUsername(), ActionType.LECTURE, ActionStatus.REFUSE);
    }

    public void editFile(User user, String name, Scanner scanner) {
        File file = fileDao.getByName(name).orElse(null);
        if (file != null && ControlAcces.canDo(name, "w", user)) {
            System.out.println("Enter the content that will replace " + name + ".txt" + " current content" + ":");
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if ("exit".equalsIgnoreCase(line.trim())) {
                    break;
                }

                sb.append(line).append(System.lineSeparator());
            }
            String content = sb.toString();
            boolean updated = fileDao.update(new File(name, file.getOwner(), file.getPermissions(), content));
            logService.addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.REFUSE);
        } else if (name == "") {
            System.out.println("please enter the name of file.");
        } else if (!ControlAcces.canDo(name, "w", user)) {
            System.out.println("You don't have permissions.");
            logService.addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.REFUSE);
        } else if (name == "") {
            System.out.println("please enter the name of file.");
        } else {
            System.out.println("failed to find file:" + name);
        }
    }

    public void editFilePermissions(User user, String name, String Permissions) {
        File file = fileDao.getByName(name).orElse(null);
        if (file != null) {
            if (!(user.getUsername().equals(file.getOwner()))) {
                System.out.println("You don't have the permission to change permissions");
                logService.addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.REFUSE);
            }
            String[] filePermissions = file.getPermissions().split("\\|");
            String othersPermissions = filePermissions[1];
            if (Permissions.startsWith("-") && Permissions.length() <= 4) {
                removePermissions(file, othersPermissions, Permissions);
                logService.addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.OK);
            } else if (Permissions.length() <= 3) {
                addPermissions(file, othersPermissions, Permissions);
                logService.addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.OK);
            } else {
                System.out.println("Invalid permissions .");
                logService.addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.REFUSE);
            }
        }
    }

    private void removePermissions(File file, String othersPermissions, String Permissions) {
        String newOthersPermissions = othersPermissions;
        String[] perms = Permissions.split("");

        for (String perm : perms) {
            if (othersPermissions.contains(perm)) {
                switch (perm) {
                    case "r":
                        newOthersPermissions = newOthersPermissions.replaceAll("^(.)(.)(.)$", "-$2$3");
                        break;
                    case "w":
                        newOthersPermissions = newOthersPermissions.replaceAll("^(.)(.)(.)$", "$1-$3");
                        break;
                    case "d":
                        newOthersPermissions = newOthersPermissions.replaceAll("^(.)(.)(.)$", "$1$2-");
                        break;
                    case "-":
                        break;
                    default:
                        System.out.println("permission " + perm + " invalid .");
                        break;
                }
            }
        }
        file.setPermissions(newOthersPermissions);
        fileDao.update(file);
    }

    private void addPermissions(File file, String othersPermissions, String Permissions) {

        String[] template = { "r", "w", "d" };
        String[] chars = othersPermissions.split("");
        for (int i = 0; i < chars.length && i < template.length; i++) {
            if (chars[i].equals("-") && Permissions.contains(template[i])) {
                chars[i] = template[i];
            }
        }
        String newOthersPermissions = String.join("", chars);

        file.setPermissions(newOthersPermissions);
        fileDao.update(file);
    }

    public void deleteFile(String name, User user) {
        File file = fileDao.getByName(name).orElse(null);

        if (file != null && ControlAcces.canDo(name, "d", user)) {
            fileDao.delete(file);
            System.out.println("file " + name + " deleted succefully");
            logService.addAction(name, user.getUsername(), ActionType.SUPPRIMER, ActionStatus.OK);
        } else if (file == null) {
            System.out.println("No file with name " + name);
            logService.addAction(name, user.getUsername(), ActionType.SUPPRIMER, ActionStatus.REFUSE);
        } else if (!ControlAcces.canDo(name, "d", user)) {
            System.out.println("You don't have the permission to delte file :" + name);
            logService.addAction(name, user.getUsername(), ActionType.SUPPRIMER, ActionStatus.REFUSE);
        }
    }
}
