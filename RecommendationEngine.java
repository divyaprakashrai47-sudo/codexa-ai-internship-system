package com.recommendation.engine;

import java.util.*;

/**
 * AI Content-Based Recommendation Engine for Student-Internship Matching.
 * 
 * Formula:
 * Match Score % = (W_skills * Skill_Similarity) + (W_interest * Interest_Match)
 *                 + (W_exp * Experience_Match) + (W_location * Location_Match)
 */
public class RecommendationEngine {

    public static class CriteriaWeights {
        public double skillWeight = 0.50;
        public double interestWeight = 0.25;
        public double experienceWeight = 0.15;
        public double locationWeight = 0.10;

        public CriteriaWeights() {}

        public CriteriaWeights(double wSkills, double wInterest, double wExp, double wLoc) {
            this.skillWeight = wSkills;
            this.interestWeight = wInterest;
            this.experienceWeight = wExp;
            this.locationWeight = wLoc;
        }
    }

    public static class StudentProfile {
        public int studentId;
        public List<String> skills;
        public String preferredDomain;
        public String experienceLevel; // "Beginner", "Intermediate", "Advanced"
        public String workPreference;  // "Remote", "Hybrid", "Onsite"
        public String targetLocation;

        public StudentProfile(int studentId, List<String> skills, String preferredDomain, 
                              String experienceLevel, String workPreference, String targetLocation) {
            this.studentId = studentId;
            this.skills = skills != null ? skills : new ArrayList<>();
            this.preferredDomain = preferredDomain;
            this.experienceLevel = experienceLevel;
            this.workPreference = workPreference;
            this.targetLocation = targetLocation;
        }
    }

    public static class InternshipListing {
        public int internshipId;
        public String title;
        public String companyName;
        public String category;
        public List<String> requiredSkills;
        public String experienceLevel;
        public String workLocation; // "Remote", "Hybrid", "Onsite"
        public String city;
        public int stipendAmount;

        public InternshipListing(int internshipId, String title, String companyName, String category,
                                 List<String> requiredSkills, String experienceLevel, 
                                 String workLocation, String city, int stipendAmount) {
            this.internshipId = internshipId;
            this.title = title;
            this.companyName = companyName;
            this.category = category;
            this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
            this.experienceLevel = experienceLevel;
            this.workLocation = workLocation;
            this.city = city;
            this.stipendAmount = stipendAmount;
        }
    }

    public static class RecommendationResult implements Comparable<RecommendationResult> {
        public InternshipListing internship;
        public double totalMatchPercentage;
        public double skillSubScore;
        public double interestSubScore;
        public double expSubScore;
        public double locationSubScore;
        public List<String> matchedSkills;
        public List<String> missingSkills;

        public RecommendationResult(InternshipListing internship) {
            this.internship = internship;
            this.matchedSkills = new ArrayList<>();
            this.missingSkills = new ArrayList<>();
        }

        @Override
        public int compareTo(RecommendationResult other) {
            return Double.compare(other.totalMatchPercentage, this.totalMatchPercentage);
        }
    }

    /**
     * Calculates recommendations for a student against a list of internships.
     */
    public List<RecommendationResult> calculateRecommendations(
            StudentProfile student, 
            List<InternshipListing> internships, 
            CriteriaWeights weights) {

        List<RecommendationResult> results = new ArrayList<>();

        for (InternshipListing internship : internships) {
            RecommendationResult res = evaluateMatch(student, internship, weights);
            results.add(res);
        }

        // Sort by match percentage in descending order
        Collections.sort(results);
        return results;
    }

    private RecommendationResult evaluateMatch(
            StudentProfile student, 
            InternshipListing internship, 
            CriteriaWeights weights) {

        RecommendationResult result = new RecommendationResult(internship);

        // 1. Skill Match Calculation (Overlap Ratio / Jaccard Similarity)
        Set<String> studentSkillSet = new HashSet<>();
        for (String s : student.skills) {
            studentSkillSet.add(s.trim().toLowerCase());
        }

        int reqCount = internship.requiredSkills.size();
        int matchCount = 0;

        for (String reqSkill : internship.requiredSkills) {
            String lowerReq = reqSkill.trim().toLowerCase();
            if (studentSkillSet.contains(lowerReq)) {
                matchCount++;
                result.matchedSkills.add(reqSkill);
            } else {
                result.missingSkills.add(reqSkill);
            }
        }

        double skillRatio = reqCount > 0 ? (double) matchCount / reqCount : 1.0;
        result.skillSubScore = skillRatio * 100.0;

        // 2. Interest / Domain Match
        double interestScore = 20.0; // baseline
        if (student.preferredDomain != null && 
            student.preferredDomain.equalsIgnoreCase(internship.category)) {
            interestScore = 100.0;
        } else if (student.preferredDomain != null && 
                  internship.title.toLowerCase().contains(student.preferredDomain.toLowerCase())) {
            interestScore = 85.0;
        }
        result.interestSubScore = interestScore;

        // 3. Experience Match
        double expScore = 50.0;
        if (student.experienceLevel != null && student.experienceLevel.equalsIgnoreCase(internship.experienceLevel)) {
            expScore = 100.0;
        } else if ("Beginner".equalsIgnoreCase(student.experienceLevel) && "Intermediate".equalsIgnoreCase(internship.experienceLevel)) {
            expScore = 70.0;
        }
        result.expSubScore = expScore;

        // 4. Location & Remote Preference Match
        double locationScore = 40.0;
        if ("Remote".equalsIgnoreCase(student.workPreference) && "Remote".equalsIgnoreCase(internship.workLocation)) {
            locationScore = 100.0;
        } else if ("Hybrid".equalsIgnoreCase(student.workPreference) || "Hybrid".equalsIgnoreCase(internship.workLocation)) {
            locationScore = 80.0;
        } else if (student.targetLocation != null && student.targetLocation.equalsIgnoreCase(internship.city)) {
            locationScore = 100.0;
        }
        result.locationSubScore = locationScore;

        // Final Weighted Percentage Calculation
        double weightedScore = (weights.skillWeight * result.skillSubScore) +
                              (weights.interestWeight * result.interestSubScore) +
                              (weights.experienceWeight * result.expSubScore) +
                              (weights.locationWeight * result.locationSubScore);

        result.totalMatchPercentage = Math.round(weightedScore * 10.0) / 10.0; // Round to 1 decimal place

        return result;
    }
}
