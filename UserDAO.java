package com.recommendation.dao;

import java.sql.*;
import java.util.*;
import com.recommendation.engine.RecommendationEngine.StudentProfile;

/**
 * Data Access Object for User & Student Database Operations (MySQL JDBC).
 */
public class UserDAO {

    private String dbUrl = "jdbc:mysql://localhost:3306/internship_recommendation_db";
    private String dbUser = "root";
    private String dbPass = "password";

    public UserDAO() {}

    public UserDAO(String url, String user, String pass) {
        this.dbUrl = url;
        this.dbUser = user;
        this.dbPass = pass;
    }

    public StudentProfile getStudentProfile(int studentId) throws SQLException {
        String sql = "SELECT p.*, u.name, u.email FROM student_profiles p " +
                     "JOIN users u ON p.student_id = u.user_id WHERE p.student_id = ?";
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String preferredDomain = rs.getString("preferred_domain");
                String expLevel = rs.getString("experience_level");
                String workPref = rs.getString("work_preference");
                String targetLoc = rs.getString("target_location");

                List<String> skills = getStudentSkills(conn, studentId);

                return new StudentProfile(studentId, skills, preferredDomain, expLevel, workPref, targetLoc);
            }
        }
        return null;
    }

    private List<String> getStudentSkills(Connection conn, int studentId) throws SQLException {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT s.skill_name FROM student_skills ss " +
                     "JOIN skills s ON ss.skill_id = s.skill_id WHERE ss.student_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                skills.add(rs.getString("skill_name"));
            }
        }
        return skills;
    }
}
