package ma.youcode.Lineperm.services;

import ma.youcode.Lineperm.enums.ActionStatus;
import ma.youcode.Lineperm.enums.ActionType;
import ma.youcode.Lineperm.models.User;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FileService {
    private final Path filesFile = Paths.get("src", "main", "Resources", "Files.txt");
    private final Path actionsFile = Paths.get("src", "main", "Resources", "Actions.txt");
    DateTimeFormatter formatter24 = DateTimeFormatter.ofPattern("HH:mm");

    public void showFiles(User user) {
        try {
            List<String> lines = Files.readAllLines(filesFile);
            if (lines.size() >= 1) {
                for (String line : lines) {
                    if (line.isEmpty())
                        continue;

                    String[] fileInfo = line.split(":");
                    String filePermissions = fileInfo[0];
                    String userName = fileInfo[1];
                    String fileName = fileInfo[2];
                    Path filePath = Paths.get("src", "main", "Resources", "files", fileName + ".txt");
                    if (Files.exists(filePath))
                        System.out.println(filePermissions + " " + userName + " " + fileName);
                }
                
            } else {
                System.out.println("No files!");
            }
            addAction("ALL", user.getUsername(), ActionType.LECTURE, ActionStatus.OK);
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }

    public void createFile(String name, String userName) {
        if (name.contains(" ") || name.contains(":")) {
            System.out.println("The name should not contain spaces or ':' characters.");
            addAction(name + ".txt", userName, ActionType.LECTURE, ActionStatus.REFUSE);
            return;
        }
        Path createdFile = Paths.get("src", "main", "Resources", "files", name + ".txt");

        if (Files.exists(createdFile)) {
            System.out.println("a file already exist with that name.");
            addAction(name + ".txt", userName, ActionType.CREATION, ActionStatus.REFUSE);
            
        } else {
            try {
                addFileToMetadata(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedWriter bw = Files.newBufferedWriter(filesFile, StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
                bw.write("rwd|---" + ":" + userName + ":" + name + System.lineSeparator());
                bw.close();
                Files.createDirectories(createdFile.getParent());
                bw = Files.newBufferedWriter(createdFile, StandardOpenOption.CREATE);
                bw.close();
                addAction(name + ".txt", userName, ActionType.CREATION, ActionStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("The file " + name + " has been created succefuly");
        }
    }

    private void addFileToMetadata(String name) throws IOException {
        Path tempFile = Files.createTempFile(filesFile.getParent(), "temp-", ".txt");

        try (BufferedReader reader = Files.newBufferedReader(filesFile);
                BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.isEmpty()) {
                    continue;
                }
                String[] fileInfo = currentLine.split(":");
                String fileName = fileInfo[2];

                if (!fileName.equals(name)) {
                    writer.write(currentLine);
                    writer.newLine();
                } else {
                    System.out.println("Line deleted successfully!");
                    continue;
                }
            }
            Files.move(tempFile, filesFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void catFile(User user, String name) {
        Path filePath = Paths.get("src", "main", "Resources", "files", name + ".txt");
        if (Files.exists(filePath) && ControlAcces.canDo(name, "r", user)) {
            System.out.println(name + ".txt" + ":");
            try {
                List<String> lines = Files.readAllLines(filePath);
                for (String line : lines) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            addAction(name + ".txt", user.getUsername(), ActionType.LECTURE, ActionStatus.OK);
        } else if (name == "") {
            System.out.println("please enter the name of file.");
        } else {
            System.out.println("failed to get file content");
        }
        addAction(name + ".txt", user.getUsername(), ActionType.LECTURE, ActionStatus.REFUSE);
    }

    public void editFile(User user, String name, Scanner scanner) {

        Path filePath = Paths.get("src", "main", "Resources", "files", name + ".txt");
        if (Files.exists(filePath) && ControlAcces.canDo(name, "w", user)) {
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
            try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardOpenOption.TRUNCATE_EXISTING)) {
                bw.write(content);
                addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.REFUSE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(!Files.exists(filePath)){
            System.out.println("file not exist");
        }else if(!ControlAcces.canDo(name, "w", user)){
            System.out.println("You don't have permissions.");
            addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.REFUSE);
        } else if (name == "") {
            System.out.println("please enter the name of file.");
            addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.REFUSE);
        }
    }

    public void editFilePermissions(User user, String name, String Permissions) {
        try {
            List<String> lines = Files.readAllLines(filesFile);
            if (lines.size() >= 1) {
                for (String line : lines) {
                    if (line.isEmpty())
                        continue;

                    String[] fileInfo = line.split(":");

                    String userName = fileInfo[1];
                    String fileName = fileInfo[2];

                    if (fileName.equals(name)) {
                        if (!(user.getUsername().equals(userName))) {
                            System.out.println("You don't have the permission to change permissions");
                            addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.REFUSE);
                            continue;
                        }
                        String[] filePermissions = fileInfo[0].split("\\|");
                        String othersPermissions = filePermissions[1];
                        if (Permissions.startsWith("-") && Permissions.length() <= 4) {
                            removePermissions(name, othersPermissions, Permissions);
                            addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.OK);
                        } else if (Permissions.length() <= 3) {
                            addPermissions(name, othersPermissions, Permissions);
                            addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.OK);
                        } else {
                            System.out.println("Invalid permissions .");
                            addAction(name + ".txt", user.getUsername(), ActionType.MODIFICATION, ActionStatus.REFUSE);
                        }
                    }
                }
            } else {
                System.out.println("No files!");
            }
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }

    private void replacePermissions(String name, String newOthersPerms, String OldOthersPerms) throws IOException {
        Path tempFile = Files.createTempFile(filesFile.getParent(), "temp-", ".txt");
        try (BufferedReader reader = Files.newBufferedReader(filesFile);
                BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.isEmpty()) {
                    continue;
                }
                if (currentLine.contains(name)) {
                    currentLine = currentLine.replace(OldOthersPerms, newOthersPerms);
                    writer.write(currentLine);
                    writer.newLine();
                }
            }
            Files.move(tempFile, filesFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Permissions updated succefuly .");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removePermissions(String name, String othersPermissions, String Permissions) {
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
        try {
            replacePermissions(name, newOthersPermissions, othersPermissions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPermissions(String name, String othersPermissions, String Permissions) {

        String[] template = { "r", "w", "d" };
        String[] chars = othersPermissions.split("");
        for (int i = 0; i < chars.length && i < template.length; i++) {
            if (chars[i].equals("-") && Permissions.contains(template[i])) {
                chars[i] = template[i];
            }
        }
        String newOthersPermissions = String.join("", chars);

        try {
            replacePermissions(name, newOthersPermissions, othersPermissions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addAction(String name, String userName, ActionType actionType, ActionStatus actionStatus) {
        try {
            Files.writeString(
            actionsFile, 
            LocalDate.now() + ";" + LocalTime.now().format(formatter24) + ";" + userName + ";" + actionType + ";" + name + ";" + actionStatus + System.lineSeparator(),
            StandardOpenOption.APPEND
        );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
