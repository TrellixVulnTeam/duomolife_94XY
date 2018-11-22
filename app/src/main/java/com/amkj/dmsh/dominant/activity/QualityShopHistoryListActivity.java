package com.amkj.dmsh.dominant.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.trade.biz.AlibcConstants;
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback;
import com.amkj.dmsh.R;
import com.amkj.dmsh.base.BaseActivity;
import com.amkj.dmsh.base.NetLoadUtils;
import com.amkj.dmsh.base.TinkerBaseApplicationLike;
import com.amkj.dmsh.bean.RequestStatus;
import com.amkj.dmsh.constant.BaseAddCarProInfoBean;
import com.amkj.dmsh.constant.CommunalDetailBean;
import com.amkj.dmsh.constant.ConstantMethod;
import com.amkj.dmsh.constant.UMShareAction;
import com.amkj.dmsh.constant.Url;
import com.amkj.dmsh.constant.XUtil;
import com.amkj.dmsh.dominant.adapter.QualityBuyListAdapter;
import com.amkj.dmsh.dominant.bean.QualityBuyListEntity;
import com.amkj.dmsh.dominant.bean.QualityBuyListEntity.QualityBuyListBean;
import com.amkj.dmsh.dominant.bean.ShopBuyDetailEntity;
import com.amkj.dmsh.dominant.bean.ShopBuyDetailEntity.ShopBuyDetailBean;
import com.amkj.dmsh.homepage.adapter.CommunalDetailAdapter;
import com.amkj.dmsh.mine.activity.ShopCarActivity;
import com.amkj.dmsh.shopdetails.activity.ShopScrollDetailsActivity;
import com.amkj.dmsh.shopdetails.bean.CommunalDetailObjectBean;
import com.amkj.dmsh.utils.Log;
import com.amkj.dmsh.utils.glide.GlideImageLoaderUtil;
import com.amkj.dmsh.utils.inteface.MyCallBack;
import com.amkj.dmsh.utils.itemdecoration.ItemDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tencent.bugly.beta.tinker.TinkerManager;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.badgeview.Badge;

import static com.amkj.dmsh.constant.ConstantMethod.getLoginStatus;
import static com.amkj.dmsh.constant.ConstantMethod.getStrings;
import static com.amkj.dmsh.constant.ConstantMethod.insertNewTotalData;
import static com.amkj.dmsh.constant.ConstantMethod.showToast;
import static com.amkj.dmsh.constant.ConstantMethod.userId;
import static com.amkj.dmsh.constant.ConstantVariable.EMPTY_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.SUCCESS_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.TOTAL_COUNT_TWENTY;
import static com.amkj.dmsh.shopdetails.bean.CommunalDetailObjectBean.TYPE_COUPON;
import static com.amkj.dmsh.shopdetails.bean.CommunalDetailObjectBean.TYPE_COUPON_PACKAGE;

;


/**
 * @author LGuiPeng
 * @email liuguipeng163@163.com
 * created on 2017/10/10
 * class description:历史清单
 */
public class QualityShopHistoryListActivity extends BaseActivity {
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
    private List<QualityBuyListBean> qualityBuyListBeanList = new ArrayList();
    private List<CommunalDetailObjectBean> itemDescriptionList = new ArrayList();
    private QualityBuyListAdapter qualityBuyListAdapter;
    private CommunalDetailAdapter communalDetailAdapter;
    private ShopBuyListView shopBuyListView;
    private ShopBuyDetailBean shopBuyDetailBean;
    private int page = 1;
    private int scrollY;
    private float screenHeight;
    private Badge badge;
    private String listId;
    private QualityBuyListEntity qualityBuyListEntity;

    @Override
    protected int getContentView() {
        return R.layout.activity_communal_ql_shop_car;
    }

