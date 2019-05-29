package com.amkj.dmsh.message.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.amkj.dmsh.R;
import com.amkj.dmsh.base.BaseActivity;
import com.amkj.dmsh.base.TinkerBaseApplicationLike;
import com.amkj.dmsh.bean.ArticleCommentEntity;
import com.amkj.dmsh.bean.ArticleCommentEntity.ArticleCommentBean;
import com.amkj.dmsh.dominant.activity.ShopTimeScrollDetailsActivity;
import com.amkj.dmsh.find.activity.ArticleDetailsImgActivity;
import com.amkj.dmsh.homepage.activity.ArticleOfficialActivity;
import com.amkj.dmsh.message.adapter.MessageCommunalAdapterNew;
import com.amkj.dmsh.network.NetLoadListenerHelper;
import com.amkj.dmsh.network.NetLoadUtils;
import com.amkj.dmsh.user.activity.UserPagerActivity;
import com.amkj.dmsh.utils.itemdecoration.ItemDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.bugly.beta.tinker.TinkerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.amkj.dmsh.constant.ConstantMethod.getLoginStatus;
import static com.amkj.dmsh.constant.ConstantMethod.showToast;
import static com.amkj.dmsh.constant.ConstantMethod.userId;
import static com.amkj.dmsh.constant.ConstantVariable.EMPTY_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.IS_LOGIN_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.SUCCESS_CODE;
import static com.amkj.dmsh.constant.Url.H_MES_LIKED;

;

/**
 * Created by atd48 on 2016/7/11.
 */
public class MessageLikedActivity extends BaseActivity {
    @BindView(R.id.tv_header_title)
    TextView tv_header_titleAll;
    @BindView(R.id.tv_header_shared)
    TextView header_shared;
    @BindView(R.id.smart_communal_refresh)
    SmartRefreshLayout smart_communal_refresh;
    @BindView(R.id.communal_recycler)
    RecyclerView communal_recycler;
    //    滚动至顶部
    @BindView(R.id.download_btn_communal)
    public FloatingActionButton download_btn_communal;

    private List<ArticleCommentBean> articleCommentList = new ArrayList();
    public static final String TYPE = "message_liked";
    private int page = 1;
    private int scrollY = 0;
    private float screenHeight;
    private MessageCommunalAdapterNew messageCommunalAdapter;
    private ArticleCommentEntity articleCommentEntity;

    @Override
    protected int getContentView() {
        return R.layout.activity_message_communal;
    }

    @Override
    protected void initViews() {
        getLoginStatus(this);
        tv_header_titleAll.setText("赞");
        header_shared.setVisibility(View.INVISIBLE);
        messageCommunalAdapter = new MessageCommunalAdapterNew(MessageLikedActivity.this, articleCommentList, TYPE);
        //获取头部消息列表
        communal_recycler.setLayoutManager(new LinearLayoutManager(this));
        communal_recycler.addItemDecoration(new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_five_dp)


                .create());
        communal_recycler.setAdapter(messageCommunalAdapter);

