package com.aljamour.pkveksamen.Repository;

import com.aljamour.pkveksamen.Model.UserRole;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {


    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createUser(String userName, String userPassword, String email, UserRole role) {
        jdbcTemplate.update(
                "INSERT INTO User(username, user_password, email, role) VALUES (?, ?, ?, ?)",
                userName, userPassword, email, role.name()
        );

        System.out.println(userName + email + userPassword + role);


    }

    public int validateLogin(String userName, String userPassword) {
        int id = 0;
        id = jdbcTemplate.queryForObject("SELECT user_id FROM user WHERE username = ? AND user_password = ?",
                Integer.class, userName, userPassword);

        return id;
    }
}

//    public int validateLogin(String userName, String userPassword) {
//        String sql = "SELECT user_id FROM user WHERE username = ? AND user_password = ?";
//
//        var result = jdbcTemplate.query(
//                sql,
//                (rs, rowNum) -> rs.getInt("user_id"),
//                userName,
//                userPassword
//        );
//
//        // Hvis ingen bruger findes, returner 0
//        return result.isEmpty() ? 0 : result.get(0);
//    }
//
//}