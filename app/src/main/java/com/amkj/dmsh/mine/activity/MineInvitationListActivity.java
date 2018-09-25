package com.amkj.dmsh.mine.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.amkj.dmsh.R;
import com.amkj.dmsh.base.BaseActivity;
import com.amkj.dmsh.base.NetLoadUtils;
import com.amkj.dmsh.base.TinkerBaseApplicationLike;
import com.amkj.dmsh.bean.RequestStatus;
import com.amkj.dmsh.constant.ConstantMethod;
import com.amkj.dmsh.constant.ConstantVariable;
import com.amkj.dmsh.constant.Url;
import com.amkj.dmsh.constant.XUtil;
import com.amkj.dmsh.find.activity.ArticleDetailsImgActivity;
import com.amkj.dmsh.find.activity.ArticleInvitationDetailsActivity;
import com.amkj.dmsh.find.adapter.PullUserInvitationAdapter;
import com.amkj.dmsh.homepage.bean.InvitationDetailEntity;
import com.amkj.dmsh.homepage.bean.InvitationDetailEntity.InvitationDetailBean;
import com.amkj.dmsh.release.dialogutils.AlertSettingBean;
import com.amkj.dmsh.release.dialogutils.AlertView;
import com.amkj.dmsh.release.dialogutils.OnAlertItemClickListener;
import com.amkj.dmsh.utils.inteface.MyCallBack;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tencent.bugly.beta.tinker.TinkerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.amkj.dmsh.base.TinkerBaseApplicationLike.mAppContext;
import static com.amkj.dmsh.constant.ConstantMethod.getLoginStatus;
import static com.amkj.dmsh.constant.ConstantMethod.showToast;
import static com.amkj.dmsh.constant.ConstantMethod.userId;
import static com.amkj.dmsh.constant.ConstantVariable.DEFAULT_TOTAL_COUNT;
import static com.amkj.dmsh.constant.ConstantVariable.EMPTY_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.IS_LOGIN_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.SUCCESS_CODE;

;
;


/**
 * @author LGuiPeng
 * @email liuguipeng163@163.com
 * created on 2017/8/24
 * class description:我的帖子列表
 */
public class MineInvitationListActivity extends BaseActivity implements OnAlertItemClickListener {
    @BindView(R.id.smart_communal_refresh)
    SmartRefreshLayout smart_communal_refresh;
    @BindView(R.id.communal_recycler)
    RecyclerView communal_recycler;
    //    滚动至顶部
    @BindView(R.id.download_btn_communal)
    public FloatingActionButton download_btn_communal;
    @BindView(R.id.tv_header_shared)
    TextView header_shared;
    @BindView(R.id.tv_header_title)
    TextView tv_header_titleAll;
    private int page = 1;
    private int scrollY = 0;
    private float screenHeight;
    private PullUserInvitationAdapter adapterInvitationAdapter;
    private List<InvitationDetailBean> invitationDetailList = new ArrayList();
    private String type = "帖子";
    private AlertView delArticleDialog;
    private InvitationDetailBean invitationDetailBean;
    private InvitationDetailEntity invitationDetailEntity;

    @Override
    protected int getContentView() {
        return R.layout.layout_communal_refresh_floating_header;
    }

