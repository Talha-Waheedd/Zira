package com.zira.app.data.model;

/** Snapshot of the Firestore {@code users/{uid}} document for the Home dashboard. */
public class UserProfile {

    public String name;
    public String email;
    public int streakCount;
    public int dailyGoalMins;
    public int totalXp;

    public UserProfile() {
    }

    public UserProfile(String name, String email, int streakCount, int dailyGoalMins, int totalXp) {
        this.name = name;
        this.email = email;
        this.streakCount = streakCount;
        this.dailyGoalMins = dailyGoalMins;
        this.totalXp = totalXp;
    }
}
