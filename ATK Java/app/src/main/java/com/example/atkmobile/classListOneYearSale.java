package com.example.atkmobile;

public class classListOneYearSale {
    public String barang; //Image URL
    public String barcode; //Name
    public String tanggal; //Name
    public String stock; //Name
    public String harga_jual; //Name

    public classListOneYearSale(String barang, String barcode, String tanggal, String stock, String harga_jual)
    {
        this.barang = barang;
        this.barcode = barcode;
        this.tanggal = tanggal;
        this.stock = stock;
        this.harga_jual = harga_jual;
    }

    public String getBarang() {
        return barang;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getStock() {
        return stock;
    }

    public String getHarga_jual() {
        return harga_jual;
    }
}
