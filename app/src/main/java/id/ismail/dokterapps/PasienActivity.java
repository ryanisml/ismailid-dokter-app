package id.ismail.dokterapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import id.ismail.dokterapps.lib.RequestHandler;
import id.ismail.dokterapps.lib.SharedDokter;

import static id.ismail.dokterapps.lib.LibHelper.getAge;
import static id.ismail.dokterapps.lib.LibHelper.my_url;

public class PasienActivity extends AppCompatActivity {
    TextView tvWaktu, tvNoktp, tvNoantrian, tvBpjs, tvNama, tvNotelp, tvAlamat, tvKeluhan,
            tvHistory, tvUmur;
    EditText etHasil, etObat;
    Button btnBatalkan, btnSimpan;
    ImageView ivNodata;
    ScrollView svPasien;
    LinearLayout lnrButton;
    Context mContext;
    SharedDokter sharedDokter;
    ProgressDialog loading;
    FirebaseFirestore db;
    String tanggalres;
    String idreservasi = "";
    String idpasien = "";
    String tglreservasi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasien);

        mContext = this;
        sharedDokter = new SharedDokter(mContext);
        db = FirebaseFirestore.getInstance();
        initToolbar();

        tvNoktp = findViewById(R.id.tv_noktp);
        tvWaktu = findViewById(R.id.tv_waktu);
        tvNoantrian = findViewById(R.id.tv_noantrian);
        tvBpjs = findViewById(R.id.tv_bpjs);
        tvNama = findViewById(R.id.tv_nama);
        tvNotelp = findViewById(R.id.tv_notelp);
        tvAlamat = findViewById(R.id.tv_alamat);
        tvKeluhan = findViewById(R.id.tv_keluhan);
        tvHistory = findViewById(R.id.tv_history);
        tvUmur = findViewById(R.id.tv_umur);
        etHasil = findViewById(R.id.et_hasil);
        etObat = findViewById(R.id.et_obat);
        btnBatalkan = findViewById(R.id.btn_batalkan);
        btnSimpan = findViewById(R.id.btn_simpan);
        ivNodata = findViewById(R.id.iv_nodata);
        svPasien = findViewById(R.id.sv_pasien);
        lnrButton = findViewById(R.id.lnr_button);

        initComponents();
    }

    private void initComponents() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    @SuppressLint("SimpleDateFormat") String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                    tvWaktu.setText(currentDateandTime);
                });
            }
        }, 0, 60000);
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        tanggalres = formatter.format(cal.getTime());

        refreshData();

        btnBatalkan.setOnClickListener(view -> {
            AlertDialog.Builder ald = new AlertDialog.Builder(mContext);
            ald.setMessage("Yakin ingin membatalkan antrian ini?");
            ald.setCancelable(true);

            ald.setPositiveButton("Yes", (dialog, id) -> batalkanPasien());

            ald.setNegativeButton("No", (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = ald.create();
            alert11.show();
        });

        btnSimpan.setOnClickListener((View.OnClickListener) view -> {
            AlertDialog.Builder ald = new AlertDialog.Builder(mContext);
            ald.setMessage("Simpan hasil pemeriksaan pasien?");
            ald.setCancelable(true);

            ald.setPositiveButton("Yes", (dialog, id) -> simpanPasien());

            ald.setNegativeButton("No", (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = ald.create();
            alert11.show();
        });

        tvHistory.setOnClickListener((View.OnClickListener) view -> {
            Intent in = new Intent(mContext, DetailPemeriksaanActivity.class);
            Bundle b = new Bundle();
            b.putString("kIdPasien", idpasien);
            b.putString("kNoktp", tvNoktp.getText().toString());
            b.putString("kNobpjs", tvBpjs.getText().toString());
            b.putString("kNama", tvNama.getText().toString());
            b.putString("kTanggal", tglreservasi);
            in.putExtras(b);
            startActivity(in);
        });
    }

    private void simpanPasien() {
        loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
        Map<String, Object> datap = new HashMap<>();
        datap.put("id_reservasi", idreservasi);
        datap.put("id_pasien", idpasien);
        datap.put("id_dokter", sharedDokter.getSpIduser());
        datap.put("keluhan", tvKeluhan.getText().toString());
        datap.put("saran", etHasil.getText().toString());
        datap.put("obat", etObat.getText().toString());
        datap.put("tanggal_pemeriksaan", tvWaktu.getText().toString());
        db.collection("tb_hasil_pemeriksaan").add(datap).addOnSuccessListener(documentReference -> {
            Map<String, Object> docData = new HashMap<>();
            docData.put("status_reservasi", 4);
            db.collection("tb_reservasi").document(idreservasi).update(docData).addOnSuccessListener(aVoid -> {
                checkAdmin(tvWaktu.getText().toString(), tvNoantrian.getText().toString(), tvKeluhan.getText().toString());
                loading.dismiss();
            });

            insertLog("Pasien " + tvNama.getText().toString() + " no ktp " + tvNoktp.getText().toString() +
                    " telah di periksa pada " + tvWaktu.getText().toString(), "insert");
        });
    }

    private void selesai(String noantrian) {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext).create();
        alertDialog.setMessage("Nomor antrian " + noantrian + " telah diperiksa. Silahkan lanjutkan pemeriksaan pasien nomor antrian berikutnya.");
        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
        lnrButton.setVisibility(View.GONE);
        svPasien.setVisibility(View.GONE);
        ivNodata.setVisibility(View.VISIBLE);
    }

    private void checkAdmin(final String tanggal, final String noantrian, final String keluhan) {
        db.collection("tb_notification").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null){
                    if (task.getResult().size() > 0) {
                        List<String> token = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            token.add(Objects.requireNonNull(documentSnapshot.getData().get("token")).toString());
                        }
                        HashMap<String, String> params = new HashMap<>();
                        for (int i = 0; i < token.size(); i++) {
                            params.put("token[" + i + "]", token.get(i));
                        }
                        params.put("tanggal", tanggal);
                        params.put("noantrian", noantrian);
                        params.put("keluhan", keluhan);
                        params.put("action", "periksa");
                        new MyHttpRequestTask().execute(params);
                    } else {
                        loading.dismiss();
                    }
                }else{
                    loading.dismiss();
                }
                selesai(noantrian);
            } else {
                if (task.getException() != null){
                Toast.makeText(mContext, "Gagal menambahkan data " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
                loading.dismiss();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class MyHttpRequestTask extends AsyncTask<HashMap<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
        }

        @SafeVarargs
        @Override
        protected final String doInBackground(HashMap<String, String>... v) {
            RequestHandler rh = new RequestHandler();
            return rh.sendPostRequest(my_url, v[0]);
        }
    }

    private void batalkanPasien() {
        loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
        Map<String, Object> docData = new HashMap<>();
        docData.put("status_reservasi", 5);
        db.collection("tb_reservasi").document(idreservasi)
                .update(docData)
                .addOnSuccessListener(aVoid -> {
                    insertLog("Pasien " + tvNama.getText().toString() + " no ktp " + tvNoktp.getText().toString() +
                            " telah dibatalkan oleh "+sharedDokter.getSpNama(), "update");
                    androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext).create();
                    alertDialog.setMessage("Antrian pasien berhasil dibatalkan");
                    alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "OK",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                    lnrButton.setVisibility(View.GONE);
                    svPasien.setVisibility(View.GONE);
                    ivNodata.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Gagal membatalkan user " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                });

    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView iv_back = toolbar.findViewById(R.id.iv_back);
        ImageView iv_refresh = toolbar.findViewById(R.id.iv_refresh);
        TextView tv_title = toolbar.findViewById(R.id.tv_title);
        iv_back.setOnClickListener(view -> onBackPressed());
        iv_refresh.setOnClickListener(view -> refreshData());
        tv_title.setText("Panggil Pasien".toUpperCase());
    }

    private void refreshData() {
        idreservasi = "";
        idpasien = "";
        loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
        db.collection("tb_reservasi").whereEqualTo("status_reservasi", 2).whereEqualTo("tanggal_reservasi", tanggalres).orderBy("nomor_antrian", Query.Direction.ASCENDING)
                .limit(1).addSnapshotListener((value, error) -> {
                    lnrButton.setVisibility(View.GONE);
                    svPasien.setVisibility(View.GONE);
                    ivNodata.setVisibility(View.VISIBLE);
                    if (error != null) {
                        Toast.makeText(mContext, "Listen failed. " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        if (loading != null) {
                            loading.dismiss();
                        }
                        return;
                    }
                    if (value != null){
                    if (value.size() > 0) {
                        lnrButton.setVisibility(View.VISIBLE);
                        svPasien.setVisibility(View.VISIBLE);
                        ivNodata.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot documentSnapshot : value) {
                            idreservasi = documentSnapshot.getId();
                            idpasien = Objects.requireNonNull(documentSnapshot.getData().get("idpasien")).toString();
                            String noktp = Objects.requireNonNull(documentSnapshot.getData().get("noktp")).toString();
                            String keluhan = Objects.requireNonNull(documentSnapshot.getData().get("keluhan")).toString();
                            String nomorantrian = Objects.requireNonNull(documentSnapshot.getData().get("nomor_antrian")).toString();
                            tglreservasi = Objects.requireNonNull(documentSnapshot.getData().get("tanggal_reservasi")).toString();
                            getPasien(noktp, keluhan, nomorantrian);
                            break;
                        }
                    } else {
                        Toast.makeText(mContext, "Data pasien tidak ditemukan", Toast.LENGTH_LONG).show();
                        if (loading != null) {
                            loading.dismiss();
                        }
                    }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void getPasien(final String noktp, final String keluhan, final String nomorantrian) {
        db.collection("tb_pasien").document(idpasien).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null){
                    String nama = Objects.requireNonNull(task.getResult().get("nama")).toString();
                    String alamat = Objects.requireNonNull(task.getResult().get("alamat")).toString();
                    String notelp = Objects.requireNonNull(task.getResult().get("notelp")).toString();
                    String tgllahir = Objects.requireNonNull(task.getResult().get("tanggal_lahir")).toString();
                    tvNoktp.setText(noktp);
                    if (task.getResult().get("nobpjs") == null) {
                        tvBpjs.setText("(Tidal ada)");
                    } else {
                        String nobpjs = Objects.requireNonNull(task.getResult().get("nobpjs")).toString();
                        tvBpjs.setText(nobpjs);
                    }
                    tvNama.setText(nama);
                    tvUmur.setText(getAge(tgllahir) + " Tahun (" + tgllahir + ")");
                    tvNotelp.setText(notelp);
                    tvAlamat.setText(alamat);
                    tvKeluhan.setText(keluhan);
                    tvNoantrian.setText(nomorantrian);
                }
            }
            if (loading != null) {
                loading.dismiss();
            }
        });
    }

    private void insertLog(String aktifitas, String status) {
        @SuppressLint("SimpleDateFormat") String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Map<String, Object> datap = new HashMap<>();
        datap.put("aktifitas_log", sharedDokter.getSpNama() + " - " + aktifitas);
        datap.put("status_log", status);
        datap.put("ip_log", sharedDokter.getSpPhoneType() + " - " + sharedDokter.getSpImei());
        datap.put("waktu_log", currentDateandTime);
        datap.put("iduser_log", sharedDokter.getSpIduser());
        datap.put("from_log", "android-dokter");
        db.collection("tb_log_aktifitas").add(datap);
    }
}