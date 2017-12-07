package uz.algo.near.models;

import java.util.List;

public class UserList {

    private List<UserListItem> online;
    private List<UserListItem> offline;

    public List<UserListItem> getOnline() {
        return online;
    }

    public void setOnline(List<UserListItem> online) {
        this.online = online;
    }

    public List<UserListItem> getOffline() {
        return offline;
    }

    public void setOffline(List<UserListItem> offline) {
        this.offline = offline;
    }
}
