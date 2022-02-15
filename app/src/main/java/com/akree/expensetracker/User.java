package com.akree.expensetracker;

public class User {

    private String username;
    private String email;
    private String profilePicture;
    private double budget;

    public User(String username, String email, String profilePicture, double budget) {
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
        this.budget = budget;
    }

    public User() {
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}

