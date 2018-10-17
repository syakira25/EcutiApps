package com.kyra.jameedean.ecutiapps.ecutiapps.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyra.jameedean.ecutiapps.ecutiapps.R;
import com.kyra.jameedean.ecutiapps.ecutiapps.model.Approve;

import java.util.ArrayList;

public class StatusLeaves_Adapter extends RecyclerView.Adapter<StatusLeaves_Adapter.ApplyLeaveViewHolder> {

    private Context mContext;
    private ArrayList<Approve> mData;

    private OnItemClick mListener;

    public StatusLeaves_Adapter(Context context, OnItemClick listener) {
        mContext = context;
        mData = new ArrayList<>();

        mListener = listener;
    }

    @Override
    public ApplyLeaveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_status, parent, false);
        return new ApplyLeaveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApplyLeaveViewHolder holder, int position) {

        Approve model = mData.get(position);

        holder.type_mc.setText("Type Leaves : "+model.getTypes_leave());
        holder.date_start.setText("Date Start : "+model.getDate_start());
        holder.date_end.setText("Date End : "+model.getDate_end());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData(Approve model) {
        mData.add(model);

        notifyDataSetChanged();
    }

    public void clear() {
        mData.clear();

        notifyDataSetChanged();
    }

    public Approve getItem(int position) {
        return mData.get(position);
    }

    public interface OnItemClick {
        void onClick(int pos);
    }

    class ApplyLeaveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView type_mc;
        private TextView date_start;
        private TextView date_end;

        ApplyLeaveViewHolder(View itemView) {
            super(itemView);

            type_mc = itemView.findViewById(R.id.tv_mc);
            date_start = itemView.findViewById(R.id.tv_dateStart);
            date_end = itemView.findViewById(R.id.tv_dateEnd);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(getAdapterPosition());
        }
    }
}