    @Override
    protected void initViews() {
        getLoginStatus(this);
        header_shared.setVisibility(View.INVISIBLE);
        tv_header_titleAll.setText("我的帖子");
        communal_recycler.setLayoutManager(new LinearLayoutManager(MineInvitationListActivity.this));
        smart_communal_refresh.setOnRefreshListener((refreshLayout) -> {
            loadData();
        });
        adapterInvitationAdapter = new PullUserInvitationAdapter(MineInvitationListActivity.this, invitationDetailList, type);
        communal_recycler.setAdapter(adapterInvitationAdapter);
        communal_recycler.addItemDecoration(new PinnedHeaderItemDecoration.Builder(-1)
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_ten_dp)
                // 开启绘制分隔线，默认关闭
                .enableDivider(true)
                // 是否关闭标签点击事件，默认开启
                .disableHeaderClick(false)
                // 设置标签和其内部的子控件的监听，若设置点击监听不为null，但是disableHeaderClick(true)的话，还是不会响应点击事件
                .setHeaderClickListener(null)
                .create());
        adapterInvitationAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                InvitationDetailBean invitationDetailBean = (InvitationDetailBean) view.getTag();
                if (invitationDetailBean != null) {
                    Intent intent = new Intent();
                    switch (invitationDetailBean.getArticletype()) {
                        case 1:
                            intent.setClass(MineInvitationListActivity.this, ArticleInvitationDetailsActivity.class);
                            break;
                        case 3:
                            break;
                        default:
                            intent.setClass(MineInvitationListActivity.this, ArticleDetailsImgActivity.class);
                            break;
                    }
                    intent.putExtra("ArtId", String.valueOf(invitationDetailBean.getId()));
                    startActivity(intent);
                }
            }
        });
        adapterInvitationAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                invitationDetailBean = (InvitationDetailBean) view.getTag();
                if (invitationDetailBean != null) {
                    switch (view.getId()) {
                        case R.id.tv_com_art_collect_count:
                            if (userId > 0) {
                                loadHud.show();
                                setArticleCollect(invitationDetailBean, view);
                            } else {
                                getLoginStatus(MineInvitationListActivity.this);
                            }
                            break;
                        case R.id.tv_com_art_like_count:
                            if (userId > 0) {
                                setArticleLiked(invitationDetailBean, view);
                            } else {
                                getLoginStatus(MineInvitationListActivity.this);
                            }
                            break;
//                        删除帖子
                        case R.id.tv_inv_live_att:
                            AlertSettingBean alertSettingBean = new AlertSettingBean();
                            AlertSettingBean.AlertData alertData = new AlertSettingBean.AlertData();
                            alertData.setCancelStr("取消");
                            alertData.setDetermineStr("确定");
                            alertData.setFirstDet(true);
                            alertData.setMsg("确定删除该篇帖子？");
                            alertSettingBean.setStyle(AlertView.Style.Alert);
                            alertSettingBean.setAlertData(alertData);
                            delArticleDialog = new AlertView(alertSettingBean, MineInvitationListActivity.this, MineInvitationListActivity.this);
                            delArticleDialog.show();
                            delArticleDialog.setCancelable(false);
                            break;
                    }
                }
            }
        });

        download_btn_communal.attachToRecyclerView(communal_recycler, null, new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                scrollY += dy;
                if (!recyclerView.canScrollVertically(-1)) {
                    scrollY = 0;
                }
                if (screenHeight == 0) {
                    TinkerBaseApplicationLike app = (TinkerBaseApplicationLike) TinkerManager.getTinkerApplicationLike();
                    screenHeight = app.getScreenHeight();
                }
                if (scrollY >= screenHeight) {
                    download_btn_communal.setVisibility(View.VISIBLE);
                } else {
                    download_btn_communal.setVisibility(View.GONE);
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
        adapterInvitationAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (invitationDetailList.size() >= page * DEFAULT_TOTAL_COUNT) {
                    page++;
                    getMyInvitationData();
                } else {
                    adapterInvitationAdapter.loadMoreEnd();
                }
            }
        }, communal_recycler);
    }

    @Override
    protected void loadData() {
        page = 1;
        getMyInvitationData();
    }

    @Override
    protected View getLoadView() {
        return smart_communal_refresh;
    }

    @Override
    protected boolean isAddLoad() {
        return true;
    }

    private void getMyInvitationData() {
        String url = Url.BASE_URL + Url.MINE_INVITATION_LIST;
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", page);
        params.put("showCount", DEFAULT_TOTAL_COUNT);
        params.put("uid", userId);
        params.put("version", 1);
        NetLoadUtils.getQyInstance().loadNetDataPost(mAppContext, url, params, new NetLoadUtils.NetLoadListener() {
            @Override
            public void onSuccess(String result) {
                smart_communal_refresh.finishRefresh();
                adapterInvitationAdapter.loadMoreComplete();
                if (page == 1) {
                    invitationDetailList.clear();
                }
                Gson gson = new Gson();
                invitationDetailEntity = gson.fromJson(result, InvitationDetailEntity.class);
                if (invitationDetailEntity != null) {
                    if (invitationDetailEntity.getCode().equals(SUCCESS_CODE)) {
                        invitationDetailList.addAll(invitationDetailEntity.getInvitationSearchList());
                    } else if (!invitationDetailEntity.getCode().equals(EMPTY_CODE)) {
                        showToast(MineInvitationListActivity.this, invitationDetailEntity.getMsg());
                    }
                    adapterInvitationAdapter.notifyDataSetChanged();
                }
                NetLoadUtils.getQyInstance().showLoadSir(loadService,invitationDetailList,invitationDetailEntity);
            }

            @Override
            public void netClose() {
                smart_communal_refresh.finishRefresh();
                adapterInvitationAdapter.loadMoreComplete();
                NetLoadUtils.getQyInstance().showLoadSir(loadService,invitationDetailList,invitationDetailEntity);
            }

            @Override
            public void onError(Throwable throwable) {
                smart_communal_refresh.finishRefresh();
                adapterInvitationAdapter.loadMoreComplete();
                NetLoadUtils.getQyInstance().showLoadSir(loadService,invitationDetailList,invitationDetailEntity);
            }
        });
    }

    //    文章收藏
    private void setArticleCollect(final InvitationDetailBean invitationDetailBean, View view) {
        final TextView tv_collect = (TextView) view;
        String url = Url.BASE_URL + Url.F_ARTICLE_COLLECT;
        Map<String, Object> params = new HashMap<>();
        //用户id
        params.put("uid", userId);
        //文章id
        params.put("object_id", invitationDetailBean.getId());
        params.put("type", ConstantVariable.TYPE_C_ARTICLE);
        XUtil.Post(url, params, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                loadHud.dismiss();
                Gson gson = new Gson();
                RequestStatus requestStatus = gson.fromJson(result, RequestStatus.class);
                if (requestStatus != null) {
                    if (requestStatus.getCode().equals("01")) {
                        tv_collect.setSelected(!tv_collect.isSelected());
                        tv_collect.setText(ConstantMethod.getNumCount(tv_collect.isSelected(), invitationDetailBean.isCollect(), invitationDetailBean.getCollect(), "收藏"));
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                loadHud.dismiss();
                showToast(MineInvitationListActivity.this, String.format(getResources().getString(R.string.collect_failed), "文章"));
                super.onError(ex, isOnCallback);
            }
        });
    }

    private void setArticleLiked(InvitationDetailBean invitationDetailBean, View view) {
        TextView tv_like = (TextView) view;
        String url = Url.BASE_URL + Url.F_ARTICLE_DETAILS_FAVOR;
        Map<String, Object> params = new HashMap<>();
        //用户id
        params.put("tuid", userId);
        //关注id
        params.put("id", invitationDetailBean.getId());
        params.put("favortype", "doc");
        XUtil.Post(url, params, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
            }
        });
        tv_like.setSelected(!tv_like.isSelected());
        tv_like.setText(ConstantMethod.getNumCount(tv_like.isSelected(), invitationDetailBean.isFavor(), invitationDetailBean.getFavor(), "赞"));
    }

    private void delArticle() {
        String url = Url.BASE_URL + Url.MINE_INVITATION_DEL;
        Map<String, Object> params = new HashMap<>();
        params.put("id", invitationDetailBean.getId());
        params.put("uid", userId);
        XUtil.Post(url, params, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                RequestStatus requestStatus = gson.fromJson(result, RequestStatus.class);
                if (requestStatus != null) {
                    if (requestStatus.getCode().equals("01")) {
                        showToast(MineInvitationListActivity.this, "删除帖子完成");
                        page = 1;
                        loadData();
                    } else {
                        showToast(MineInvitationListActivity.this, requestStatus.getMsg());
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IS_LOGIN_CODE) {
            loadData();
        }
    }

    @OnClick(R.id.tv_life_back)
    void goBack(View view) {
        finish();
    }

    @Override
    public void onAlertItemClick(Object o, int position) {
        if (o.equals(delArticleDialog) && position != AlertView.CANCELPOSITION) {
            delArticle();
        }
    }
}
