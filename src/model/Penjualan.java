package model;

import java.util.Date;

public class Penjualan {
    private int    idJual;
    private String noFaktur;
    private Date   tglTransaksi;
    private String idCustomer;
    private double totalBayar;
    private int    idUser;

    public Penjualan() {}

    public int    getIdJual()       { return idJual; }
    public String getNoFaktur()     { return noFaktur; }
    public Date   getTglTransaksi() { return tglTransaksi; }
    public String getIdCustomer()   { return idCustomer; }
    public double getTotalBayar()   { return totalBayar; }
    public int    getIdUser()       { return idUser; }

    public void setIdJual(int idJual)              { this.idJual       = idJual; }
    public void setNoFaktur(String noFaktur)       { this.noFaktur     = noFaktur; }
    public void setTglTransaksi(Date tglTransaksi) { this.tglTransaksi = tglTransaksi; }
    public void setIdCustomer(String idCustomer)   { this.idCustomer   = idCustomer; }
    public void setTotalBayar(double totalBayar)   { this.totalBayar   = totalBayar; }
    public void setIdUser(int idUser)              { this.idUser       = idUser; }
}
