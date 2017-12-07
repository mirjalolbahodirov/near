package uz.algo.near.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.algo.near.components.ChatDateUtil;
import uz.algo.near.models.UserList;
import uz.algo.near.models.UserListItem;
import uz.algo.near.security.SecurityUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    private SecurityUtil securityUtil;
    private JdbcTemplate jdbcTemplate;
    private ChatDateUtil chatDateUtil;

    private RowMapper<UserListItem> mapper;

    {
        mapper = (rs, rowNum) -> {
            UserListItem item = new UserListItem();
            item.setId(rs.getLong(rs.findColumn("id")));
            item.setTitle(rs.getString(rs.findColumn("title")));
            item.setImg(rs.getString(rs.findColumn("img")));
            String desc = rs.getString(rs.findColumn("desc"));
            if (desc != null && desc.length() > 50) {
                desc = desc.substring(0, 50);
            }
            item.setDesc(desc);

            Timestamp dateTimestamp = rs.getTimestamp(rs.findColumn("date"));
            if (dateTimestamp!=null) {
                item.setDate(chatDateUtil.transform(new Date(dateTimestamp.getTime())));
            } else {
                item.setDate("");
            }
            item.setCount(rs.getInt(rs.findColumn("count")));
            item.setActive(rs.getBoolean(rs.findColumn("active")));
            item.setEmail(rs.getString(rs.findColumn("email")));
            return item;
        };
    }

    @Autowired
    public UserRestController(SecurityUtil securityUtil, JdbcTemplate jdbcTemplate, ChatDateUtil chatDateUtil) {
        this.securityUtil = securityUtil;
        this.jdbcTemplate = jdbcTemplate;
        this.chatDateUtil = chatDateUtil;
    }

    @GetMapping("list")
    private UserList userList() {
        UserList userList = new UserList();
        userList.setOnline(getUsersByActiveStatus(true));
        userList.setOffline(getUsersByActiveStatus(false));
        return userList;
    }

    private List<UserListItem> getUsersByActiveStatus(boolean active) {
        Long id = securityUtil.getCurrentUser().getId();
        String sql = "select u.email, u.id, u.display_name as title, u.profile_image as img, u.active, (select message_time from message where ((from_id=? and to_id=u.id) or (from_id=u.id and to_id=?)) and \n" +
                "message_time = (select max(message_time) from message where ((from_id=? and to_id=u.id) or (from_id=u.id and to_id=?)))) as date, (select message from message where \n" +
                "((from_id=? and to_id=u.id) or (from_id=u.id and to_id=?)) and message_time = (select max(message_time) from message where ((from_id=? and to_id=u.id) or (from_id=u.id and \n" +
                "to_id=?)))) as desc, (select count(id) from message where to_id=? and from_id=u.id and seen=false) as count from my_user u where id <> ? and u.active=?\n" +
                "order by date desc nulls last";

        return jdbcTemplate.query(sql, mapper, id, id, id, id, id, id, id, id, id, id, active);
    }

}
