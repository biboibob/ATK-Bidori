package com.example.atkmobile;

public class classListMostWanted {
    public String barang; //Image URL
    public String barcode; //Name
    public String sold; //Name

    public classListMostWanted(String barang, String barcode, String sold)
    {
        this.barang = barang;
        this.barcode = barcode;
        this.sold = sold;
    }

    public String getBarang() {
        return barang;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getSold() {
        return sold;
    }
}
