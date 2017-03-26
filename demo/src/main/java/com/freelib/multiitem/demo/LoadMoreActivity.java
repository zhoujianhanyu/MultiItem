package com.freelib.multiitem.demo;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.freelib.multiitem.adapter.BaseItemAdapter;
import com.freelib.multiitem.adapter.holder.BaseViewHolder;
import com.freelib.multiitem.demo.bean.ImageBean;
import com.freelib.multiitem.demo.bean.ImageTextBean;
import com.freelib.multiitem.demo.bean.TextBean;
import com.freelib.multiitem.demo.viewholder.ImageAndTextManager;
import com.freelib.multiitem.demo.viewholder.ImageViewManager;
import com.freelib.multiitem.demo.viewholder.LoadMoreHolderManager;
import com.freelib.multiitem.demo.viewholder.TextViewManager;
import com.freelib.multiitem.listener.OnItemClickListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.layout_recycler)
public class LoadMoreActivity extends AppCompatActivity {
    @ViewById(R.id.recyclerView)
    protected RecyclerView recyclerView;

    private BaseItemAdapter adapter;
    private int currPage;

    public static void startLoadMoreActivity(Context context) {
        LoadMoreActivity_.intent(context).start();
    }

    @AfterViews
    protected void initViews() {
        setTitle(R.string.load_more_title);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //初始化adapter
        adapter = new BaseItemAdapter();
        //为XXBean数据源注册XXManager管理类
        adapter.register(TextBean.class, new TextViewManager());
        adapter.register(ImageTextBean.class, new ImageAndTextManager());
        adapter.register(ImageBean.class, new ImageViewManager());
        adapter.addFootItem(new TextBean("我是Foot View"));
        recyclerView.setAdapter(adapter);
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(new TextBean("AAA" + i));
            list.add(new ImageBean(R.drawable.img1));
            list.add(new ImageTextBean(R.drawable.img2, "BBB" + i));
        }
        adapter.setDataItems(list);

        adapter.enableLoadMore(new LoadMoreHolderManager(this::loadMoreData));
    }

    /**
     * 模拟加载数据
     * 数据加载时间模拟延时1秒
     * 前两次成功
     * 第三次加载失败
     * 第四次加载成功，并加载数据完成
     */
    private void loadMoreData() {
        new Handler().postDelayed(() -> {
            if (currPage < 2) {
                for (int i = 0; i < 10; i++) {
                    adapter.addDataItem(new TextBean("我是后加的" + i));
                }
                adapter.setLoadCompleted(false);
            } else if (currPage == 2) {
                adapter.setLoadFailed();
            } else {
                adapter.setLoadCompleted(true);
            }
            currPage++;
        }, 1000);
    }

}

