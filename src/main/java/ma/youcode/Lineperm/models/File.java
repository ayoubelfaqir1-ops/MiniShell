package ma.youcode.Lineperm.models;

public class File {
    private String name;
    private String permissions;

    public File(String name, String permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return this.name;
    }

    public String getPermissions() {
        return this.permissions;
    }

    public void setName(String password) {
        this.name = name;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
