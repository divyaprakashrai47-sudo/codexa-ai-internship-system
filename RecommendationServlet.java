package com.recommendation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.recommendation.dao.InternshipDAO;
import com.recommendation.dao.UserDAO;
import com.recommendation.engine.RecommendationEngine;
import com.recommendation.engine.RecommendationEngine.*;

/**
 * Java Web Servlet handling REST API endpoint for internship recommendations.
 * URL Pattern: /api/recommendations
 */
@WebServlet("/api/recommendations")
public class RecommendationServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private InternshipDAO internshipDAO = new InternshipDAO();
    private RecommendationEngine engine = new RecommendationEngine();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String studentIdParam = req.getParameter("studentId");
        if (studentIdParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Missing studentId parameter\"}");
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdParam);
            StudentProfile student = userDAO.getStudentProfile(studentId);

            if (student == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Student profile not found\"}");
                return;
            }

            List<InternshipListing> internships = internshipDAO.getAllActiveInternships();
            CriteriaWeights weights = new CriteriaWeights(0.50, 0.25, 0.15, 0.10);

            List<RecommendationResult> recommendations = engine.calculateRecommendations(student, internships, weights);

            // Construct JSON Response
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < recommendations.size(); i++) {
                RecommendationResult r = recommendations.get(i);
                json.append(String.format(
                    "{\"internshipId\": %d, \"title\": \"%s\", \"company\": \"%s\", \"matchScore\": %.1f, \"matchedSkills\": %s, \"missingSkills\": %s}",
                    r.internship.internshipId,
                    r.internship.title,
                    r.internship.companyName,
                    r.totalMatchPercentage,
                    listToJson(r.matchedSkills),
                    listToJson(r.missingSkills)
                ));
                if (i < recommendations.size() - 1) json.append(",");
            }
            json.append("]");

            out.print(json.toString());

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private String listToJson(List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(list.get(i)).append("\"");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
