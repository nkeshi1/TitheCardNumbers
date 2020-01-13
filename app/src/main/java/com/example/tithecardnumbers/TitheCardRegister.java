package com.example.tithecardnumbers;

public class TitheCardRegister {
    private String holderName;
    private int cardNo, cardYear;

    public TitheCardRegister(int cardNo, String holderName, int cardYear) {
        this.holderName = holderName;
        this.cardYear = cardYear;
        this.cardNo = cardNo;
    }

    public TitheCardRegister() {}

    public String getHolderName() { return holderName; }

    public void setHolderName(String holderName) { this.holderName = holderName; }

    public int getCardNo() { return cardNo; }

    public void setCardNo(int cardNo) { this.cardNo = cardNo; }

    public int getCardYear() { return cardYear; }

    public void setCardYear(int cardYear) { this.cardYear = cardYear; }
}
