package id.ismail.dokterapps.firestore_item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.ismail.dokterapps.R;

import static id.ismail.dokterapps.lib.LibHelper.getAge;

public class PasienAdapter extends RecyclerView.Adapter<PasienAdapter.ViewHolder> implements Filterable {
    ArrayList<ReservasiItem> itemList;
    ArrayList<ReservasiItem> itemListFilter;
    Context mContext;

    public void setItemList(ArrayList<ReservasiItem> itemList) {
        this.itemList = itemList;
    }

    public void setItemListFilter(ArrayList<ReservasiItem> itemListFilter) {
        this.itemListFilter = itemListFilter;
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public PasienAdapter(Context context, ArrayList<ReservasiItem> item) {
        this.mContext = context;
        itemList = item;
        itemListFilter = item;
    }

    @NonNull
    @Override
    public PasienAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pasien, parent, false);
        return new ViewHolder(itemView);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ReservasiItem obj, int pos);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(PasienAdapter.ViewHolder holder, final int position) {
        final ReservasiItem pasien = itemListFilter.get(position);
        holder.tvNoktp.setText(pasien.getNo_ktp());
        holder.tvBpjs.setText(pasien.getNo_bpjs());
        holder.tvNama.setText(pasien.getNama_pasien());
        holder.tvNotelp.setText(pasien.getNo_telepon());
        holder.tvUmur.setText(getAge(pasien.getTanggal_lahir())+" Tahun ("+pasien.getTanggal_lahir()+")");
        holder.tvStatus.setText(pasien.getJumlah_reservasi() + " Reservasi");
        holder.lyt_parent.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, itemList.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (itemListFilter == null) {
            return 0;
        } else {
            return itemListFilter.size();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemListFilter = itemList;
                } else {
                    ArrayList<ReservasiItem> filteredlist = new ArrayList<>();
                    for (ReservasiItem item : itemList) {
                        if (item.getNama_pasien().toLowerCase().contains(charString.toLowerCase()) || item.getNo_ktp().toLowerCase().contains(charString.toLowerCase())) {
                            filteredlist.add(item);
                        }
                    }
                    itemListFilter = filteredlist;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemListFilter;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemListFilter = (ArrayList<ReservasiItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoktp, tvBpjs, tvNama, tvNotelp, tvUmur, tvStatus;
        FrameLayout lyt_parent;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNoktp = itemView.findViewById(R.id.tv_noktp);
            tvBpjs = itemView.findViewById(R.id.tv_bpjs);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvNotelp = itemView.findViewById(R.id.tv_notelp);
            tvUmur = itemView.findViewById(R.id.tv_umur);
            tvStatus = itemView.findViewById(R.id.tv_status);
            lyt_parent = itemView.findViewById(R.id.lyt_parent);
        }
    }
}