package com.vivo.vivofanclub.model;

public class WinnersModel {
    private String id, winnerName;

    public WinnersModel() {
    }

    public WinnersModel(String id, String winnerName) {
        this.id = id;
        this.winnerName = winnerName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }
}
