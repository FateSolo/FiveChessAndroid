package com.fatesolo.fivechessandroid;

public class UserInformation {

    private String nickname;
    private int win;
    private int lose;
    private int draw;

    public UserInformation() {
    }

    public UserInformation(String nickname, String win, String lose, String draw) {
        this.nickname = nickname;
        this.win = Integer.parseInt(win);
        this.lose = Integer.parseInt(lose);
        this.draw = Integer.parseInt(draw);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

}
