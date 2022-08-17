package com.example.choosephotoapplication.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choosephotoapplication.R;
import com.example.choosephotoapplication.ViewConstants;
import com.example.choosephotoapplication.databinding.ItemPictureDisplayBinding;
import com.example.choosephotoapplication.entiy.FileImgBean;
import com.example.choosephotoapplication.widget.SelectCheckbox;
import com.example.choosephotoapplication.widget.SquareView;
import com.example.picassotest.PicassoTest;

import java.util.ArrayList;
import java.util.List;

public class PictureDisplayByPicassoTestAdapter extends RecyclerView.Adapter<PictureDisplayByPicassoTestAdapter.ViewHolder> {

    private ItemPictureDisplayBinding binding;
    /**
     * 保存有所以图片uri的集合
     */
    private final List<FileImgBean> fileImgBeans;

    /**
     * 用于view层回调的接口
     */
    private final CallbackEnter callbackEnter;
    /**
     * 用于判断是否为第一次展示
     */
    private boolean isFirstEnter = true;


    /**
     * recycleView自身实例
     */
    private final RecyclerView recyclerView;
    private final Context context;

    /**
     * 存储图片的信息
     */
    private final ArrayList<FileImgBean> selectFileBeanList;

    /**
     * 记录选中位置
     */
    private Integer selectPosition = 1;

    /**
     * 被选的图片位置
     */
    private final List<Integer> selectPicList;

    /**
     * 存储被点击的cb
     */
    private final List<SelectCheckbox> selectViewList;

    private final int maxSelect = 7;


