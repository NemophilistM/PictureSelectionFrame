package com.example.choosephotoapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choosephotoapplication.R;
import com.example.choosephotoapplication.ViewConstants;
import com.example.choosephotoapplication.databinding.ItemPictureDisplayBinding;
import com.example.choosephotoapplication.entiy.FileImgBean;
import com.example.choosephotoapplication.util.cache.LevelThreeCache;
import com.example.choosephotoapplication.widget.SquareView;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.util.List;

public class PictureDisplayAdapter extends RecyclerView.Adapter<PictureDisplayAdapter.ViewHolder> {
    private ItemPictureDisplayBinding binding;
    /**
     * 保存有所以图片uri的集合
     */
    private List<FileImgBean> fileImgBeans;
    /**
     * 用于view层回调的接口
     */
    private requestAndEnter requestAndEnter;
    /**
     * 用于判断是否为第一次展示
     */
    private boolean isFirstEnter = true;
    /**
     * 三级缓存
     */
    private LevelThreeCache levelThreeCache = new LevelThreeCache();


    /**
     * 第一个标点的位置
     */
    private int firstVisibleItem;
    /**
     * 当前界面的可见数
     */
    private int visibleItemCount;


    /**
     * recycleView自身实例
     */
    private RecyclerView recyclerView;

    public static Handler handler;


    public PictureDisplayAdapter(List<FileImgBean> fileImgBeans,RecyclerView recyclerView,requestAndEnter requestAndEnter) {
        this.requestAndEnter = requestAndEnter;
        this.fileImgBeans = fileImgBeans;
        this.recyclerView = recyclerView;


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                assert layoutManager != null;
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition()*4;
                if(newState == 0){
                    loadBitmaps(firstVisibleItem,visibleItemCount);
                }else {
                    handler.removeMessages(ViewConstants.DOWNLOAD_HANDLER);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isFirstEnter&&fileImgBeans.size()>0){
                    loadBitmaps(0, Math.min(fileImgBeans.size(), ViewConstants.EACH_PAGE_PICTURE_UPDATE_LIMIT));
                    isFirstEnter = false;
                }
            }
        });
    }

    @NonNull
    @Override
    public PictureDisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemPictureDisplayBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding.getRoot(),binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int nowPosition = position*4;
        if(position>fileImgBeans.size()/4){
            holder.viewOne.setTag(fileImgBeans.get(nowPosition).getUri());
            setImageView(fileImgBeans.get(nowPosition).getUri(),holder.viewOne);
            if(nowPosition+1<fileImgBeans.size()){
                holder.viewTwo.setTag(fileImgBeans.get(nowPosition+1).getUri());
                setImageView(fileImgBeans.get(nowPosition+1).getUri(),holder.viewTwo);
            }else if(nowPosition+2<fileImgBeans.size()){
                holder.viewThree.setTag(fileImgBeans.get(nowPosition+2).getUri());
                setImageView(fileImgBeans.get(nowPosition+2).getUri(),holder.viewThree);
            }
        }else {
            holder.viewOne.setTag(fileImgBeans.get(nowPosition).getUri());
            holder.viewTwo.setTag(fileImgBeans.get(nowPosition+1).getUri());
            holder.viewThree.setTag(fileImgBeans.get(nowPosition+2).getUri());
            holder.viewFour.setTag(fileImgBeans.get(nowPosition+3).getUri());
            setImageView(fileImgBeans.get(nowPosition).getUri(),holder.viewOne);
            setImageView(fileImgBeans.get(nowPosition+1).getUri(),holder.viewTwo);
            setImageView(fileImgBeans.get(nowPosition+2).getUri(),holder.viewThree);
            setImageView(fileImgBeans.get(nowPosition+3).getUri(),holder.viewFour);
            holder.viewOne.setOnClickListener(v->{

            });
        }


    }

    @Override
    public int getItemCount() {
        if(fileImgBeans.size()%4>0){
            return fileImgBeans.size()/4+1;
        }else {
            return fileImgBeans.size()/4;
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(position == fileImgBeans.size()/4+1){
            visibleItemCount = 28+fileImgBeans.size()%4;
        }else {
            visibleItemCount = ViewConstants.EACH_PAGE_PICTURE_UPDATE_LIMIT;
        }
        return super.getItemViewType(position);
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

    private void setImageView(Uri uri, ImageView view){
        Bitmap bitmap  = levelThreeCache.getBitmap(uri,recyclerView.getContext(),null);
        if(bitmap == null ){
            view.setImageResource(R.drawable.ic_wait);
        }else {
            view.setImageBitmap(bitmap);
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

    private void loadBitmaps (int firstVisibleItem,int visibleItemCount){
            for (int i = firstVisibleItem; i < firstVisibleItem+visibleItemCount ; i++) {
                Uri uri  = fileImgBeans.get(i).getUri();
                ImageView imageView  = recyclerView.findViewWithTag(uri);
                // 从三级缓存获取图片
                Bitmap localBitmap  = levelThreeCache.getBitmap(uri,recyclerView.getContext(), (bitmap,uri1)-> {
                    ImageView imageView1 = recyclerView.findViewWithTag(uri1);
                    if(imageView1!=null&&bitmap!=null){
                        imageView1.post(()->{
                            imageView1.setImageBitmap(bitmap);
                        });
                    }
                });
                if(localBitmap==null&&imageView !=null){
                    imageView.setImageResource(R.drawable.ic_wait);
                } else if(localBitmap!=null && imageView !=null) {
                    imageView.setImageBitmap(localBitmap);
                }

            }

    }

}
