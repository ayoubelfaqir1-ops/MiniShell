package ma.youcode.Lineperm.Daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ma.youcode.Lineperm.models.Action;
import ma.youcode.Lineperm.models.User;
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

    public int getRefusedActions() {
        String sql = "SELECT COUNT(id) AS total_count FROM actions WHERE status = 'REFUSE' ";

        try (Statement stmt = cn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                return rs.getInt("total_count");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all actions", e);
        }
        return 0;
    }

    public int getDistinctActionsCount() {
        String sql = "SELECT COUNT(DISTINCT username) AS total_count FROM actions";

        try (Statement stmt = cn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                return rs.getInt("total_count");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all actions", e);
        }
        return 0;
    }

    public int getActionsPerUser(User user) {
        String sql = "SELECT Count(id) AS total_count FROM actions GROUP BY username ";

        try (PreparedStatement stmt = cn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all actions", e);
        }
        return 0;
    }

    public Map<String, Integer> getTop3ActiveFiles() {
        String sql = "SELECT COUNT(*) AS total_actions ,target AS file_name FROM actions GROUP BY target ORDER BY total_actions DESC LIMIT 3;";
        Map<String, Integer> fileActionCounts = new LinkedHashMap<>();
        try (Statement stmt = cn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                fileActionCounts.put(rs.getString("file_name"),rs.getInt("total_actions"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting actions", e);
        }
        return fileActionCounts;
    }

    public Map<String, Integer> AccesRefusedByUser() {
        String sql = "SELECT COUNT(*) AS total_actions ,username  FROM actions WHERE status = 'REFUSE' GROUP BY username;";
        Map<String, Integer> refuseByUser = new LinkedHashMap<>();
        try (Statement stmt = cn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                refuseByUser.put(rs.getString("username"),rs.getInt("total_actions"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting actions", e);
        }
        return refuseByUser;
    }

    public Map<String, Integer> UserPlusActif() {
        String sql = "SELECT COUNT(*) AS total_actions ,username  FROM actions GROUP BY username ORDER BY COUNT(*) DESC LIMIT 1;";
        Map<String, Integer> userActif = new HashMap<>();
        try (Statement stmt = cn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                userActif.put(rs.getString("username"),rs.getInt("total_actions"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting actions", e);
        }
        return userActif;
    }

    public Map<ActionType, List<Action>> ActionsByType() {
        String sql = "SELECT * FROM actions ORDER BY action_type;";
        Map<ActionType, List<Action>> actionsByType = new HashMap<>();
        try (Statement stmt = cn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ActionType typeStr = ActionType.valueOf(rs.getString("action_type")) ;
                
                Action action = new Action(
                    rs.getString("action_date"), rs.getString("action_time"), 
                    rs.getString("username"), typeStr, 
                    rs.getString("target"), ActionStatus.valueOf(rs.getString("status"))
                );

                actionsByType.computeIfAbsent(typeStr, k -> new ArrayList<>()).add(action);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting actions", e);
        }
        return actionsByType;
    }

}
