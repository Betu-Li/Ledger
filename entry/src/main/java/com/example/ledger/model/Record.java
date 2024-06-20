package com.example.ledger.model;

public class Record {
    private int id;
    private String date;
    private String time;
    private double amount;
    private String type;
    private String category;

    public Record(String date, double amount, String type, String category) {
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.category = category;
    }

    public Record(String date, String time, double amount, String type, String category) {
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.type = type;
        this.category = category;
    }

    public Record(int id, String date, String time, double amount, String type, String category) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.type = type;
        this.category = category;
    }
    public Record(){

    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

