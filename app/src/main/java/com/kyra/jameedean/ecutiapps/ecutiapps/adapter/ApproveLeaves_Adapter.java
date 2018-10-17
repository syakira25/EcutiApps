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

public class ApproveLeaves_Adapter extends RecyclerView.Adapter<ApproveLeaves_Adapter.ApplyLeaveViewHolder> {

    private Context mContext;
    private ArrayList<Approve> mData;

    private OnItemClick mListener;

    public ApproveLeaves_Adapter(Context context, OnItemClick listener) {
        mContext = context;
        mData = new ArrayList<>();

        mListener = listener;
    }

    @Override
    public ApplyLeaveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_holder_leaves, parent, false);
        return new ApplyLeaveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApplyLeaveViewHolder holder, int position) {

        Approve model = mData.get(position);

        holder.name.setText("Staff Name : "+model.getName());
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

        private TextView name;
        private TextView date_start;
        private TextView date_end;


        ApplyLeaveViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_name);
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
