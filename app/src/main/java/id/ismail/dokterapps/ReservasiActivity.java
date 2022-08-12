package id.ismail.dokterapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import id.ismail.dokterapps.firestore_item.ReservasiAdapter;
import id.ismail.dokterapps.firestore_item.ReservasiItem;
import id.ismail.dokterapps.lib.SharedDokter;

public class ReservasiActivity extends AppCompatActivity {
    Button btnPilih;
    TextView tvTanggal;
    RecyclerView rvItem;
    ImageView ivNodata;
    SharedDokter sharedDokter;
    Context mContext;
    ProgressDialog loading;
    FirebaseFirestore db;
    ReservasiAdapter reservasiAdapter;
    ArrayList<ReservasiItem> reservasiItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservasi);
        mContext = this;
        sharedDokter = new SharedDokter(mContext);
        db = FirebaseFirestore.getInstance();
        initToolbar();

        btnPilih = findViewById(R.id.btn_pilih);
        tvTanggal = findViewById(R.id.tv_tanggal);
        rvItem = findViewById(R.id.rv_item);
        ivNodata = findViewById(R.id.iv_nodata);

        initComponents();
    }

    private void initComponents() {
        rvItem.setLayoutManager(new LinearLayoutManager(mContext));
        reservasiAdapter = new ReservasiAdapter(mContext, reservasiItems);
        rvItem.setAdapter(reservasiAdapter);

        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formatted = formatter.format(cal.getTime());
        tvTanggal.setText(formatted);
        getData();
        btnPilih.setOnClickListener(view -> chooseDate());
    }

    private void chooseDate() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker =
                new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    calendar.set(year1, month1, dayOfMonth);
                    String dateString = sdf.format(calendar.getTime());
                    tvTanggal.setText(dateString); // set the date
                    getData();
                }, year, month, day); // set date picker to current date

        datePicker.show();

        datePicker.setOnCancelListener(DialogInterface::dismiss);
    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView iv_back = toolbar.findViewById(R.id.iv_back);
        ImageView iv_refresh = toolbar.findViewById(R.id.iv_refresh);
        TextView tv_title = toolbar.findViewById(R.id.tv_title);
        iv_back.setOnClickListener(view -> onBackPressed());
        iv_refresh.setOnClickListener(view -> getData());
        tv_title.setText("Data Reservasi".toUpperCase());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getData() {
        loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
        db.collection("tb_reservasi").whereEqualTo("tanggal_reservasi", tvTanggal.getText().toString())
                .orderBy("nomor_antrian", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        rvItem.setVisibility(View.GONE);
                        ivNodata.setVisibility(View.VISIBLE);
                        Toast.makeText(mContext, "Listen failed. " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        if (loading != null) {
                            loading.dismiss();
                        }
                        return;
                    }
                    reservasiItems.clear();
                    reservasiAdapter.setItemList(reservasiItems);
                    reservasiAdapter.notifyDataSetChanged();
                    if (value != null) {
                        if (value.size() > 0) {
                            rvItem.setVisibility(View.VISIBLE);
                            ivNodata.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot documentSnapshot : value) {
                                String idreservasi = documentSnapshot.getId();
                                String idpasien = Objects.requireNonNull(documentSnapshot.getData().get("idpasien")).toString();
                                String noktp = Objects.requireNonNull(documentSnapshot.getData().get("noktp")).toString();
                                String keluhan = Objects.requireNonNull(documentSnapshot.getData().get("keluhan")).toString();
                                String nomorantrian = Objects.requireNonNull(documentSnapshot.getData().get("nomor_antrian")).toString();
                                int statusreservasi = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getData().get("status_reservasi")).toString());
                                String tanggalreservasi = Objects.requireNonNull(documentSnapshot.getData().get("tanggal_reservasi")).toString();
                                getPasien(idreservasi, idpasien, noktp, keluhan, nomorantrian, statusreservasi, tanggalreservasi);
                            }
                        } else {
                            rvItem.setVisibility(View.GONE);
                            ivNodata.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext, "Tidak ada data ditemukan", Toast.LENGTH_LONG).show();
                            if (loading != null) {
                                loading.dismiss();
                            }
                        }
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getPasien(final String idreservasi, final String idpasien, final String noktp, final String keluhan, final String nomorantrian, final int statusreservasi, final String tanggalreservasi) {
        db.collection("tb_pasien").document(idpasien).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                    if (task.getResult() != null){
                    ReservasiItem ri = new ReservasiItem(idreservasi, idpasien, noktp, "", Objects.requireNonNull(task.getResult().get("nama")).toString(), Objects.requireNonNull(task.getResult().get("alamat")).toString(),
                            Objects.requireNonNull(task.getResult().get("notelp")).toString(), Objects.requireNonNull(task.getResult().get("tanggal_lahir")).toString(), keluhan, nomorantrian, statusreservasi, 0, tanggalreservasi);
                    reservasiItems.add(ri);
                    reservasiAdapter.setItemList(reservasiItems);
                    reservasiAdapter.notifyDataSetChanged();
                }
            }
            if (loading != null) {
                loading.dismiss();
            }
        });
    }
}