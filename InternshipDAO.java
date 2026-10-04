package com.recommendation.dao;

import java.sql.*;
import java.util.*;
import com.recommendation.engine.RecommendationEngine.InternshipListing;

/**
 * Data Access Object for Internship Operations (MySQL JDBC).
 */
public class InternshipDAO {

    private String dbUrl = "jdbc:mysql://localhost:3306/internship_recommendation_db";
    private String dbUser = "root";
    private String dbPass = "password";

    public List<InternshipListing> getAllActiveInternships() throws SQLException {
        List<InternshipListing> list = new ArrayList<>();
        String sql = "SELECT * FROM internships WHERE is_active = TRUE";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("internship_id");
                String title = rs.getString("title");
                String company = rs.getString("company_name");
                String category = rs.getString("category");
                String expLevel = rs.getString("experience_level");
                String workLoc = rs.getString("work_location");
                String city = rs.getString("city");
                int stipend = rs.getInt("stipend_amount");

                List<String> requiredSkills = getInternshipSkills(conn, id);

                InternshipListing item = new InternshipListing(
                    id, title, company, category, requiredSkills, expLevel, workLoc, city, stipend
                );
                list.add(item);
            }
        }
        return list;
    }

    private List<String> getInternshipSkills(Connection conn, int internshipId) throws SQLException {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT s.skill_name FROM internship_skills ik " +
                     "JOIN skills s ON ik.skill_id = s.skill_id WHERE ik.internship_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, internshipId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                skills.add(rs.getString("skill_name"));
            }
        }
        return skills;
    }
}
