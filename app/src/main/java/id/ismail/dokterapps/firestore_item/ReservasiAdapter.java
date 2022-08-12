package id.ismail.dokterapps.firestore_item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.ismail.dokterapps.R;

import static id.ismail.dokterapps.lib.LibHelper.getAge;

public class ReservasiAdapter extends RecyclerView.Adapter<ReservasiAdapter.ViewHolder> {
    ArrayList<ReservasiItem> itemList;
    Context mContext;

    public ReservasiAdapter(Context context, ArrayList<ReservasiItem> item) {
        this.mContext = context;
        itemList = item;
    }

    public void setItemList(ArrayList<ReservasiItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ReservasiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservasi, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ReservasiAdapter.ViewHolder holder, int position) {
        final ReservasiItem item = itemList.get(position);
        holder.tvNama.setText(item.getNama_pasien());
        holder.tvNomor.setText(item.getNomor_antrian());
        holder.tvKeluhan.setText("Keluhan : " + item.getKeluhan());
        holder.tvUmur.setText("Umur : " + getAge(item.getTanggal_lahir()) + " Tahun");
        reservasi_status(holder.tvStatus, item.getStatus_reservasi());
    }

    private void reservasi_status(TextView tv_status, int status_reservasi) {
        int warna = R.color.btn_default;
        String status = "Gagal load data";
        if (status_reservasi == 1) {
            warna = R.color.btn_info;
            status = "Menunggu Panggilan";
        }else if (status_reservasi == 2) {
            warna = R.color.btn_primary;
            status = "Sedang dipanggil";
        } else if (status_reservasi == 3) {
            warna = R.color.btn_danger;
            status = "Tidak Datang";
        } else if (status_reservasi == 4) {
            warna = R.color.btn_success;
            status = "Telah Selesai";
        } else if (status_reservasi == 5) {
            warna = R.color.btn_warning;
            status = "Dibatalkan";
        }
        tv_status.setBackgroundResource(warna);
        tv_status.setText(status);
    }

    @Override
    public int getItemCount() {
        if (itemList == null) {
            return 0;
        } else {
            return itemList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomor, tvNama, tvUmur, tvKeluhan, tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNomor = itemView.findViewById(R.id.tv_nomor);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvUmur = itemView.findViewById(R.id.tv_umur);
            tvKeluhan = itemView.findViewById(R.id.tv_keluhan);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}