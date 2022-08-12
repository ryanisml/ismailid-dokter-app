package id.ismail.dokterapps.firestore_item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.ismail.dokterapps.R;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    ArrayList<DetailItem> itemList;
    Context mContext;

    public DetailAdapter(Context context, ArrayList<DetailItem> item) {
        this.mContext = context;
        itemList = item;
    }

    public void setItemList(ArrayList<DetailItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public DetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DetailAdapter.ViewHolder holder, int position) {
        final DetailItem item = itemList.get(position);
        holder.tvNomor.setText(item.getNomor_antrian());
        holder.tvTanggal.setText(item.getTanggal_reservasi());
        holder.tvKeluhan.setText(item.getKeluhan());
        reservasi_status(holder.tvStatus, item.getStatus_reservasi());
        if (item.getStatus_reservasi() == 4){
            holder.trObat.setVisibility(View.VISIBLE);
            holder.tvObat.setText(item.getObat());

            holder.trHasil.setVisibility(View.VISIBLE);
            holder.tvHasil.setText(item.getSaran());

            holder.trDokter.setVisibility(View.VISIBLE);
            holder.tvDokter.setText(item.getNama_dokter());

            holder.trTanggal.setVisibility(View.VISIBLE);
            holder.tvTglPeriksa.setText(item.getTanggal_pemeriksaan());
        }else{
            holder.trObat.setVisibility(View.GONE);
            holder.trHasil.setVisibility(View.GONE);
            holder.trDokter.setVisibility(View.GONE);
            holder.trTanggal.setVisibility(View.GONE);
        }
    }

    private void reservasi_status(TextView tv_status, int status_reservasi) {
        int warna = R.color.btn_default;
        String status = "Gagal load data";
        if (status_reservasi == 1) {
            warna = R.color.btn_info;
            status = "Menunggu Panggilan";
        } else if (status_reservasi == 2) {
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
        TextView tvNomor, tvObat, tvHasil, tvKeluhan, tvStatus, tvTglPeriksa, tvDokter, tvTanggal;
        TableRow trHasil, trObat, trTanggal, trDokter;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNomor = itemView.findViewById(R.id.tv_nomor);
            tvObat = itemView.findViewById(R.id.tv_obat);
            tvHasil = itemView.findViewById(R.id.tv_hasil);
            tvKeluhan = itemView.findViewById(R.id.tv_keluhan);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTglPeriksa = itemView.findViewById(R.id.tv_tanggal_periksa);
            tvDokter = itemView.findViewById(R.id.tv_dokter);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            trHasil = itemView.findViewById(R.id.tr_hasil);
            trObat = itemView.findViewById(R.id.tr_obat);
            trTanggal = itemView.findViewById(R.id.tr_tanggal);
            trDokter = itemView.findViewById(R.id.tr_dokter);
        }
    }
}