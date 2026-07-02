-- Database: db_toko_berkah_jaya (RESET BERSIH)
DROP DATABASE IF EXISTS db_toko_berkah_jaya;
CREATE DATABASE db_toko_berkah_jaya;
USE db_toko_berkah_jaya;

-- Tabel User
CREATE TABLE tb_user (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    nama_lengkap VARCHAR(100)
);

-- Tabel Barang
CREATE TABLE tb_barang (
    id_barang VARCHAR(10) PRIMARY KEY,
    nama_barang VARCHAR(100) NOT NULL,
    kategori VARCHAR(50),
    harga_jual DOUBLE NOT NULL,
    stok INT NOT NULL DEFAULT 0
);

-- Tabel Customer
CREATE TABLE tb_customer (
    id_customer VARCHAR(10) PRIMARY KEY,
    nama_customer VARCHAR(100) NOT NULL,
    alamat TEXT,
    no_telepon VARCHAR(15)
);

-- Tabel Penjualan
CREATE TABLE tb_penjualan (
    id_penjualan INT AUTO_INCREMENT PRIMARY KEY,
    tanggal DATE NOT NULL,
    id_customer VARCHAR(10),
    id_barang VARCHAR(10),
    jumlah_beli INT NOT NULL,
    total_harga DOUBLE NOT NULL,
    FOREIGN KEY (id_customer) REFERENCES tb_customer(id_customer),
    FOREIGN KEY (id_barang) REFERENCES tb_barang(id_barang)
);

-- Data Awal User
INSERT INTO tb_user (username, password, nama_lengkap) VALUES ('admin', 'admin123', 'Administrator Toko');

-- Data Awal Barang
INSERT INTO tb_barang VALUES ('B001', 'Beras Premium 5kg', 'Sembako', 65000, 50);
INSERT INTO tb_barang VALUES ('B002', 'Minyak Goreng 2L', 'Sembako', 32000, 30);
INSERT INTO tb_barang VALUES ('B003', 'Gula Pasir 1kg', 'Sembako', 14000, 100);
INSERT INTO tb_barang VALUES ('B004', 'Telur Ayam 1kg', 'Sembako', 28000, 75);

-- Data Awal Customer
INSERT INTO tb_customer VALUES ('C001', 'Budi Santoso', 'Jl. Merdeka No. 10', '08123456789');
INSERT INTO tb_customer VALUES ('C002', 'Siti Rahayu', 'Jl. Pahlawan No. 5', '08234567890');
