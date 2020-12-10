package com.example.atkmobile;

public class classListDetailStorage {
    public String id_barang;
    public String barang;
    public String barcode;
    public String penginput;
    public String update_akkhir;
    public String stock;
    public String harga_jual;

    public classListDetailStorage(String id_barang,String barang, String barcode, String penginput,String update_akkhir, String stock, String harga_jual)
    {
        this.id_barang = id_barang;
        this.barang = barang;
        this.barcode = barcode;
        this.penginput = penginput;
        this.update_akkhir = update_akkhir;
        this.stock = stock;
        this.harga_jual = harga_jual;
    }

    public String getId_barang() { return id_barang; }

    public String getBarang() {
        return barang;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getPenginput() { return penginput; }

    public String getUpdate_akkhir() {

        return update_akkhir; }

    public String getStock() { return stock; }

    public String getHarga_jual() { return harga_jual; }
}

