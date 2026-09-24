package ma.youcode.Lineperm.services;

import ma.youcode.Lineperm.Daos.FileDao;
import ma.youcode.Lineperm.models.File;
import ma.youcode.Lineperm.models.User;

public class ControlAcces {
    private static FileDao fileDao = new FileDao();

    public static boolean canDo(String name, String perm, User user) {
        File file = fileDao.getByName(name).orElse(null);
        if (file != null) {
            String[] filePermissions = file.getPermissions().split("\\|");
            String othersPermissions = filePermissions[1];
            if (file.getName().equals(name)
                    && (othersPermissions.contains(perm) || file.getOwner().equals(user.getUsername()))) {
                return true;
            }
        }

        return false;
    }
}
