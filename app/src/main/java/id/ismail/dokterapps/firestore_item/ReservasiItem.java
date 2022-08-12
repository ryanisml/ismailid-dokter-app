package id.ismail.dokterapps.firestore_item;

public class ReservasiItem {
    private String id_reservasi;
    private String id_pasien;
    private String no_ktp;
    private String no_bpjs;
    private String nama_pasien;
    private String alamat_pasien;
    private String no_telepon;
    private String tanggal_lahir;
    private String keluhan;
    private String nomor_antrian;
    private int status_reservasi;
    private int jumlah_reservasi;
    private String tanggal_reservasi;

    public String getId_pasien() {
        return id_pasien;
    }

    public String getNo_ktp() {
        return no_ktp;
    }

    public String getId_reservasi() {
        return id_reservasi;
    }

    public String getNo_bpjs() {
        return no_bpjs;
    }

    public String getNama_pasien() {
        return nama_pasien;
    }

    public String getAlamat_pasien() {
        return alamat_pasien;
    }

    public String getNo_telepon() {
        return no_telepon;
    }

    public String getTanggal_lahir() {
        return tanggal_lahir;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public String getNomor_antrian() {
        return nomor_antrian;
    }

    public int getStatus_reservasi() {
        return status_reservasi;
    }

    public String getTanggal_reservasi() {
        return tanggal_reservasi;
    }

    public int getJumlah_reservasi() {
        return jumlah_reservasi;
    }

    public ReservasiItem(String id_reservasi, String id_pasien, String no_ktp, String no_bpjs, String nama_pasien,
                         String alamat_pasien, String no_telepon, String tanggal_lahir, String keluhan, String nomor_antrian,
                         int status_reservasi, int jumlah_reservasi, String tanggal_reservasi) {
        this.id_reservasi = id_reservasi;
        this.id_pasien = id_pasien;
        this.no_ktp = no_ktp;
        this.no_bpjs = no_bpjs;
        this.nama_pasien = nama_pasien;
        this.alamat_pasien = alamat_pasien;
        this.no_telepon = no_telepon;
        this.tanggal_lahir = tanggal_lahir;
        this.keluhan = keluhan;
        this.nomor_antrian = nomor_antrian;
        this.status_reservasi = status_reservasi;
        this.jumlah_reservasi = jumlah_reservasi;
        this.tanggal_reservasi = tanggal_reservasi;
    }

    public ReservasiItem() {
    }
}
