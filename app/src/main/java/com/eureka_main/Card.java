package com.eureka_main;

public class Card {
    private String line1;
    private String line2;
    private String img;

    public Card(String line1, String line2,String imgx) {
        this.line1 = line1;
        this.line2 = line2;
        img = imgx;
    }


    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getImage() {
        return img;
    }
}
