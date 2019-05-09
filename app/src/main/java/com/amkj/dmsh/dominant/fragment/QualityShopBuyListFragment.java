package com.amkj.dmsh.dominant.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amkj.dmsh.R;
import com.amkj.dmsh.base.BaseFragment;
import com.amkj.dmsh.base.TinkerBaseApplicationLike;
import com.amkj.dmsh.bean.RequestStatus;
import com.amkj.dmsh.constant.BaseAddCarProInfoBean;
import com.amkj.dmsh.constant.CommunalDetailBean;
import com.amkj.dmsh.constant.ConstantMethod;
import com.amkj.dmsh.constant.Url;
import com.amkj.dmsh.dominant.activity.QualityShopHistoryListActivity;
import com.amkj.dmsh.dominant.adapter.QualityBuyListAdapter;
import com.amkj.dmsh.dominant.adapter.QualityHistoryAdapter;
import com.amkj.dmsh.dominant.bean.QualityBuyListEntity;
import com.amkj.dmsh.dominant.bean.QualityBuyListEntity.QualityBuyListBean;
import com.amkj.dmsh.dominant.bean.QualityHistoryListEntity;
import com.amkj.dmsh.dominant.bean.QualityHistoryListEntity.QualityHistoryListBean;
import com.amkj.dmsh.dominant.bean.ShopBuyDetailEntity;
import com.amkj.dmsh.dominant.bean.ShopBuyDetailEntity.ShopBuyDetailBean;
import com.amkj.dmsh.homepage.adapter.CommunalDetailAdapter;
import com.amkj.dmsh.network.NetLoadListenerHelper;
import com.amkj.dmsh.network.NetLoadUtils;
import com.amkj.dmsh.shopdetails.activity.ShopScrollDetailsActivity;
import com.amkj.dmsh.shopdetails.bean.CommunalDetailObjectBean;
import com.amkj.dmsh.utils.glide.GlideImageLoaderUtil;
import com.amkj.dmsh.utils.itemdecoration.ItemDecoration;
import com.amkj.dmsh.utils.webformatdata.CommunalWebDetailUtils;
import com.amkj.dmsh.utils.webformatdata.ShareDataBean;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.jessyan.autosize.utils.AutoSizeUtils;
import q.rorbin.badgeview.Badge;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.amkj.dmsh.base.TinkerBaseApplicationLike.mAppContext;
import static com.amkj.dmsh.constant.ConstantMethod.getLoginStatus;
import static com.amkj.dmsh.constant.ConstantMethod.getStrings;
import static com.amkj.dmsh.constant.ConstantMethod.insertNewTotalData;
import static com.amkj.dmsh.constant.ConstantMethod.showToast;
import static com.amkj.dmsh.constant.ConstantMethod.showToastRequestMsg;
import static com.amkj.dmsh.constant.ConstantMethod.skipProductUrl;
import static com.amkj.dmsh.constant.ConstantMethod.userId;
import static com.amkj.dmsh.constant.ConstantVariable.EMPTY_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.SUCCESS_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.TOTAL_COUNT_TWENTY;
import static com.amkj.dmsh.constant.Url.QUALITY_SHOP_BUY_DETAIL;
import static com.amkj.dmsh.constant.Url.QUALITY_SHOP_HISTORY_LIST;
import static com.amkj.dmsh.constant.Url.QUALITY_SHOP_HISTORY_LIST_PRO;


/**
 * @author LGuiPeng
 * @email liuguipeng163@163.com
 * created on 2017/6/26
 * class description:必买清单
 */
