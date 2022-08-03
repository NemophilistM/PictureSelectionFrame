package com.example.choosephotoapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choosephotoapplication.R;
import com.example.choosephotoapplication.ViewConstants;
import com.example.choosephotoapplication.databinding.ItemPictureDisplayBinding;
import com.example.choosephotoapplication.entiy.FileImgBean;
import com.example.choosephotoapplication.widget.SquareView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PictureDisplayAdapter extends RecyclerView.Adapter<PictureDisplayAdapter.ViewHolder> {
    private ItemPictureDisplayBinding binding;
    private List<FileImgBean> fileImgBeans;
    private requestAndEnter requestAndEnter;


    public PictureDisplayAdapter(requestAndEnter requestAndEnter) {
        this.requestAndEnter = requestAndEnter;
    }

    @NonNull
    @Override
    public PictureDisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemPictureDisplayBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding.getRoot(),binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.viewOne.setImageURI(fileImgBeans.get((position-1)*4).getUri());
//        holder.viewTwo.setImageURI(fileImgBeans.get((position-1)*4+1).getUri());
//        holder.viewThree.setImageURI(fileImgBeans.get((position-1)*4+2).getUri());
//        holder.viewFour.setImageURI(fileImgBeans.get((position-1)*4+3).getUri());
        int nowPosition = position*4;
        Picasso.with(holder.itemView.getContext()).load(fileImgBeans.get(nowPosition).getUri()).placeholder(R.drawable.ic_wait).into(holder.viewOne);
        Picasso.with(holder.itemView.getContext()).load(fileImgBeans.get(nowPosition+1).getUri()).placeholder(R.drawable.ic_wait).into(holder.viewTwo);
        Picasso.with(holder.itemView.getContext()).load(fileImgBeans.get(nowPosition+2).getUri()).placeholder(R.drawable.ic_wait).into(holder.viewThree);
        Picasso.with(holder.itemView.getContext()).load(fileImgBeans.get(nowPosition+3).getUri()).placeholder(R.drawable.ic_wait).into(holder.viewFour);
    }




    @Override
    public int getItemCount() {
        return fileImgBeans.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void notify(List<FileImgBean> fileImgBeans){
        this.fileImgBeans = fileImgBeans;
//        notifyItemRangeChanged(position, ViewConstants.PICTURE_UPDATE_LIMIT);
        notifyDataSetChanged();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        SquareView viewOne,viewTwo,viewThree,viewFour;
        public ViewHolder(@NonNull View itemView,ItemPictureDisplayBinding binding) {
            super(itemView);
            viewOne = binding.ivDisplayOne;
            viewTwo = binding.ivDisplayTwo;
            viewThree = binding.ivDisplayThree;
            viewFour = binding.ivDisplayFour;
        }
    }

    /**
     * adapter通知activity发送请求
     */
    public interface requestAndEnter {
        void load();
        void enter();

    }

    /**
     * 用于activity告知adapter请求是否成功
     */
    public interface ILoadCallback {
        void onSuccess();

        void onFailure();
    }
}
