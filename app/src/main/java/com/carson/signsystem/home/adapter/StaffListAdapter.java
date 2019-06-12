package com.carson.signsystem.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carson.signsystem.R;
import com.carson.signsystem.home.model.StaffListData;

import java.util.List;

public class StaffListAdapter extends RecyclerView.Adapter<StaffListAdapter.StaffViewHolder> {

    //先做适配器的构造函数
    private Context mContext;
    private List<StaffListData> mData = null;

    public  StaffListAdapter(Context context,List<StaffListData> data){
        mContext = context;
        mData = data;
    }

    //创建一个viewHolder类继承自RecyclerView.ViewHolder
    public static class StaffViewHolder extends RecyclerView.ViewHolder{
        TextView name,job_number,department;
        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.staff_name);
            job_number = (TextView)itemView.findViewById(R.id.staff_job_number);
            department = (TextView)itemView.findViewById(R.id.staff_department);

        }
    }




//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//
//    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_list_item, parent, false);

        return new StaffViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder staffViewHolder, int pos) {
        StaffListData staffItem = mData.get(pos);
        staffViewHolder.name.setText(staffItem.getName());
        staffViewHolder.job_number.setText(staffItem.getJob_number());
        staffViewHolder.department.setText(staffItem.getDepartment());
    }


}
