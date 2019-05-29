package com.amkj.dmsh.dominant.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.amkj.dmsh.R;
import com.amkj.dmsh.base.BaseActivity;
import com.amkj.dmsh.base.TinkerBaseApplicationLike;
import com.amkj.dmsh.constant.ConstantMethod;
import com.amkj.dmsh.constant.Url;
import com.amkj.dmsh.dominant.adapter.QualityGroupShopAdapter;
import com.amkj.dmsh.dominant.bean.QualityGroupEntity;
import com.amkj.dmsh.dominant.bean.QualityGroupEntity.QualityGroupBean;
import com.amkj.dmsh.network.NetLoadListenerHelper;
import com.amkj.dmsh.network.NetLoadUtils;
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
import static com.amkj.dmsh.constant.ConstantMethod.showToast;
import static com.amkj.dmsh.constant.ConstantVariable.EMPTY_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.SUCCESS_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.TOTAL_COUNT_TWENTY;

/**
 * @author LGuiPeng
 * @email liuguipeng163@163.com
 * created on 2017/6/8
 * class description:全部拼团
 */
public class QualityGroupShopAllActivity extends BaseActivity {
    @BindView(R.id.smart_communal_refresh)
    SmartRefreshLayout smart_communal_refresh;
    @BindView(R.id.communal_recycler)
    RecyclerView communal_recycler;
    //    滚动至顶部
    @BindView(R.id.download_btn_communal)
    public FloatingActionButton download_btn_communal;
    @BindView(R.id.tv_header_title)
    TextView tv_header_titleAll;
    @BindView(R.id.tv_header_shared)
    TextView tv_header_shared;
    private int page = 1;
    private int scrollY;
    private float screenHeight;
    private List<QualityGroupBean> qualityGroupBeanList = new ArrayList();
    private QualityGroupShopAdapter qualityGroupShopAdapter;
    private QualityGroupEntity qualityGroupEntity;
    private ConstantMethod constantMethod;

    @Override
    protected int getContentView() {
        return R.layout.activity_ql_gp_sp_list;
    }

    @Override
    protected void initViews() {
        constantMethod = new ConstantMethod();
        tv_header_titleAll.setText("全部拼团");
        tv_header_shared.setVisibility(View.GONE);
        communal_recycler.setLayoutManager(new LinearLayoutManager(QualityGroupShopAllActivity.this));
        qualityGroupShopAdapter = new QualityGroupShopAdapter(QualityGroupShopAllActivity.this, constantMethod, qualityGroupBeanList);
        communal_recycler.addItemDecoration(new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_five_dp).create());
        communal_recycler.setAdapter(qualityGroupShopAdapter);

        smart_communal_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                loadData();
            }
        });
        qualityGroupShopAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                QualityGroupBean qualityGroupBean = (QualityGroupBean) view.getTag();
                if (qualityGroupBean != null) {
                    Intent intent = new Intent(QualityGroupShopAllActivity.this, QualityGroupShopDetailActivity.class);
                    intent.putExtra("gpInfoId", String.valueOf(qualityGroupBean.getGpInfoId()));
                    startActivity(intent);
                }
            }
        });
        qualityGroupShopAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
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
        qualityGroupShopAdapter.openLoadAnimation(null);
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
        String url = Url.BASE_URL + Url.GROUP_SHOP_JOIN_ALL;
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", page);
        params.put("showCount", TOTAL_COUNT_TWENTY);
//            区分新人团
        params.put("version", 1);
        NetLoadUtils.getNetInstance().loadNetDataPost(QualityGroupShopAllActivity.this, url,
                params, new NetLoadListenerHelper() {
                    @Override
                    public void onSuccess(String result) {
                        smart_communal_refresh.finishRefresh();
                        qualityGroupShopAdapter.loadMoreComplete();
                        if (page == 1) {
                            qualityGroupBeanList.clear();
                        }
                        Gson gson = new Gson();
                        qualityGroupEntity = gson.fromJson(result, QualityGroupEntity.class);
                        if (qualityGroupEntity != null) {
                            if (qualityGroupEntity.getCode().equals(SUCCESS_CODE)) {
                                for (int i = 0; i < qualityGroupEntity.getQualityGroupBeanList().size(); i++) {
                                    QualityGroupBean qualityGroupBean = qualityGroupEntity.getQualityGroupBeanList().get(i);
                                    qualityGroupBean.setCurrentTime(qualityGroupEntity.getCurrentTime());
                                    qualityGroupBeanList.add(qualityGroupBean);
                                }
                            } else if (qualityGroupEntity.getCode().equals(EMPTY_CODE)) {
                                qualityGroupShopAdapter.loadMoreEnd();
                            } else {
                                showToast(QualityGroupShopAllActivity.this, qualityGroupEntity.getMsg());
                            }
                            qualityGroupShopAdapter.notifyDataSetChanged();
                        }
                        NetLoadUtils.getNetInstance().showLoadSir(loadService, qualityGroupBeanList, qualityGroupEntity);
                    }

                    @Override
                    public void onNotNetOrException() {
                        smart_communal_refresh.finishRefresh();
                        qualityGroupShopAdapter.loadMoreEnd(true);
                        NetLoadUtils.getNetInstance().showLoadSir(loadService, qualityGroupBeanList, qualityGroupEntity);
                    }

                    @Override
                    public void netClose() {
                        showToast(QualityGroupShopAllActivity.this, R.string.unConnectedNetwork);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showToast(QualityGroupShopAllActivity.this, R.string.connectedFaile);
                    }
                });
    }

    @OnClick(R.id.tv_life_back)
    void goBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (constantMethod != null) {
            constantMethod.stopSchedule();
            constantMethod.releaseHandlers();
        }
    }
}
