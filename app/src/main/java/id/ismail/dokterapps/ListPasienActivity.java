package id.ismail.dokterapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import id.ismail.dokterapps.firestore_item.PasienAdapter;
import id.ismail.dokterapps.firestore_item.ReservasiItem;
import id.ismail.dokterapps.lib.SharedDokter;

public class ListPasienActivity extends AppCompatActivity {
    RecyclerView rvItem;
    ImageView ivNodata;
    SearchView searchView;
    SharedDokter sharedDokter;
    Context mContext;
    ProgressDialog loading;
    FirebaseFirestore db;
    PasienAdapter pasienAdapter;
    ArrayList<ReservasiItem> reservasiItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pasien);

        mContext = this;
        sharedDokter = new SharedDokter(mContext);
        db = FirebaseFirestore.getInstance();
        initToolbar();

        rvItem = findViewById(R.id.rv_item);
        ivNodata = findViewById(R.id.iv_nodata);
        searchView = findViewById(R.id.search_view);

        searchView.setFocusable(false);

        initComponents();
    }

    private void initComponents() {
        rvItem.setLayoutManager(new LinearLayoutManager(mContext));
        pasienAdapter = new PasienAdapter(mContext, reservasiItems);
        rvItem.setAdapter(pasienAdapter);

        searchData();
        getData();
        clickData();
    }

    private void clickData() {
        pasienAdapter.setOnItemClickListener((view, obj, pos) -> {
            Intent in = new Intent(mContext, DetailPemeriksaanActivity.class);
            Bundle b = new Bundle();
            b.putString("kIdPasien", obj.getId_pasien());
            b.putString("kNoktp", obj.getNo_ktp());
            b.putString("kNobpjs", obj.getNo_bpjs());
            b.putString("kNama", obj.getNama_pasien());
            b.putInt("kTotal", obj.getJumlah_reservasi());
            b.putString("kTanggal", obj.getTanggal_lahir());
            in.putExtras(b);
            startActivity(in);
        });
    }

    private void searchData() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (pasienAdapter != null) {
                    pasienAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
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
        tvTitle.setText("List Data Pasien".toUpperCase());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getData() {
        loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
        db.collection("tb_pasien").addSnapshotListener((value, error) -> {
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
            pasienAdapter.setItemList(reservasiItems);
            pasienAdapter.setItemListFilter(reservasiItems);
            pasienAdapter.notifyDataSetChanged();

            if (value != null){
                if (value.size() > 0) {
                    rvItem.setVisibility(View.VISIBLE);
                    ivNodata.setVisibility(View.GONE);
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        String idpasien = documentSnapshot.getId();
                        String noktp = Objects.requireNonNull(documentSnapshot.getData().get("noktp")).toString();
                        String nobpjs = "(Tidak ada)";
                        if (documentSnapshot.getData().get("nobpjs") != null){
                            nobpjs = Objects.requireNonNull(documentSnapshot.getData().get("nobpjs")).toString();
                        }
                        String nama = Objects.requireNonNull(documentSnapshot.getData().get("nama")).toString();
                        String notelp = Objects.requireNonNull(documentSnapshot.getData().get("notelp")).toString();
                        String alamat = Objects.requireNonNull(documentSnapshot.getData().get("alamat")).toString();
                        String tgllahir = Objects.requireNonNull(documentSnapshot.getData().get("tanggal_lahir")).toString();
                        getTotal(idpasien, noktp, nobpjs, nama, notelp, alamat, tgllahir);
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
    private void getTotal(final String idpasien, final String noktp, final String nobpjs, final String nama, final String notelp, final String alamat, final String tgllahir) {
        db.collection("tb_reservasi").whereEqualTo("noktp", noktp).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null){
                    int totalre = task.getResult().size();
                    ReservasiItem ri = new ReservasiItem("", idpasien, noktp, nobpjs, nama, alamat, notelp, tgllahir,
                            "", "0", 0, totalre, "");
                    reservasiItems.add(ri);
                    pasienAdapter.setItemList(reservasiItems);
                    pasienAdapter.setItemListFilter(reservasiItems);
                    pasienAdapter.notifyDataSetChanged();
                }
            }
            if (loading != null){
                loading.dismiss();
            }
        });
    }
}