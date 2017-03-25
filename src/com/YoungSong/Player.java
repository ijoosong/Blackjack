package com.YoungSong;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Young on 3/19/2017.
 */
public class Player {
    private String name;
    private int pocketAmount;
    private int currentAmount;
    private int firstBetAmount;
    private int secondBetAmount;
    private int thirdBetAmount;
    private int win ;
    private int blackjack ;
    private int loose ;
    private int push  ;
    private int inicialBet ;
    private String firstcard ;
    public Map<Integer,String> onHand ;


    public Player(String name, int pocketAmount) {
        this.name = name;
        this.pocketAmount = pocketAmount;
        this.currentAmount = pocketAmount;
        this.onHand = new HashMap<>();
    }

    public int getFirstBetAmount() {
        return firstBetAmount;
    }

    public int getSecondBetAmount() {
        return secondBetAmount;
    }

    public int getThirdBetAmount() {
        return thirdBetAmount;
    }

    public String getName() {
        return name;
    }

    public int getCurrentAmount(){
        return currentAmount;
    }

    public String getFirstcard() {
        return firstcard;
    }

    public int getInicialBet() {
        return inicialBet;
    }

    public void setFirstBetAmount(int firstBetAmount) {
        this.firstBetAmount = firstBetAmount;
    }

    public void setSecondBetAmount(int secondBetAmount) {
        this.secondBetAmount = secondBetAmount;
    }

    public void setThirdBetAmount(int thirdBetAmount) {
        this.thirdBetAmount = thirdBetAmount;
    }

    public void setFirstcard(String firstcard) {
        this.firstcard = firstcard;
    }

    public void setPocketAmount(int amount) {
        this.pocketAmount += amount;
    }

    public void setCurrentAmount(int amount) {
        this.currentAmount += amount;
    }

    public void setInicialBet(int inicialBet) {
        this.inicialBet = inicialBet;
    }

    public void setWin(int amount) {
        this.win += 1;
        this.currentAmount += amount*2;
    }

    public void setBlackjack(int amount) {
        this.blackjack += 1;
        this.win += 1;
        this.currentAmount += amount*2.5;
    }

    public void setLoose() {
        this.loose += 1;
    }

    public void setPush(int amount) {
        this.push += 1;
        this.currentAmount += amount;
    }

    public void updateResult(String result, int amount) {
        switch (result.toUpperCase()) {
            case "BLACKJACK":
                this.blackjack += 1;
                this.win += 1;
                this.currentAmount += amount*2.5;
                break;
            case "WIN":
                win += 1;
                this.currentAmount += amount*2;
                break;
            case "LOOSE":
                this.loose += 1;
                break;
            case "PUSH":
                this.push += 1;
                this.currentAmount += amount;
                break;
        }
        this.firstBetAmount = 0;
    }

    public String getScore(){
        double winningPercentage = 0d;
        double losingPercentage = 0d;
        try {
            winningPercentage = 100 * (double)(win + blackjack/2) / (double)(win + loose + + push + blackjack/2);
            losingPercentage = 100 *  (double)loose / (double)(win + loose + push + blackjack/2);
        } catch (ArithmeticException e){
            winningPercentage = 0;
            losingPercentage = 0;
        }
        String message = "\n" + name + "'s score" + "\n" +
                         "Win: " + this.win + ", Loose: " + this.loose + ", Push: " + this.push +
                         ", Black Jack: " + this.blackjack + "\n" +
                         "Start with $ " + this.pocketAmount + ",  Current amount $ " + this.currentAmount +
                         "   " + (10000-(loose-win)*2+blackjack) + "\n" +
                         "% of win : " + String.format("%.4f",winningPercentage) + "%,  " +
                         "% of lose : " + String.format("%.4f",losingPercentage) + "%";
        return message;
    }
}
