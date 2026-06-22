package model;

public class Kategori {
    private int idKategori;
    private String namaKategori;

    public Kategori() {}

    public Kategori(int idKategori, String namaKategori) {
        this.idKategori   = idKategori;
        this.namaKategori = namaKategori;
    }

    public Kategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public int    getIdKategori()   { return idKategori; }
    public String getNamaKategori() { return namaKategori; }
    public void   setIdKategori(int idKategori)       { this.idKategori   = idKategori; }
    public void   setNamaKategori(String namaKategori){ this.namaKategori = namaKategori; }
}