        smart_communal_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                loadData();
            }
        });
        messageCommunalAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getData();
            }
        }, communal_recycler);
        TinkerBaseApplicationLike app = (TinkerBaseApplicationLike) TinkerManager.getTinkerApplicationLike();
        screenHeight = app.getScreenHeight();
        communal_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                scrollY += dy;
                if (!recyclerView.canScrollVertically(-1)) {
                    scrollY = 0;
                }
                if (scrollY > screenHeight * 1.5 && dy < 0) {
                    if (download_btn_communal.getVisibility() == GONE) {
                        download_btn_communal.setVisibility(VISIBLE);
                        download_btn_communal.hide(false);
                    }
                    if (!download_btn_communal.isVisible()) {
                        download_btn_communal.show();
                    }
                } else {
                    if (download_btn_communal.isVisible()) {
                        download_btn_communal.hide();
                    }
                }
            }
        });
        download_btn_communal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) communal_recycler.getLayoutManager();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int mVisibleCount = linearLayoutManager.findLastVisibleItemPosition()
                        - linearLayoutManager.findFirstVisibleItemPosition() + 1;
                if (firstVisibleItemPosition > mVisibleCount) {
                    communal_recycler.scrollToPosition(mVisibleCount);
                }
                communal_recycler.smoothScrollToPosition(0);
            }
        });
        messageCommunalAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ArticleCommentBean articleCommentBean = (ArticleCommentBean) view.getTag(R.id.iv_avatar_tag);
                if (articleCommentBean == null) {
                    articleCommentBean = (ArticleCommentBean) view.getTag();
                }
                if (articleCommentBean != null) {
                    Intent intent = new Intent();
                    switch (view.getId()) {
                        case R.id.iv_inv_user_avatar:
                            intent.setClass(MessageLikedActivity.this, UserPagerActivity.class);
                            intent.putExtra("userId", String.valueOf(articleCommentBean.getUid()));
                            startActivity(intent);
                            break;
                        case R.id.tv_user_product_description:
                            if (articleCommentBean.getStatus() == 1) {
                                switch (articleCommentBean.getObj()) {
                                    case "doc":
                                        intent.setClass(MessageLikedActivity.this, ArticleOfficialActivity.class);
                                        intent.putExtra("ArtId", String.valueOf(articleCommentBean.getObject_id()));
                                        startActivity(intent);
                                        break;
                                    case "post":
                                        intent.setClass(MessageLikedActivity.this, ArticleDetailsImgActivity.class);
                                        intent.putExtra("ArtId", String.valueOf(articleCommentBean.getObject_id()));
                                        startActivity(intent);
                                        break;
                                    case "goods":
                                        intent.setClass(MessageLikedActivity.this, ShopTimeScrollDetailsActivity.class);
                                        intent.putExtra("productId", String.valueOf(articleCommentBean.getObject_id()));
                                        startActivity(intent);
                                        break;
                                }
                            } else {
                                showToast(MessageLikedActivity.this, "已删除");
                            }
                    }
                }
            }
        });
    }

    @OnClick(R.id.tv_life_back)
    void finish(View view) {
        finish();
    }

    @Override
    protected void loadData() {
        page = 1;
        getData();
    }

    @Override
    public View getLoadView() {
        return smart_communal_refresh;
    }

    @Override
    protected boolean isAddLoad() {
        return true;
    }

    @Override
    protected void getData() {
        if (userId < 1) {
            NetLoadUtils.getNetInstance().showLoadSirLoadFailed(loadService);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("to_uid", userId);
        params.put("currentPage", page);
        NetLoadUtils.getNetInstance().loadNetDataPost(MessageLikedActivity.this, H_MES_LIKED
                , params, new NetLoadListenerHelper() {
                    @Override
                    public void onSuccess(String result) {
                        smart_communal_refresh.finishRefresh();
                        messageCommunalAdapter.loadMoreComplete();
                        if (page == 1) {
                            articleCommentList.clear();
                        }
                        Gson gson = new Gson();
                        //赞
                        articleCommentEntity = gson.fromJson(result, ArticleCommentEntity.class);
                        if (articleCommentEntity != null) {
                            if (articleCommentEntity.getCode().equals(SUCCESS_CODE)) {
                                articleCommentList.addAll(articleCommentEntity.getArticleCommentList());
                            } else if (articleCommentEntity.getCode().equals(EMPTY_CODE)) {
                                messageCommunalAdapter.loadMoreEnd();
                            }else{
                                showToast(MessageLikedActivity.this, articleCommentEntity.getMsg());
                            }
                        }
                        messageCommunalAdapter.notifyDataSetChanged();
                        NetLoadUtils.getNetInstance().showLoadSir(loadService, articleCommentList, articleCommentEntity);
                    }

                    @Override
                    public void onNotNetOrException() {
                        smart_communal_refresh.finishRefresh();
                        messageCommunalAdapter.loadMoreEnd(true);
                        NetLoadUtils.getNetInstance().showLoadSir(loadService, articleCommentList, articleCommentEntity);
                    }

                    @Override
                    public void netClose() {
                        showToast(MessageLikedActivity.this, R.string.unConnectedNetwork);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showToast(MessageLikedActivity.this, R.string.invalidData);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            if (requestCode == IS_LOGIN_CODE) {
                finish();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IS_LOGIN_CODE) {
            loadData();
        }
    }
}
