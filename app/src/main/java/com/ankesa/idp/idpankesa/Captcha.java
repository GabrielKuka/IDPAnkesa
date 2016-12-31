package com.ankesa.idp.idpankesa;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.List;
import java.util.Random;

abstract class Captcha {
    static List<Integer> usedColors;
    Bitmap image;
    String answer = "";
    int x = 0;
    int y = 0;
    private int width;
    private int height;

    static int color() {
        Random r = new Random();
        int number;
        do {
            number = r.nextInt(9);
        } while (usedColors.contains(number));
        usedColors.add(number);
        switch (number) {
            case 0:
                return Color.BLACK;
            case 1:
                return Color.BLUE;
            case 2:
                return Color.CYAN;
            case 3:
                return Color.DKGRAY;
            case 4:
                return Color.GRAY;
            case 5:
                return Color.GREEN;
            case 6:
                return Color.MAGENTA;
            case 7:
                return Color.RED;
            case 8:
                return Color.YELLOW;
            case 9:
                return Color.WHITE;
            default:
                return Color.WHITE;
        }
    }

    protected abstract Bitmap image();

    int getWidth() {
        return this.width;
    }

    void setWidth(int width) {
        if (width > 0 && width < 10000) {
            this.width = width;
        } else {
            this.width = 300;
        }
    }

    int getHeight() {
        return this.height;
    }

    void setHeight(int height) {
        if (height > 0 && height < 10000) {
            this.height = height;
        } else {
            this.height = 100;
        }
    }

    public Bitmap getImage() {
        return this.image;
    }

    public boolean checkAnswer(String ans) {
        return ans.equals(this.answer);
    }
}
