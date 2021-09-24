package com.user.logic;

public class Stats {
    private int statsID;
    private int userID;
    private double averageSpeed;
    private int numRacesCompleted;
    private int racesWon;
    private int lastRaceSpeed;
    private int bestRaceSpeed;
    private int leaderboardRank;
    private double[] keyStats = new double[26]; //26, we're using just alphabets for now, index 0 corresponds to a, 25 for z

    public Stats(int userID) {
        this.userID = userID;
    }

    public int getStatsID() {
        return statsID;
    }

    public void getStatsFromDatabase(int userID) {
        // get all stats from database using userID
        return;
    }

    public int getUserID() {
        return userID;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public int getNumRacesCompleted() {
        return numRacesCompleted;
    }

    public void setNumRacesCompleted(int numRacesCompleted) {
        this.numRacesCompleted = numRacesCompleted;
    }

    public int getRacesWon() {
        return racesWon;
    }

    public void setRacesWon(int racesWon) {
        this.racesWon = racesWon;
    }

    public int getLastRaceSpeed() {
        return lastRaceSpeed;
    }

    public void setLastRaceSpeed(int lastRaceSpeed) {
        this.lastRaceSpeed = lastRaceSpeed;
    }

    public int getBestRaceSpeed() {
        return bestRaceSpeed;
    }

    public void setBestRaceSpeed(int bestRaceSpeed) {
        this.bestRaceSpeed = bestRaceSpeed;
    }

    public int getLeaderboardRank() {
        return leaderboardRank;
    }

    public void setLeaderboardRank(int leaderboardRank) {
        this.leaderboardRank = leaderboardRank;
    }
}
