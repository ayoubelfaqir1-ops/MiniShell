package ma.youcode.Lineperm.models;

import ma.youcode.Lineperm.enums.ActionStatus;
import ma.youcode.Lineperm.enums.ActionType;

public class Action implements Comparable<Action>{
    private String date;
    private String time;
    private String username;
    private ActionType actionType;
    private String target;
    private ActionStatus status;

    public Action(String date, String time, String username, ActionType actionType, String target, ActionStatus status) {
        this.date = date;
        this.time = time;
        this.username = username;
        this.actionType = actionType;
        this.target = target;
        this.status = status;
    }
    @Override
    public int compareTo(Action other) {
        return this.username.compareTo(other.getUsername());
    }
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public ActionStatus getStatus() {
        return status;
    }

    public void setStatus(ActionStatus status) {
        this.status = status;
    }
}