public class QualityShopBuyListFragment extends BaseFragment {
    @BindView(R.id.smart_communal_refresh)
    SmartRefreshLayout smart_communal_refresh;
    @BindView(R.id.communal_recycler)
    RecyclerView communal_recycler;
    //    滚动至顶部
    @BindView(R.id.download_btn_communal)
    public FloatingActionButton download_btn_communal;
    @BindView(R.id.tv_header_title)
    TextView tv_header_titleAll;
    @BindView(R.id.iv_img_service)
    ImageView iv_img_service;
    @BindView(R.id.fl_header_service)
    FrameLayout fl_header_service;
    //    滑动布局
    @BindView(R.id.dr_communal_pro)
    DrawerLayout dr_communal_pro;
    @BindView(R.id.ll_communal_pro_list)
    LinearLayout ll_communal_pro_list;
    @BindView(R.id.tv_communal_pro_title)
    TextView tv_communal_pro_title;
    @BindView(R.id.rv_communal_pro)
    RecyclerView rv_communal_pro;
    //  打开标签
    @BindView(R.id.tv_communal_pro_tag)
    TextView tv_wel_pro_tag;
    @BindView(R.id.tl_quality_bar)
    Toolbar tl_quality_bar;
    private int page = 1;
    private int scrollY;
    private float screenHeight;
    private List<QualityBuyListBean> qualityBuyListBeanList = new ArrayList();
    private List<CommunalDetailObjectBean> itemDescriptionList = new ArrayList();
    private List<QualityHistoryListBean> historyListBeanList = new ArrayList();
    private QualityBuyListAdapter qualityBuyListAdapter;
    private CommunalDetailAdapter communalDetailAdapter;
    private ShopBuyListView shopBuyListView;
    private ShopBuyDetailBean shopBuyDetailBean;
    private Badge badge;
    private QualityHistoryAdapter qualityHistoryAdapter;
    private QualityBuyListEntity qualityBuyListEntity;

    @Override
    protected int getContentView() {
        return R.layout.activity_communal_ql_draw_refresh;
    }

