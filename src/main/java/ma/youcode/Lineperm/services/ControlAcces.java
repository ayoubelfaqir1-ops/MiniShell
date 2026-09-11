package ma.youcode.Lineperm.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ma.youcode.Lineperm.models.User;

public class ControlAcces {
    private static final Path filesFile = Paths.get("src", "main", "Resources", "Files.txt");

    public static boolean canDo(String name, String perm, User user) {
        try {
            List<String> lines = Files.readAllLines(filesFile);
            if (lines.size() >= 1) {
                for (String line : lines) {
                    if (line.isEmpty())
                        continue;

                    String[] fileInfo = line.split(":");
                    String[] filePermissions = fileInfo[0].split("\\|");
                    String othersPermissions = filePermissions[1];
                    String userName = fileInfo[1];
                    String fileName = fileInfo[2];
                    if (fileName.equals(name) && (othersPermissions.contains(perm) || isOwner(user, name))) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
        return false;

    }

    public static boolean isOwner(User user, String name) {
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
                    if (fileName.equals(name) && userName.equals(user.getUsername())) {
                        return true;
                    }
                }
            } else {
                System.out.println("No files!");
            }
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
        return false;
    }
}