    @Override
    protected void initViews() {
        Intent intent = getIntent();
        listId = intent.getStringExtra("listId");
        if (TextUtils.isEmpty(listId)) {
            showToast(this, R.string.list_date_due);
            finish();
            overridePendingTransition(0, 0);
        }
        tv_header_titleAll.setText("");
        iv_img_service.setImageResource(R.drawable.shop_car_gray_icon);
        communal_recycler.setLayoutManager(new LinearLayoutManager(QualityShopHistoryListActivity.this));
        communal_recycler.addItemDecoration(new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_gray_f_two_px)






                .create());
        qualityBuyListAdapter = new QualityBuyListAdapter(QualityShopHistoryListActivity.this, qualityBuyListBeanList);
        qualityBuyListAdapter.setHeaderAndEmpty(true);
        View headerView = LayoutInflater.from(QualityShopHistoryListActivity.this).inflate(R.layout.layout_communal_detail_scroll_rec_cover_wrap, null);
        shopBuyListView = new ShopBuyListView();
        ButterKnife.bind(shopBuyListView, headerView);
        shopBuyListView.initView();
        qualityBuyListAdapter.addHeaderView(headerView);
        communal_recycler.setAdapter(qualityBuyListAdapter);
        qualityBuyListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (page * TOTAL_COUNT_TWENTY <= qualityBuyListBeanList.size()) {
                    page++;
                    getBuyListRecommend();
                } else {
                    qualityBuyListAdapter.loadMoreEnd();
                    qualityBuyListAdapter.setEnableLoadMore(false);
                }
            }
        }, communal_recycler);
        communalDetailAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (loadHud != null) {
                    loadHud.show();
                }
                switch (view.getId()) {
                    case R.id.img_product_coupon_pic:
                        int couponId = (int) view.getTag(R.id.iv_avatar_tag);
                        int type = (int) view.getTag(R.id.iv_type_tag);
                        if (couponId > 0) {
                            if (userId != 0) {
                                if (type == TYPE_COUPON) {
                                    getDirectCoupon(couponId);
                                } else if (type == TYPE_COUPON_PACKAGE) {
                                    getDirectCouponPackage(couponId);
                                }
                            } else {
                                if (loadHud != null) {
                                    loadHud.dismiss();
                                }
                                getLoginStatus(QualityShopHistoryListActivity.this);
                            }
                        }
                        break;
                    case R.id.iv_communal_cover_wrap:
                        CommunalDetailObjectBean detailObjectBean = (CommunalDetailObjectBean) view.getTag(R.id.iv_tag);
                        if (detailObjectBean != null) {
                            if (detailObjectBean.getItemType() == CommunalDetailObjectBean.TYPE_GOODS_IMG) {
                                Intent intent = new Intent(QualityShopHistoryListActivity.this, ShopScrollDetailsActivity.class);
                                intent.putExtra("productId", String.valueOf(detailObjectBean.getId()));
                                startActivity(intent);
                            }
                        }
                        loadHud.dismiss();
                        break;

                    case R.id.ll_layout_tb_coupon:
                        CommunalDetailObjectBean couponBean = (CommunalDetailObjectBean) view.getTag();
                        if (couponBean != null) {
                            if (loadHud != null) {
                                loadHud.dismiss();
                            }
                            if (userId != 0) {
                                skipAliBCWebView(couponBean.getCouponUrl());
                            } else {
                                if (loadHud != null) {
                                    loadHud.dismiss();
                                }
                                getLoginStatus(QualityShopHistoryListActivity.this);
                            }
                        }
                        break;

                }
            }
        });

        communalDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CommunalDetailObjectBean communalDetailBean = (CommunalDetailObjectBean) view.getTag();
                if (communalDetailBean != null) {
                    ConstantMethod.skipProductUrl(QualityShopHistoryListActivity.this, communalDetailBean.getItemTypeId(), communalDetailBean.getId());
                }
            }
        });
        qualityBuyListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                QualityBuyListBean qualityBuyListBean = (QualityBuyListBean) view.getTag();
                if (qualityBuyListBean != null) {
                    Intent intent = new Intent(QualityShopHistoryListActivity.this, ShopScrollDetailsActivity.class);
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
                                constantMethod.addShopCarGetSku(QualityShopHistoryListActivity.this, baseAddCarProInfoBean, loadHud);
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
                        getLoginStatus(QualityShopHistoryListActivity.this);
                    }
                }
            }
        });

        smart_communal_refresh.setOnRefreshListener((refreshLayout) -> {
            loadData();
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
        badge = ConstantMethod.getBadge(QualityShopHistoryListActivity.this, fl_header_service);
        totalPersonalTrajectory = insertNewTotalData(QualityShopHistoryListActivity.this, listId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCarCount();
    }

    private void getCarCount() {
        if (userId > 0) {
            //购物车数量展示
            String url = Url.BASE_URL + Url.Q_QUERY_CAR_COUNT;
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            XUtil.Post(url, params, new MyCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    Gson gson = new Gson();
                    RequestStatus requestStatus = gson.fromJson(result, RequestStatus.class);
                    if (requestStatus != null) {
                        if (requestStatus.getCode().equals("01")) {
                            int cartNumber = requestStatus.getResult().getCartNumber();
                            badge.setBadgeNumber(cartNumber);
                        } else if (!requestStatus.getCode().equals("02")) {
                            showToast(QualityShopHistoryListActivity.this, requestStatus.getMsg());
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        try {
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadData() {
        page = 1;
        getCarCount();
        getBuyListDetailData();
        getBuyListRecommend();
    }

    @Override
    protected View getLoadView() {
        return smart_communal_refresh;
    }

    @Override
    protected boolean isAddLoad() {
        return true;
    }

    private void getBuyListRecommend() {
        String url = Url.BASE_URL + Url.QUALITY_SHOP_HISTORY_LIST_PRO;
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", page);
        params.put("showCount", TOTAL_COUNT_TWENTY);
        params.put("must_buy_id", listId);
        NetLoadUtils.getQyInstance().loadNetDataPost(QualityShopHistoryListActivity.this, url
                , params, new NetLoadUtils.NetLoadListener() {
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
                            }
                            qualityBuyListAdapter.notifyDataSetChanged();
                        }
                        NetLoadUtils.getQyInstance().showLoadSirSuccess(loadService);
                    }

                    @Override
                    public void netClose() {
                        smart_communal_refresh.finishRefresh();
                        qualityBuyListAdapter.loadMoreComplete();
                        NetLoadUtils.getQyInstance().showLoadSirSuccess(loadService);
                        showToast(QualityShopHistoryListActivity.this, R.string.unConnectedNetwork);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        smart_communal_refresh.finishRefresh();
                        qualityBuyListAdapter.loadMoreComplete();
                        NetLoadUtils.getQyInstance().showLoadSirSuccess(loadService);
                        showToast(QualityShopHistoryListActivity.this, R.string.invalidData);
                    }
                });
    }

    private void getBuyListDetailData() {
        String url = Url.BASE_URL + Url.QUALITY_SHOP_HISTORY_LIST_DETAIL;
        Map<String, Object> params = new HashMap<>();
        params.put("id", listId);
        if (userId > 0) {
            params.put("uid", userId);
        }
        NetLoadUtils.getQyInstance().loadNetDataPost(QualityShopHistoryListActivity.this, url
                , params, new NetLoadUtils.NetLoadListener() {

                    @Override
                    public void onSuccess(String result) {
                        itemDescriptionList.clear();
                        Gson gson = new Gson();
                        ShopBuyDetailEntity shopDetailsEntity = gson.fromJson(result, ShopBuyDetailEntity.class);
                        if (shopDetailsEntity != null) {
                            if (shopDetailsEntity.getCode().equals(SUCCESS_CODE)) {
                                shopBuyDetailBean = shopDetailsEntity.getShopBuyDetailBean();
                                tv_header_titleAll.setText(getStrings(shopBuyDetailBean.getName()));
                                List<CommunalDetailBean> descriptionBeanList = shopBuyDetailBean.getDescriptionBeanList();
                                GlideImageLoaderUtil.loadImgDynamicDrawable(QualityShopHistoryListActivity.this, shopBuyListView.iv_communal_cover_wrap, shopBuyDetailBean.getCoverImgUrl());
                                if (descriptionBeanList != null) {
                                    itemDescriptionList.addAll(ConstantMethod.getDetailsDataList(descriptionBeanList));
                                }
                            } else if (!shopDetailsEntity.getCode().equals(EMPTY_CODE)) {
                                showToast(QualityShopHistoryListActivity.this, shopDetailsEntity.getMsg());
                            }
                            communalDetailAdapter.setNewData(itemDescriptionList);
                        }
                    }

                    @Override
                    public void netClose() {}

                    @Override
                    public void onError(Throwable throwable) {}
                });
    }

    class ShopBuyListView {
        @BindView(R.id.communal_recycler_wrap)
        RecyclerView communal_recycler_wrap;
        @BindView(R.id.iv_communal_cover_wrap)
        ImageView iv_communal_cover_wrap;

        public void initView() {
            iv_communal_cover_wrap.setVisibility(View.VISIBLE);
            communal_recycler_wrap.setNestedScrollingEnabled(false);
            communal_recycler_wrap.setLayoutManager(new LinearLayoutManager((QualityShopHistoryListActivity.this)));
            communalDetailAdapter = new CommunalDetailAdapter(QualityShopHistoryListActivity.this, itemDescriptionList);
            communal_recycler_wrap.setAdapter(communalDetailAdapter);
        }
    }

    private void getDirectCoupon(int id) {
        String url = Url.BASE_URL + Url.FIND_ARTICLE_COUPON;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("couponId", id);
        XUtil.Post(url, params, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                Gson gson = new Gson();
                RequestStatus requestStatus = gson.fromJson(result, RequestStatus.class);
                if (requestStatus != null) {
                    if (requestStatus.getCode().equals("01")) {
                        showToast(QualityShopHistoryListActivity.this, requestStatus.getResult() != null ? requestStatus.getResult().getMsg() : requestStatus.getMsg());
                    } else {
                        showToast(QualityShopHistoryListActivity.this, requestStatus.getResult() != null ? requestStatus.getResult().getMsg() : requestStatus.getMsg());
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showToast(QualityShopHistoryListActivity.this, R.string.Get_Coupon_Fail);
                if (loadHud != null) {
                    loadHud.dismiss();
                }
            }
        });
    }

    private void getDirectCouponPackage(int couponId) {
        String url = Url.BASE_URL + Url.COUPON_PACKAGE;
        Map<String, Object> params = new HashMap<>();
        params.put("uId", userId);
        params.put("cpId", couponId);
        XUtil.Post(url, params, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                Gson gson = new Gson();
                RequestStatus requestStatus = gson.fromJson(result, RequestStatus.class);
                if (requestStatus != null) {
                    if (requestStatus.getCode().equals("01")) {
                        showToast(QualityShopHistoryListActivity.this, requestStatus.getResult() != null ? requestStatus.getResult().getMsg() : requestStatus.getMsg());
                    } else {
                        showToast(QualityShopHistoryListActivity.this, requestStatus.getResult() != null ? requestStatus.getResult().getMsg() : requestStatus.getMsg());
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showToast(QualityShopHistoryListActivity.this, R.string.Get_Coupon_Fail);
                if (loadHud != null) {
                    loadHud.dismiss();
                }
            }
        });
    }

    public void skipAliBCWebView(final String url) {
        if (!TextUtils.isEmpty(url)) {
            if (userId != 0) {
                skipNewTaoBao(url);
            } else {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                getLoginStatus(QualityShopHistoryListActivity.this);
            }
        } else {
            showToast(QualityShopHistoryListActivity.this, "地址缺失");
            if (loadHud != null) {
                loadHud.dismiss();
            }
        }
    }

    private void skipNewTaoBao(final String url) {
        AlibcLogin alibcLogin = AlibcLogin.getInstance();
        alibcLogin.showLogin(new AlibcLoginCallback() {
            @Override
            public void onSuccess(int i) {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                skipNewShopDetails(url);
            }

            @Override
            public void onFailure(int code, String msg) {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                showToast(QualityShopHistoryListActivity.this, "登录失败 ");
            }
        });
    }

    private void skipNewShopDetails(String url) {
        //提供给三方传递配置参数
        Map<String, String> exParams = new HashMap<>();
        exParams.put(AlibcConstants.ISV_CODE, "appisvcode");
        //设置页面打开方式
        AlibcShowParams showParams = new AlibcShowParams(OpenType.Native, false);
        //实例化商品详情 itemID打开page
        AlibcBasePage ordersPage = new AlibcPage(url);
        AlibcTrade.show(QualityShopHistoryListActivity.this, ordersPage, showParams, null, exParams, new AlibcTradeCallback() {
            @Override
            public void onTradeSuccess(AlibcTradeResult alibcTradeResult) {
                //打开电商组件，用户操作中成功信息回调。tradeResult：成功信息（结果类型：加购，支付；支付结果）
//                showToast(context, "获取详情成功");
                Log.d("商品详情", "onTradeSuccess: ");
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("商品详情", "onFailure: " + code + msg);
                //打开电商组件，用户操作中错误信息回调。code：错误码；msg：错误信息
//                showToast(ShopTimeScrollDetailsActivity.this, msg);
            }
        });
    }

    @OnClick(R.id.tv_life_back)
    void goBack(View view) {
        finish();
    }

    @OnClick(R.id.iv_img_service)
    void skipService(View view) {
        Intent intent = new Intent(QualityShopHistoryListActivity.this, ShopCarActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.iv_img_share)
    void setShare(View view) {
        if (shopBuyDetailBean != null) {
            new UMShareAction(QualityShopHistoryListActivity.this
                    , shopBuyDetailBean.getCoverImgUrl()
                    , "必买清单"
                    , "集结各路口碑好货，为你精选出必买的家居、母婴优品，不踩雷，买得更顺心。"
                    , Url.BASE_SHARE_PAGE_TWO + "m/template/goods/must_buy.html");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (totalPersonalTrajectory != null) {
            Map<String, String> totalMap = new HashMap<>();
            totalMap.put("relate_id", listId);
            totalPersonalTrajectory.stopTotal(totalMap);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (totalPersonalTrajectory != null) {
            Map<String, String> totalMap = new HashMap<>();
            totalMap.put("relate_id", listId);
            totalPersonalTrajectory.stopTotal(totalMap);
        }
    }
}