    public PictureDisplayByPicassoTestAdapter(Context context, List<FileImgBean> fileImgBeans, RecyclerView recyclerView, CallbackEnter callbackEnter) {
        this.callbackEnter = callbackEnter;
        this.fileImgBeans = fileImgBeans;
        this.recyclerView = recyclerView;
        this.context = context;

        selectPicList = new ArrayList<>();
        selectFileBeanList = new ArrayList<>();
        selectViewList = new ArrayList<>();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                firstVisibleItem = layoutManager.findFirstVisibleItemPosition() * 4;
                if (newState == 0) {
                    PicassoTest.with(context).resumeTag(context);
                } else {
                    PicassoTest.with(context).pauseTag(context);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isFirstEnter && fileImgBeans.size() > 0) {
                    PicassoTest.with(context).resumeTag(context);
                    isFirstEnter = false;
                }
            }
        });
    }

    @NonNull
    @Override
    public PictureDisplayByPicassoTestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = ItemPictureDisplayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PictureDisplayByPicassoTestAdapter.ViewHolder(binding.getRoot(), binding);

    }


    @Override
    public void onBindViewHolder(@NonNull PictureDisplayByPicassoTestAdapter.ViewHolder holder, int position) {
        int nowPosition = position * 4;
        if (position > fileImgBeans.size() / 4) {
            PicassoTest.with(context).load(fileImgBeans.get(nowPosition).getUri()).resize(200, 200).centerInside().placeholder(R.drawable.ic_wait).tag(context).into(holder.viewOne);
            setCheckbox(holder.cbOne, nowPosition);

            if (nowPosition + 1 < fileImgBeans.size()) {
                PicassoTest.with(context).load(fileImgBeans.get(nowPosition + 1).getUri()).resize(200, 200).centerInside().placeholder(R.drawable.ic_wait).tag(context).into(holder.viewTwo);
                setCheckbox(holder.cbTwo, nowPosition);

            } else if (nowPosition + 2 < fileImgBeans.size()) {
                PicassoTest.with(context).load(fileImgBeans.get(nowPosition + 2).getUri()).resize(200, 200).centerInside().placeholder(R.drawable.ic_wait).tag(context).into(holder.viewThree);
                setCheckbox(holder.cbThree, nowPosition);
            }

        } else {
//            holder.viewOne.setTag(fileImgBeans.get(nowPosition).getUri());
//            holder.viewTwo.setTag(fileImgBeans.get(nowPosition+1).getUri());
//            holder.viewThree.setTag(fileImgBeans.get(nowPosition+2).getUri());
//            holder.viewFour.setTag(fileImgBeans.get(nowPosition+3).getUri());

            PicassoTest.with(context).load(fileImgBeans.get(nowPosition).getUri()).resize(200, 200).centerInside().placeholder(R.drawable.ic_wait).tag(context).into(holder.viewOne);
            PicassoTest.with(context).load(fileImgBeans.get(nowPosition + 1).getUri()).resize(200, 200).centerInside().placeholder(R.drawable.ic_wait).tag(context).into(holder.viewTwo);
            PicassoTest.with(context).load(fileImgBeans.get(nowPosition + 2).getUri()).resize(200, 200).centerInside().placeholder(R.drawable.ic_wait).tag(context).into(holder.viewThree);
            PicassoTest.with(context).load(fileImgBeans.get(nowPosition + 3).getUri()).resize(200, 200).centerInside().placeholder(R.drawable.ic_wait).tag(context).into(holder.viewFour);

            holder.viewOne.setOnClickListener(v -> {
                callbackEnter.enter(nowPosition);
            });

            setCheckbox(holder.cbOne, nowPosition);
            holder.viewTwo.setOnClickListener(v -> {
                callbackEnter.enter(nowPosition + 1);
            });
            setCheckbox(holder.cbTwo, nowPosition + 1);

            holder.viewThree.setOnClickListener(v -> {
                callbackEnter.enter(nowPosition + 2);
            });
            setCheckbox(holder.cbThree, nowPosition + 2);


            holder.viewFour.setOnClickListener(v -> {
                callbackEnter.enter(nowPosition + 3);
            });
            setCheckbox(holder.cbFour, nowPosition + 3);
        }
    }

    @Override
    public int getItemCount() {
        if (fileImgBeans.size() % 4 > 0) {
            return fileImgBeans.size() / 4 + 1;
        } else {
            return fileImgBeans.size() / 4;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SquareView viewOne, viewTwo, viewThree, viewFour;
        SelectCheckbox cbOne, cbTwo, cbThree, cbFour;

        public ViewHolder(@NonNull View itemView, ItemPictureDisplayBinding binding) {
            super(itemView);
            viewOne = binding.ivDisplayOne;
            viewTwo = binding.ivDisplayTwo;
            viewThree = binding.ivDisplayThree;
            viewFour = binding.ivDisplayFour;
            cbOne = binding.cbOne;
            cbTwo = binding.cbTwo;
            cbThree = binding.cbThree;
            cbFour = binding.cbFour;
        }
    }

    /**
     * 进入图片
     */
    public interface CallbackEnter {
        void enter(int position);
    }

    private void setCheckbox(SelectCheckbox cb, int nowPosition) {
        int index = 0;
        for (int i = 0; i < selectPicList.size(); i++) {
            if (selectPicList.get(i) == nowPosition) {
                index = i + 1;
            }
        }
        if (index != 0) {
            cb.setViewText(String.valueOf(index), true);
        } else {
            cb.setViewText("", false);
        }
        cb.setOnClickListener(v -> {
            if (selectPosition > maxSelect && !cb.isViewSelected()) {
                Toast.makeText(context, "预览图片已达到最高，请勿继续选取", Toast.LENGTH_SHORT).show();
            } else {
                if (cb.isViewSelected()) {
                    cb.setViewText("", false);
                    selectPicList.remove((Object) nowPosition);
                    selectViewList.remove(cb);
                    selectFileBeanList.remove(fileImgBeans.get(nowPosition));
                    selectPosition--;
                    for (int i = 0; i < selectViewList.size(); i++) {
                        SelectCheckbox selectCheckbox = selectViewList.get(i);
                        selectCheckbox.setViewText(String.valueOf(i + 1), true);
                    }
                } else {
                    cb.setViewText(String.valueOf(selectPosition), true);
                    selectPosition++;
                    selectFileBeanList.add(fileImgBeans.get(nowPosition));
                    selectPicList.add(nowPosition);
                    selectViewList.add(cb);
                }
            }


        });
    }
    public ArrayList<FileImgBean> getSelectFileBeanList(){
        return selectFileBeanList;
    }
}
