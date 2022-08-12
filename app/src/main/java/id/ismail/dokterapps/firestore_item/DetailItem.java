package id.ismail.dokterapps.firestore_item;

public class DetailItem {
    private String id_reservasi;
    private String id_pasien;
    private String keluhan;
    private String nomor_antrian;
    private int status_reservasi;
    private String tanggal_reservasi;
    private String id_dokter;
    private String nama_dokter;
    private String obat;
    private String saran;
    private String tanggal_pemeriksaan;

    public String getId_reservasi() {
        return id_reservasi;
    }

    public String getId_pasien() {
        return id_pasien;
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

    public String getId_dokter() {
        return id_dokter;
    }

    public String getNama_dokter() {
        return nama_dokter;
    }

    public String getObat() {
        return obat;
    }

    public String getSaran() {
        return saran;
    }

    public String getTanggal_pemeriksaan() {
        return tanggal_pemeriksaan;
    }

    public DetailItem(String id_reservasi, String id_pasien, String keluhan, String nomor_antrian, int status_reservasi,
                      String tanggal_reservasi, String id_dokter, String nama_dokter, String obat, String saran, String tanggal_pemeriksaan) {
        this.id_reservasi = id_reservasi;
        this.id_pasien = id_pasien;
        this.keluhan = keluhan;
        this.nomor_antrian = nomor_antrian;
        this.status_reservasi = status_reservasi;
        this.tanggal_reservasi = tanggal_reservasi;
        this.id_dokter = id_dokter;
        this.nama_dokter = nama_dokter;
        this.obat = obat;
        this.saran = saran;
        this.tanggal_pemeriksaan = tanggal_pemeriksaan;
    }

    public DetailItem() {
    }
}
