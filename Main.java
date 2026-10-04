package com.recommendation;

import java.util.*;
import com.recommendation.engine.RecommendationEngine;
import com.recommendation.engine.RecommendationEngine.*;

/**
 * Main Standalone Runner class to demonstrate Java Recommendation Engine execution.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   AI-BASED INTERNSHIP RECOMMENDATION ENGINE   ");
        System.out.println("=================================================\n");

        // 1. Create Sample Student Profile (Matching User Prompt Scenario)
        StudentProfile student = new StudentProfile(
            2,
            Arrays.asList("Java", "Python", "SQL"),
            "AI/ML",
            "Beginner",
            "Remote",
            "Remote"
        );

        System.out.println("Student Profile:");
        System.out.println(" - Skills: " + student.skills);
        System.out.println(" - Preferred Domain: " + student.preferredDomain);
        System.out.println(" - Experience Level: " + student.experienceLevel);
        System.out.println(" - Work Preference: " + student.workPreference + "\n");

        // 2. Create Sample Active Internships
        List<InternshipListing> internships = new ArrayList<>();

        internships.add(new InternshipListing(
            101, "AI/ML Intern", "NeuralTech AI", "AI/ML",
            Arrays.asList("Python", "SQL", "Machine Learning", "PyTorch"),
            "Beginner", "Remote", "Remote", 25000
        ));

        internships.add(new InternshipListing(
            102, "Python Developer Intern", "PyData Systems", "Backend",
            Arrays.asList("Python", "Java", "SQL"),
            "Beginner", "Remote", "Remote", 20000
        ));

        internships.add(new InternshipListing(
            103, "Data Analyst Intern", "Insight Analytics", "Data Analysis",
            Arrays.asList("SQL", "Python", "Data Analysis", "Pandas"),
            "Beginner", "Remote", "Remote", 18000
        ));

        internships.add(new InternshipListing(
            104, "Full Stack Web Intern", "WebFlow Labs", "Web Development",
            Arrays.asList("React", "Node.js", "SQL"),
            "Intermediate", "Hybrid", "Bangalore", 22000
        ));

        // 3. Set Criteria Weights (Skills 50%, Domain 25%, Exp 15%, Location 10%)
        CriteriaWeights weights = new CriteriaWeights(0.50, 0.25, 0.15, 0.10);

        // 4. Run Recommendation Engine
        RecommendationEngine engine = new RecommendationEngine();
        List<RecommendationResult> results = engine.calculateRecommendations(student, internships, weights);

        // 5. Output Results
        System.out.println("System Recommendations Output:");
        System.out.println("-------------------------------------------------");
        for (RecommendationResult res : results) {
            System.out.printf("%-25s — %.1f%% match\n", res.internship.title, res.totalMatchPercentage);
            System.out.println("   Matched Skills: " + res.matchedSkills);
            System.out.println("   Missing Skills: " + res.missingSkills);
            System.out.println();
        }
    }
}
