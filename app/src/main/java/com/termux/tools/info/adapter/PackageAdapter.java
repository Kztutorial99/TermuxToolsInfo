package com.termux.tools.info.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.termux.tools.info.R;
import com.termux.tools.info.model.PackageInfo;

import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {

    private List<PackageInfo> packageList;
    private OnPackageClickListener listener;

    public interface OnPackageClickListener {
        void onPackageClick(PackageInfo packageInfo);
    }

    public PackageAdapter(List<PackageInfo> packageList, OnPackageClickListener listener) {
        this.packageList = packageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_package, parent, false);
        return new PackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        PackageInfo packageInfo = packageList.get(position);
        holder.nameText.setText(packageInfo.getName());
        holder.versionText.setText("v" + packageInfo.getVersion());
        holder.descriptionText.setText(packageInfo.getDescription());
        holder.sizeText.setText(packageInfo.getFormattedSize());

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPackageClick(packageInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    static class PackageViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, versionText, descriptionText, sizeText;
        CardView cardView;

        PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.packageName);
            versionText = itemView.findViewById(R.id.packageVersion);
            descriptionText = itemView.findViewById(R.id.packageDescription);
            sizeText = itemView.findViewById(R.id.packageSize);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
