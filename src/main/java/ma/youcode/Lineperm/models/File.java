package ma.youcode.Lineperm.models;

public class File {
    private String name;
    private String permissions;
    private String owner;
    private String content = "";

    public File(String name, String owner, String permissions) {
        this.name = name;
        this.permissions = permissions;
        this.owner = owner;
    }

    public File(String name, String owner, String permissions, String content) {
        this.name = name;
        this.permissions = permissions;
        this.owner = owner;
        this.content = content;
    }

    public String getName() {
        return this.name;
    }

    public String getPermissions() {
        return this.permissions;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getContent() {
        return this.content;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
