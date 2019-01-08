package com.notebook.cvxt001122.bookbank;

public class Model {

    private String bookName,issuedDate,returningDate,interval;
    boolean isBroadcasted;

    public Model(){}
    public Model(String bookName,String issuedDate,String returningDate,String interval,boolean isBroadcasted){
        this.bookName=bookName;
        this.issuedDate=issuedDate;
        this.returningDate=returningDate;
        this.interval=interval;
        this.isBroadcasted=isBroadcasted;
    }

    public String getBookName() {
        return bookName;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public String getReturningDate() {
        return returningDate;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public void setReturningDate(String returningDate) {
        this.returningDate = returningDate;
    }

    public String getInterval() {
        return interval;
    }
}
