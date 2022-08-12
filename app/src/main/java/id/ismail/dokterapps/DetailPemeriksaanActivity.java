package id.ismail.dokterapps;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import id.ismail.dokterapps.firestore_item.DetailAdapter;
import id.ismail.dokterapps.firestore_item.DetailItem;
import id.ismail.dokterapps.lib.SharedDokter;

public class DetailPemeriksaanActivity extends AppCompatActivity {
    TextView tvNoktp, tvBpjs, tvNama, tvJumlah;
    RecyclerView rvItem;
    ImageView ivNodata;
    SharedDokter sharedDokter;
    Context mContext;
    ProgressDialog loading;
    FirebaseFirestore db;
    DetailAdapter detailAdapter;
    ArrayList<DetailItem> detailItems = new ArrayList<>();
    String idpas = "";
    String umur = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pemeriksaan);

        mContext = this;
        sharedDokter = new SharedDokter(mContext);
        db = FirebaseFirestore.getInstance();
        initToolbar();

        tvNoktp = findViewById(R.id.tv_noktp);
        tvBpjs = findViewById(R.id.tv_bpjs);
        tvNama = findViewById(R.id.tv_nama);
        tvJumlah = findViewById(R.id.tv_jumlah);
        rvItem = findViewById(R.id.rv_item);
        ivNodata = findViewById(R.id.iv_nodata);

        initComponents();
    }

    private void initComponents() {
        rvItem.setLayoutManager(new LinearLayoutManager(mContext));
        detailAdapter = new DetailAdapter(mContext, detailItems);
        rvItem.setAdapter(detailAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idpas = bundle.getString("kIdPasien");
            tvNoktp.setText(bundle.getString("kNoktp"));
            tvBpjs.setText(bundle.getString("kNobpjs"));
            tvNama.setText(bundle.getString("kNama"));
            umur = bundle.getString("kTanggal");
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setMessage("Gagal load detail data pasien");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
            finish();
        }
        getData();
    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView ivBack = toolbar.findViewById(R.id.iv_back);
        ImageView ivRefresh = toolbar.findViewById(R.id.iv_refresh);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        ivBack.setOnClickListener(view -> onBackPressed());
        ivRefresh.setOnClickListener(view -> getData());
        tvTitle.setText("History Pemeriksaan".toUpperCase());
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void getData() {
        tvJumlah.setText("0 Reserves");
        loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
        db.collection("tb_reservasi").whereEqualTo("idpasien", idpas).orderBy("tanggal_reservasi").orderBy("nomor_antrian").addSnapshotListener((value, error) -> {
            if (error != null) {
                rvItem.setVisibility(View.GONE);
                ivNodata.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "Listen failed. " + error.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Listen "+error.getMessage());
                if (loading != null) {
                    loading.dismiss();
                }
                return;
            }
            detailItems.clear();
            detailAdapter.setItemList(detailItems);
            detailAdapter.notifyDataSetChanged();

            if (value != null){
                if (value.size() > 0) {
                    rvItem.setVisibility(View.VISIBLE);
                    ivNodata.setVisibility(View.GONE);
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        String idreservasi = documentSnapshot.getId();
                        String idpasien = Objects.requireNonNull(documentSnapshot.getData().get("idpasien")).toString();
                        String keluhan = Objects.requireNonNull(documentSnapshot.getData().get("keluhan")).toString();
                        String nomorantrian = Objects.requireNonNull(documentSnapshot.getData().get("nomor_antrian")).toString();
                        int statusreservasi = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getData().get("status_reservasi")).toString());
                        String tanggalreservasi = Objects.requireNonNull(documentSnapshot.getData().get("tanggal_reservasi")).toString();
                        if (statusreservasi == 4) {
                            getDetail(idreservasi, keluhan, nomorantrian, statusreservasi, tanggalreservasi);
                        } else {
                            DetailItem ri = new DetailItem(idreservasi, idpasien, keluhan, nomorantrian, statusreservasi,
                                    tanggalreservasi, "", "", "", "", "");
                            detailItems.add(ri);
                            detailAdapter.setItemList(detailItems);
                            detailAdapter.notifyDataSetChanged();
                        }
                    }
                    tvJumlah.setText(value.size() + " Reservasi");
                } else {
                    rvItem.setVisibility(View.GONE);
                    ivNodata.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext, "Tidak ada data ditemukan", Toast.LENGTH_LONG).show();
                }
            }
            if (loading != null) {
                loading.dismiss();
            }
        });
    }

    private void getDetail(final String idreservasi, final String keluhan, final String nomorantrian, final int statusreservasi, final String tanggalreservasi) {
        db.collection("tb_hasil_pemeriksaan").whereEqualTo("id_reservasi", idreservasi).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null){
                    if (task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            String iddokter = Objects.requireNonNull(documentSnapshot.getData().get("id_dokter")).toString();
                            String obat = Objects.requireNonNull(documentSnapshot.getData().get("obat")).toString();
                            String saran = Objects.requireNonNull(documentSnapshot.getData().get("saran")).toString();
                            String tanggal_pemeriksaan = Objects.requireNonNull(documentSnapshot.getData().get("tanggal_pemeriksaan")).toString();
                            getDokter(idreservasi, keluhan, nomorantrian, statusreservasi, tanggalreservasi, iddokter, obat, saran, tanggal_pemeriksaan);
                            break;
                        }
                    }
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getDokter(final String idreservasi, final String keluhan, final String nomorantrian, final int statusreservasi, final String tanggalreservasi,
                           final String iddokter, final String obat, final String saran, final String tanggal_pemeriksaan) {
        db.collection("tb_dokter").document(iddokter).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null){
                    DetailItem ri = new DetailItem(idreservasi, idpas, keluhan, nomorantrian, statusreservasi, tanggalreservasi,
                            iddokter, Objects.requireNonNull(task.getResult().get("nama")).toString(), obat, saran, tanggal_pemeriksaan);
                    detailItems.add(ri);
                    detailAdapter.setItemList(detailItems);
                    detailAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}