    @Override
    protected void initViews() {
        tl_quality_bar.setVisibility(GONE);
        tv_header_titleAll.setText("必买清单");
        iv_img_service.setImageResource(R.drawable.shop_car_gray_icon);
        communal_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        smart_communal_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                loadData();
            }
        });
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
        communal_recycler.addItemDecoration(new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_gray_f_two_px).create());
        qualityBuyListAdapter = new QualityBuyListAdapter(getActivity(), qualityBuyListBeanList);
        qualityBuyListAdapter.setHeaderAndEmpty(true);
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_communal_detail_scroll_rec_cover_wrap, null);
        shopBuyListView = new ShopBuyListView();
        ButterKnife.bind(shopBuyListView, headerView);
        shopBuyListView.initView();
        qualityBuyListAdapter.addHeaderView(headerView);
        communal_recycler.setAdapter(qualityBuyListAdapter);
        qualityBuyListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (shopBuyDetailBean != null) {
                    page++;
                    getBuyListRecommend(shopBuyDetailBean.getId());
                }
            }
        }, communal_recycler);

        rv_communal_pro.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_communal_pro.addItemDecoration(new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_gray_f_two_px).create());
        qualityHistoryAdapter = new QualityHistoryAdapter(historyListBeanList);
        qualityHistoryAdapter.setEnableLoadMore(false);
        rv_communal_pro.setAdapter(qualityHistoryAdapter);
        qualityHistoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                QualityHistoryListBean qualityHistoryListBean = (QualityHistoryListBean) view.getTag();
                if (qualityHistoryListBean != null) {
                    dr_communal_pro.closeDrawers();
                    Intent intent = new Intent(getActivity(), QualityShopHistoryListActivity.class);
                    intent.putExtra("listId", String.valueOf(qualityHistoryListBean.getId()));
                    ConstantMethod.saveSourceId(getClass().getSimpleName(), String.valueOf(qualityHistoryListBean.getId()));
                    startActivity(intent);
                }
            }
        });
        qualityBuyListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                QualityBuyListBean qualityBuyListBean = (QualityBuyListBean) view.getTag();
                if (qualityBuyListBean != null) {
                    Intent intent = new Intent(getActivity(), ShopScrollDetailsActivity.class);
                    intent.putExtra("productId", String.valueOf(qualityBuyListBean.getId()));
                    startActivity(intent);
                }
            }
        });
        qualityBuyListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                loadHud.show();
                QualityBuyListBean qualityBuyListBean = (QualityBuyListBean) view.getTag();
                if (qualityBuyListBean != null) {
                    if (userId > 0) {
                        switch (view.getId()) {
                            case R.id.iv_ql_bl_add_car:
                                BaseAddCarProInfoBean baseAddCarProInfoBean = new BaseAddCarProInfoBean();
                                baseAddCarProInfoBean.setProductId(qualityBuyListBean.getId());
                                baseAddCarProInfoBean.setActivityCode(getStrings(qualityBuyListBean.getActivityCode()));
                                baseAddCarProInfoBean.setProName(getStrings(qualityBuyListBean.getName()));
                                baseAddCarProInfoBean.setProPic(getStrings(qualityBuyListBean.getPicUrl()));
                                ConstantMethod constantMethod = new ConstantMethod();
                                constantMethod.addShopCarGetSku(getActivity(), baseAddCarProInfoBean, loadHud);
                                constantMethod.setAddOnCarListener(new ConstantMethod.OnAddCarListener() {
                                    @Override
                                    public void onAddCarSuccess() {
                                        getCarCount();
                                    }
                                });
                                break;
                        }
                    } else {
                        loadHud.dismiss();
                        getLoginStatus(getActivity());
                    }
                }
            }
        });
        //          关闭手势滑动
        dr_communal_pro.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
        badge = ConstantMethod.getBadge(getActivity(), fl_header_service);
    }

    private void getCarCount() {
        if (userId > 0) {
            //购物车数量展示
            String url = Url.BASE_URL + Url.Q_QUERY_CAR_COUNT;
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), url, params, new NetLoadListenerHelper() {
                @Override
                public void onSuccess(String result) {
                    Gson gson = new Gson();
                    RequestStatus requestStatus = gson.fromJson(result, RequestStatus.class);
                    if (requestStatus != null) {
                        if (requestStatus.getCode().equals(SUCCESS_CODE)) {
                            int cartNumber = requestStatus.getResult().getCartNumber();
                            badge.setBadgeNumber(cartNumber);
                        } else if (!requestStatus.getCode().equals(EMPTY_CODE)) {
                            showToastRequestMsg(getActivity(), requestStatus);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void loadData() {
        page = 1;
        getCarCount();
        getBuyListDetailData();
//        获取历史清单
        getHistoryList();
    }


    @Override
    protected boolean isAddLoad() {
        return true;
    }

    private void getBuyListRecommend(int id) {
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", page);
        params.put("must_buy_id", id);
        params.put("showCount", TOTAL_COUNT_TWENTY);
        if (userId > 0) {
            params.put("uid", userId);
        }
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), QUALITY_SHOP_HISTORY_LIST_PRO
                , params, new NetLoadListenerHelper() {
                    @Override
                    public void onSuccess(String result) {
                        qualityBuyListAdapter.loadMoreComplete();
                        smart_communal_refresh.finishRefresh();
                        if (page == 1) {
                            qualityBuyListBeanList.clear();
                        }
                        Gson gson = new Gson();
                        qualityBuyListEntity = gson.fromJson(result, QualityBuyListEntity.class);
                        if (qualityBuyListEntity != null) {
                            if (qualityBuyListEntity.getCode().equals(SUCCESS_CODE)) {
                                qualityBuyListBeanList.addAll(qualityBuyListEntity.getQualityBuyListBeanList());
                            } else if (qualityBuyListEntity.getCode().equals(EMPTY_CODE)) {
                                qualityBuyListAdapter.loadMoreEnd();
                            } else {
                                showToast(getActivity(), qualityBuyListEntity.getMsg());
                            }
                            qualityBuyListAdapter.notifyDataSetChanged();
                        }
                        NetLoadUtils.getNetInstance().showLoadSirSuccess(loadService);
                    }

                    @Override
                    public void onNotNetOrException() {
                        smart_communal_refresh.finishRefresh();
                        qualityBuyListAdapter.loadMoreEnd(true);
                        NetLoadUtils.getNetInstance().showLoadSirSuccess(loadService);
                    }

                    @Override
                    public void netClose() {
                        showToast(getActivity(), R.string.unConnectedNetwork);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showToast(getActivity(), R.string.invalidData);
                    }
                });
    }

    private void getBuyListDetailData() {
        Map<String, Object> params = new HashMap<>();
        if (userId > 0) {
            params.put("uid", userId);
        }
        /**
         * 3.1.8 加入并列商品 两排 三排
         */
        params.put("version", 1);
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), QUALITY_SHOP_BUY_DETAIL, params, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                itemDescriptionList.clear();
                Gson gson = new Gson();
                ShopBuyDetailEntity shopDetailsEntity = gson.fromJson(result, ShopBuyDetailEntity.class);
                if (shopDetailsEntity != null) {
                    if (shopDetailsEntity.getCode().equals(SUCCESS_CODE)) {
                        shopBuyDetailBean = shopDetailsEntity.getShopBuyDetailBean();
                        //记录埋点参数sourceId(必买清单id)
                        ConstantMethod.saveSourceId(getSimpleName(), String.valueOf(shopBuyDetailBean.getId()));
                        List<CommunalDetailBean> descriptionBeanList = shopBuyDetailBean.getDescriptionBeanList();
                        GlideImageLoaderUtil.loadImgDynamicDrawable(getActivity(), shopBuyListView.iv_communal_cover_wrap, shopBuyDetailBean.getCoverImgUrl());
                        if (descriptionBeanList != null) {
                            itemDescriptionList.addAll(CommunalWebDetailUtils.getCommunalWebInstance().getWebDetailsFormatDataList(descriptionBeanList));
                        }
                        totalPersonalTrajectory = insertNewTotalData(getActivity(), String.valueOf(shopBuyDetailBean.getId()));
                        getBuyListRecommend(shopBuyDetailBean.getId());
                    } else if (!shopDetailsEntity.getCode().equals(EMPTY_CODE)) {
                        showToast(getActivity(), shopDetailsEntity.getMsg());
                    }
                    communalDetailAdapter.setNewData(itemDescriptionList);
                }
                smart_communal_refresh.finishRefresh();
                NetLoadUtils.getNetInstance().showLoadSirSuccess(loadService);
            }

            @Override
            public void onNotNetOrException() {
                smart_communal_refresh.finishRefresh();
                NetLoadUtils.getNetInstance().showLoadSirSuccess(loadService);
            }
        });
    }

    private void getHistoryList() {
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), QUALITY_SHOP_HISTORY_LIST, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                historyListBeanList.clear();
                QualityHistoryListEntity qualityHistoryListEntity = gson.fromJson(result, QualityHistoryListEntity.class);
                if (qualityHistoryListEntity != null) {
                    if (qualityHistoryListEntity.getCode().equals(SUCCESS_CODE)) {
                        historyListBeanList.addAll(qualityHistoryListEntity.getQualityHistoryListBeanList());
                        setHistoryListData();
                        qualityHistoryAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void setHistoryListData() {
        tv_wel_pro_tag.setVisibility(View.VISIBLE);
        ll_communal_pro_list.setVisibility(View.VISIBLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        int radius = AutoSizeUtils.mm2px(mAppContext, 50);
        drawable.setCornerRadii(new float[]{radius, radius, 0, 0, 0, 0, radius, radius});
        try {
            drawable.setColor(0xffffffff);
            drawable.setStroke(1, 0xffcccccc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_wel_pro_tag.setBackground(drawable);
        tv_wel_pro_tag.setText("历史清单");
        tv_communal_pro_title.setText("历史清单");
    }

    class ShopBuyListView {
        @BindView(R.id.communal_recycler_wrap)
        RecyclerView communal_recycler_wrap;
        @BindView(R.id.iv_communal_cover_wrap)
        ImageView iv_communal_cover_wrap;

        public void initView() {
            iv_communal_cover_wrap.setVisibility(View.VISIBLE);
            communal_recycler_wrap.setNestedScrollingEnabled(false);
            communal_recycler_wrap.setLayoutManager(new LinearLayoutManager((getActivity())));
            communalDetailAdapter = new CommunalDetailAdapter(getActivity(), itemDescriptionList);
            communal_recycler_wrap.setAdapter(communalDetailAdapter);
            communalDetailAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    ShareDataBean shareDataBean = null;
                    if (view.getId() == R.id.tv_communal_share && shopBuyDetailBean != null) {
                        shareDataBean = new ShareDataBean(shopBuyDetailBean.getCoverImgUrl()
                                , "必买清单"
                                , "集结各路口碑好货，为你精选出必买的家居、母婴优品，不踩雷，买得更顺心。"
                                , Url.BASE_SHARE_PAGE_TWO + "m/template/goods/must_buy.html");

                    }
                    CommunalWebDetailUtils.getCommunalWebInstance()
                            .setWebDataClick(getActivity(), shareDataBean, view, loadHud);
                }
            });
            communalDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    CommunalDetailObjectBean communalDetailBean = (CommunalDetailObjectBean) view.getTag();
                    if (communalDetailBean != null) {
                        skipProductUrl(getActivity(), communalDetailBean.getItemTypeId(), communalDetailBean.getId());
                    }
                }
            });
        }
    }


    @OnClick(R.id.tv_communal_pro_tag)
    void openSlideProList(View view) {
//            商品列表
        if (dr_communal_pro.isDrawerOpen(ll_communal_pro_list)) {
            dr_communal_pro.closeDrawers();
        } else {
            dr_communal_pro.openDrawer(ll_communal_pro_list);
        }
    }
}
