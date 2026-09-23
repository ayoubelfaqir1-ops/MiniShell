package ma.youcode.Lineperm.Daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ma.youcode.Lineperm.models.Action;
import ma.youcode.Lineperm.enums.ActionType;
import ma.youcode.Lineperm.enums.ActionStatus;

public class ActionsDao extends AbstractDao<Action> {

    Connection cn = super.getConnection();

    @Override
    public Optional<Action> get(int id) {
        String sql = "SELECT * FROM actions WHERE id = ?";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Action act = new Action(
                    rs.getString("action_date"), rs.getString("action_time"), 
                    rs.getString("username"), ActionType.valueOf(rs.getString("action_type")), 
                    rs.getString("target"), ActionStatus.valueOf(rs.getString("status"))
                );
                return Optional.of(act);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving action", e);
        }
    }

    @Override
    public List<Action> getAll() {
        List<Action> actions = new ArrayList<>();
        try (Statement stmt = cn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM actions");
            while (rs.next()) {
                actions.add(new Action(
                    rs.getString("action_date"), rs.getString("action_time"), 
                    rs.getString("username"), ActionType.valueOf(rs.getString("action_type")), 
                    rs.getString("target"), ActionStatus.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all actions", e);
        }
        return actions;
    }

    @Override
    public boolean save(Action action) {
        String sql = "INSERT INTO actions (action_date, action_time, username, action_type, target, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setString(1, action.getDate());
            stmt.setString(2, action.getTime());
            stmt.setString(3, action.getUsername());
            stmt.setString(4, action.getActionType().name());
            stmt.setString(5, action.getTarget());
            stmt.setString(6, action.getStatus().name());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving action", e);
        }
    }

    @Override
    public boolean update(Action action) {
        throw new UnsupportedOperationException("Cannot update an action because Action.java does not have an ID field.");
    }

    @Override
    public boolean delete(Action action) {
        throw new UnsupportedOperationException("Cannot delete an action because Action.java does not have an ID field.");
    }
}
