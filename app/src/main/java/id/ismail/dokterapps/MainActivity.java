package id.ismail.dokterapps;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import id.ismail.dokterapps.lib.SharedDokter;

public class MainActivity extends AppCompatActivity {
    LinearLayout lnrKeluar, lnrPanggil, lnrReservasi, lnrPasien;
    Context mContext;
    SharedDokter sharedDokter;
    TextView tvNamadokter, tvEmail, tvTanggal;
    ImageView ivSetting;
    FirebaseFirestore db;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        sharedDokter = new SharedDokter(mContext);
        db = FirebaseFirestore.getInstance();

        lnrKeluar = findViewById(R.id.lnr_keluar);
        lnrPanggil = findViewById(R.id.lnr_panggil);
        lnrReservasi = findViewById(R.id.lnr_reservasi);
        lnrPasien = findViewById(R.id.lnr_pasien);
        tvNamadokter = findViewById(R.id.tv_namadokter);
        tvEmail = findViewById(R.id.tv_email);
        tvTanggal = findViewById(R.id.tv_tanggal);
        ivSetting = findViewById(R.id.iv_setting);

        initComponents();
    }

    private void initComponents() {
        Timer timer = new Timer();
        timer.schedule(new setTimeAwal(), 0, 60000);

        tvNamadokter.setText(sharedDokter.getSpNama());
        tvEmail.setText(sharedDokter.getSpEmail());

        lnrPanggil.setOnClickListener(view -> startActivity(new Intent(mContext, PasienActivity.class)));

        lnrReservasi.setOnClickListener((View.OnClickListener) view -> startActivity(new Intent(mContext, ReservasiActivity.class)));

        lnrPasien.setOnClickListener((View.OnClickListener) view -> startActivity(new Intent(mContext, ListPasienActivity.class)));

        lnrKeluar.setOnClickListener(view -> {
            insertLog("Dokter telah logout akun android", "logout");
            SharedPreferences settings = mContext.getSharedPreferences(SharedDokter.SP_APP, Context.MODE_PRIVATE);
            settings.edit().clear().apply();
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            FirebaseMessaging.getInstance().unsubscribeFromTopic("pasien-hadir");
            startActivity(intent);
            finish();
        });

        ivSetting.setOnClickListener((View.OnClickListener) view -> changePassword());
    }

    class setTimeAwal extends TimerTask {
        public void run() {
            @SuppressLint("SimpleDateFormat") String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
            tvTanggal.setText(currentDateandTime);
        }
    }

    private void changePassword() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.getWindow().setDimAmount(0);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        dialog.show();

        ImageView ivClose = dialog.findViewById(R.id.iv_close);
        final EditText etPassword = dialog.findViewById(R.id.et_password);
        final EditText etPasswordBaru = dialog.findViewById(R.id.et_password_baru);
        final EditText etPasswordVerifikasi = dialog.findViewById(R.id.et_password_verifikasi);
        Button btnSimpan = dialog.findViewById(R.id.btn_simpan);

        ivClose.setOnClickListener(view -> dialog.dismiss());

        btnSimpan.setOnClickListener(view -> {
            String passwordlama = etPassword.getText().toString().trim();
            String passwordbaru = etPasswordBaru.getText().toString().trim();
            String passwordverifikasi = etPasswordVerifikasi.getText().toString().trim();

            if (passwordlama.isEmpty()) {
                etPassword.setError("Password Lama wajib diisi");
                etPassword.requestFocus();
                return;
            }

            if (passwordbaru.isEmpty()) {
                etPasswordBaru.setError("Password Baru wajib diisi");
                etPasswordBaru.requestFocus();
                return;
            }

            if (passwordverifikasi.isEmpty()) {
                etPasswordVerifikasi.setError("Verifikasi Password Baru wajib diisi");
                etPasswordVerifikasi.requestFocus();
                return;
            }

            if (passwordbaru.length() < 5){
                etPasswordBaru.setError("Minimal 6 Karakter");
                etPasswordBaru.requestFocus();
                return;
            }

            if (!passwordbaru.equals(passwordverifikasi)){
                etPasswordVerifikasi.setError("Verifikasi Password Baru Tidak Sesuai");
                etPasswordVerifikasi.requestFocus();
                return;
            }

            simpanPassword(dialog, passwordlama, passwordbaru);
        });
    }

    private void simpanPassword(final Dialog dialog, final String passwordlama, final String passwordbaru) {
        loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
        db.collection("tb_dokter").whereEqualTo("email", sharedDokter.getSpEmail())
                .whereEqualTo("password", passwordlama).limit(1).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null){
                            if (task.getResult().size() > 0){
                                selesai(dialog, passwordbaru);
                            }else{
                                Toast.makeText(mContext, "Password Tidak Sesuai", Toast.LENGTH_LONG).show();
                                loading.dismiss();
                            }
                        }else{
                            Toast.makeText(mContext, "Password Tidak Sesuai", Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    } else {
                        Toast.makeText(mContext, "Data Anda Tidak Ditemukan", Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }
                });
    }

    private void selesai(final Dialog dialog, String passwordbaru) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("password", passwordbaru);
        db.collection("tb_dokter").document(sharedDokter.getSpIduser())
                .update(docData)
                .addOnSuccessListener(aVoid -> {
                    dialog.dismiss();
                    insertLog("Melakukan perubahan password", "update");
                    androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext).create();
                    alertDialog.setMessage("Berhasil merubah password");
                    alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "OK",
                            (dialog1, which) -> dialog1.dismiss());
                    alertDialog.show();
                    loading.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Gagal membatalkan user " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                });
    }

    private void insertLog(String aktifitas, String status){
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