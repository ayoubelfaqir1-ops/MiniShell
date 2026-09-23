package ma.youcode.Lineperm.Daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ma.youcode.Lineperm.models.File;

public class FileDao extends AbstractDao<File> {

    Connection cn = super.getConnection();

    @Override
    public Optional<File> get(int id) {
        throw new UnsupportedOperationException("Files use 'name' as a primary key, not an int ID.");
    }

    public Optional<File> getByName(String name) {
        String sql = "SELECT * FROM files WHERE name = ?";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new File(rs.getString("name"), rs.getString("permissions")));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving file", e);
        }
    }

    @Override
    public List<File> getAll() {
        List<File> files = new ArrayList<>();
        try (Statement stmt = cn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM files");
            while (rs.next()) {
                files.add(new File(rs.getString("name"), rs.getString("permissions")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all files", e);
        }
        return files;
    }

    @Override
    public boolean save(File file) {
        String sql = "INSERT INTO files (name, permissions) VALUES (?, ?)";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setString(1, file.getName());
            stmt.setString(2, file.getPermissions());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving file", e);
        }
    }

    @Override
    public boolean update(File file) {
        String sql = "UPDATE files SET permissions = ? WHERE name = ?";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setString(1, file.getPermissions());
            stmt.setString(2, file.getName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating file", e);
        }
    }

    @Override
    public boolean delete(File file) {
        String sql = "DELETE FROM files WHERE name = ?";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setString(1, file.getName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting file", e);
        }
    }
}
