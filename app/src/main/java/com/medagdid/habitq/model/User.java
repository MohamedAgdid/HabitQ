package com.medagdid.habitq.model;
public class User {

    private int level;
    private int currentXP;
    private int totalXP;

    public User() {
        this.level = 1;
        this.currentXP = 0;
        this.totalXP = 0;
    }

    public int getLevel() { return level; }
    public int getCurrentXP() { return currentXP; }
    public int getTotalXP() { return totalXP; }

    public void setLevel(int level) { this.level = level; }
    public void setCurrentXP(int xp) { this.currentXP = xp; }
    public void addXP(int xp) {
        this.currentXP += xp;
        this.totalXP += xp;
    }
}
