package com.quiz.admin;

import com.quiz.admin.dto.UserDto;
import com.quiz.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDao {
    public com.quiz.model.User validateAdmin(String username, String password) {
        String sql = "SELECT id, username, email FROM users WHERE username = ? AND password = ? AND role = 'ADMIN' AND active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    com.quiz.model.User user = new com.quiz.model.User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return null;
    }

    public List<UserDto> getAllUsers() {
        List<UserDto> users = new ArrayList<>();
        String sql = "SELECT id, username, email, active FROM users WHERE role = 'USER'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UserDto dto = new UserDto();
                dto.setId(rs.getLong("id"));
                dto.setUsername(rs.getString("username"));
                dto.setEmail(rs.getString("email"));
                dto.setActive(rs.getBoolean("active"));
                dto.setRegisteredDate(new java.util.Date());
                users.add(dto);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return users;
    }

    public boolean disableUser(Long userId) {
        String sql = "UPDATE users SET active = FALSE WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
}
