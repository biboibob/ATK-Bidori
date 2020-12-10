package com.example.atkmobile;

public class classListRecapDI {
    public String date;
    public String total;
    public Double val;


    public classListRecapDI(String date,String total,Double val) {
        this.date = date;
        this.total = total;
        this.val = val;
    }

    public Double getVal() {
        return val;
    }

    public String getTotal() {
        return total;
    }

    public String getDate() {
        return date;
    }
}
