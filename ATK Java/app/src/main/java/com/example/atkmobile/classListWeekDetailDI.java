package com.example.atkmobile;

public class classListWeekDetailDI {
    public String date;
    public String total;
    public String order;

    public classListWeekDetailDI(String date, String total, String order) {
        this.date = date;
        this.total = total;
        this.order = order;

    }


    public String getDate() {
        return date;
    }

    public String getOrder() {
        return order;
    }

    public String getTotal() {
        return total;
    }
}





