package model;

public class DetailPenjualan {
    private int    idDetail;
    private int    idJual;
    private String idBarang;
    private double hargaSatuan;
    private int    jumlahBeli;
    private double subtotal;

    public DetailPenjualan() {}

    public DetailPenjualan(int idDetail, int idJual, String idBarang, double hargaSatuan, int jumlahBeli, double subtotal) {
        this.idDetail    = idDetail;
        this.idJual      = idJual;
        this.idBarang    = idBarang;
        this.hargaSatuan = hargaSatuan;
        this.jumlahBeli  = jumlahBeli;
        this.subtotal    = subtotal;
    }

    public int    getIdDetail()    { return idDetail; }
    public int    getIdJual()      { return idJual; }
    public String getIdBarang()    { return idBarang; }
    public double getHargaSatuan() { return hargaSatuan; }
    public int    getJumlahBeli()  { return jumlahBeli; }
    public double getSubtotal()    { return subtotal; }

    public void setIdDetail(int idDetail)          { this.idDetail    = idDetail; }
    public void setIdJual(int idJual)              { this.idJual      = idJual; }
    public void setIdBarang(String idBarang)       { this.idBarang    = idBarang; }
    public void setHargaSatuan(double hargaSatuan) { this.hargaSatuan = hargaSatuan; }
    public void setJumlahBeli(int jumlahBeli)      { this.jumlahBeli  = jumlahBeli; }
    public void setSubtotal(double subtotal)       { this.subtotal    = subtotal; }
}
