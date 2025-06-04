package org.ruletka.server;

public class Bet {
    public enum Type {NUM, COLOR}

    private Type type;
    private Integer num;
    private String color;
    private int amount;

    public Bet(int num, int amount) {
        this.type = Type.NUM;
        this.num = num;
        this.amount = amount;
    }

    public Bet(String color, int amount) {
        this.type = Type.COLOR;
        this.color = color;
        this.amount = amount;
    }

    public Type getType() {
        return type;
    }
    public Integer getNum() {
        return num;
    }
    public String getColor() {
        return color;
    }
    public int getAmount() {
        return amount;
    }
}
