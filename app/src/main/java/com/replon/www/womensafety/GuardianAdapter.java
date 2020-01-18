package com.replon.www.womensafety;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GuardianAdapter extends RecyclerView.Adapter<GuardianAdapter.ViewHolder>{

    Activity activity;
    ArrayList<ContentsGuardian> guardianList;

    public GuardianAdapter(Activity activity, ArrayList<ContentsGuardian> guardianList) {
        this.activity = activity;
        this.guardianList = guardianList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(activity.getApplicationContext());
        View view=layoutInflater.inflate(R.layout.row_view_registered,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        final ContentsGuardian contents = guardianList.get(i);

        holder.name.setText(contents.getName());
        holder.phone.setText(contents.getPhone());


    }

    @Override
    public int getItemCount() {
        return guardianList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,phone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.tv_name);
            phone = (TextView)itemView.findViewById(R.id.tv_phone);

        }
    }
}
