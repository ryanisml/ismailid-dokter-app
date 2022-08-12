package id.ismail.dokterapps;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import id.ismail.dokterapps.lib.SharedDokter;

import static id.ismail.dokterapps.lib.LibHelper.getDeviceName;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnSign;
    TextView tvForgot;
    private long mLastClickTime = 0;
    ProgressDialog loading;
    Context mContext;
    SharedDokter sharedDokter;
    String tabel = "tb_dokter";
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        sharedDokter = new SharedDokter(mContext);

        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSign = findViewById(R.id.btn_sign);
        tvForgot = findViewById(R.id.tv_forgot);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initComponents();
    }

    private void initComponents() {
        btnSign.setOnClickListener((View.OnClickListener) view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Email wajib diisi");
                etEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Kata sandi wajib diisi");
                etPassword.requestFocus();
                return;
            }

            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        final String imei = UUID.randomUUID().toString();
        loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
        db.collection(tabel).whereEqualTo("email", email).whereEqualTo("password", password).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null){
                    if (task.getResult().size() > 0){
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            sharedDokter.saveSPString(SharedDokter.SP_IDUSER, documentSnapshot.getId());
                            sharedDokter.saveSPString(SharedDokter.SP_NO_KTP, Objects.requireNonNull(documentSnapshot.getData().get("noktp")).toString());
                            sharedDokter.saveSPString(SharedDokter.SP_NAMA, Objects.requireNonNull(documentSnapshot.getData().get("nama")).toString());
                            sharedDokter.saveSPString(SharedDokter.SP_EMAIL, Objects.requireNonNull(documentSnapshot.getData().get("email")).toString());
                            sharedDokter.saveSPString(SharedDokter.SP_STATUS, Objects.requireNonNull(documentSnapshot.getData().get("status_dokter")).toString());
                            sharedDokter.saveSPString(SharedDokter.SP_IMEI, imei);
                            sharedDokter.saveSPString(SharedDokter.SP_PHONE_TYPE, getDeviceName());
                            sharedDokter.saveSPBoolean(SharedDokter.SP_SUDAH_LOGIN, true);
                            FirebaseMessaging.getInstance().subscribeToTopic("pasien-hadir");
                            Intent intent = new Intent(mContext, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            insertLog();
                            startActivity(intent);
                            break;
                        }
                    }else{
                        Toast.makeText(mContext, "Email dan password tidak ditemukan", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(mContext, "Email dan password tidak ditemukan", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mContext, "Email dan password tidak ditemukan", Toast.LENGTH_LONG).show();
            }
            loading.dismiss();
        });
    }

    private void insertLog(){
        @SuppressLint("SimpleDateFormat") String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Map<String, Object> datap = new HashMap<>();
        datap.put("aktifitas_log", sharedDokter.getSpNama() + " - " + "Dokter melakukan login di android");
        datap.put("status_log", "login");
        datap.put("ip_log", sharedDokter.getSpPhoneType() + " - " + sharedDokter.getSpImei());
        datap.put("waktu_log", currentDateandTime);
        datap.put("iduser_log", sharedDokter.getSpIduser());
        datap.put("from_log", "android-dokter");
        db.collection("tb_log_aktifitas").add(datap);
    }
}