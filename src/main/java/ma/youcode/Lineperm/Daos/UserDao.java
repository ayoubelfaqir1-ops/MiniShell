package ma.youcode.Lineperm.Daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import ma.youcode.Lineperm.models.User;

class UserDao extends AbstractDao<User> {

    Connection cn = super.getConnection();

    @Override
    public Optional<User> get(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user with id " + id, e);
        }
    }

    @Override
    public List<User> getAll() {
        try (Statement stmt = (Statement) cn.createStatement()) {
            String sql = "SELECT * FROM users";
            ResultSet rs = stmt.executeQuery(sql);
            List<User> users = new ArrayList<>();
            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error geting all users", e);
        }
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = cn.prepareStatement(sql)) {

            stmt.setInt(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error saving user with id " + user.getId(), e);
        }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";

        try (PreparedStatement stmt = cn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, user.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                return false;
            }

            return true;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error updating user with id " + user.getId(), e);
        }
    }

    @Override
    public boolean delete(User user) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement stmt = cn.prepareStatement(sql)) {

            stmt.setInt(1, user.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                return false;
            }

            return true;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error deleting user with id " + user.getId(), e);
        }
    }
}
