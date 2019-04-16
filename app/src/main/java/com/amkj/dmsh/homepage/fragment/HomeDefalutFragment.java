package com.amkj.dmsh.homepage.fragment;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amkj.dmsh.R;
import com.amkj.dmsh.base.BaseFragment;
import com.amkj.dmsh.base.EventMessage;
import com.amkj.dmsh.bean.HomeWelfareEntity;
import com.amkj.dmsh.bean.HomeWelfareEntity.HomeWelfareBean;
import com.amkj.dmsh.constant.BaseAddCarProInfoBean;
import com.amkj.dmsh.constant.CommunalAdHolderView;
import com.amkj.dmsh.constant.ConstantMethod;
import com.amkj.dmsh.constant.Url;
import com.amkj.dmsh.dominant.activity.DoMoLifeWelfareActivity;
import com.amkj.dmsh.dominant.activity.DoMoLifeWelfareDetailsActivity;
import com.amkj.dmsh.dominant.activity.QualityNewUserActivity;
import com.amkj.dmsh.dominant.adapter.QualityGoodNewProAdapter;
import com.amkj.dmsh.dominant.bean.QualityGoodProductEntity;
import com.amkj.dmsh.homepage.activity.ArticleOfficialActivity;
import com.amkj.dmsh.homepage.adapter.HomeArticleNewAdapter;
import com.amkj.dmsh.homepage.adapter.HomeDoubleAdapter;
import com.amkj.dmsh.homepage.adapter.HomeNewUserAdapter;
import com.amkj.dmsh.homepage.adapter.HomeTopAdapter;
import com.amkj.dmsh.homepage.bean.CommunalADActivityEntity;
import com.amkj.dmsh.homepage.bean.CommunalADActivityEntity.CommunalADActivityBean;
import com.amkj.dmsh.homepage.bean.CommunalArticleEntity;
import com.amkj.dmsh.homepage.bean.CommunalArticleEntity.CommunalArticleBean;
import com.amkj.dmsh.homepage.bean.HomeCommonEntity;
import com.amkj.dmsh.homepage.bean.HomeCommonEntity.HomeCommonBean;
import com.amkj.dmsh.homepage.bean.HomeCommonEntity.ProductInfoListBean;
import com.amkj.dmsh.homepage.bean.HomeNewUserEntity;
import com.amkj.dmsh.network.NetCacheLoadListenerHelper;
import com.amkj.dmsh.network.NetLoadListenerHelper;
import com.amkj.dmsh.network.NetLoadUtils;
import com.amkj.dmsh.shopdetails.activity.ShopScrollDetailsActivity;
import com.amkj.dmsh.user.bean.UserLikedProductEntity.LikedProductBean;
import com.amkj.dmsh.utils.glide.GlideImageLoaderUtil;
import com.amkj.dmsh.utils.itemdecoration.ItemDecoration;
import com.amkj.dmsh.utils.multitypejson.MultiTypeJsonParser;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.amkj.dmsh.constant.ConstantMethod.adClickTotal;
import static com.amkj.dmsh.constant.ConstantMethod.getLoginStatus;
import static com.amkj.dmsh.constant.ConstantMethod.getShowNumber;
import static com.amkj.dmsh.constant.ConstantMethod.getStrings;
import static com.amkj.dmsh.constant.ConstantMethod.setSkipPath;
import static com.amkj.dmsh.constant.ConstantMethod.userId;
import static com.amkj.dmsh.constant.ConstantVariable.TOTAL_COUNT_TEN;
import static com.amkj.dmsh.constant.Url.CATE_DOC_LIST;
import static com.amkj.dmsh.constant.Url.GTE_HOME_TOP;
import static com.amkj.dmsh.constant.Url.H_DML_THEME;
import static com.amkj.dmsh.constant.Url.QUALITY_SHOP_GOODS_PRO;
import static com.amkj.dmsh.constant.Url.Q_HOME_AD_LOOP;
import static com.amkj.dmsh.dominant.fragment.QualityFragment.updateCarNum;


/**
 * Created by xiaoxin on 2019/4/13 0013
 * Version:v4.0.0
 * ClassDescription :首页默认Frament(良品优选)
 */
public class HomeDefalutFragment extends BaseFragment {
    @BindView(R.id.cb_banner)
    ConvenientBanner mCbBanner;
    @BindView(R.id.rv_top)
    RecyclerView mRvTop;
    @BindView(R.id.smart_layout)
    SmartRefreshLayout mSmartLayout;
    @BindView(R.id.iv_newuser_cover)
    ImageView mIvCover;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    @BindView(R.id.rv_new_goods)
    RecyclerView mRvNewGoods;
    @BindView(R.id.ll_new_user)
    LinearLayout mLlNewUser;
    @BindView(R.id.tv_welfare_title)
    TextView mTvWelfareTitle;
    @BindView(R.id.tv_welfare_desc)
    TextView mTvWelfareDesc;
    @BindView(R.id.rv_felware)
    RecyclerView mRvFelware;
    @BindView(R.id.tv_double_left)
    TextView mTvDoubleLeft;
    @BindView(R.id.rv_double_left)
    RecyclerView mRvDoubleLeft;
    @BindView(R.id.tv_double_right)
    TextView mTvDoubleRight;
    @BindView(R.id.rv_double_right)
    RecyclerView mRvDoubleRight;
    @BindView(R.id.ll_double)
    LinearLayout mLlDouble;
    @BindView(R.id.ll_double_left)
    LinearLayout mLlDoubleLeft;
    @BindView(R.id.ll_double_right)
    LinearLayout mLlDoubleRight;
    @BindView(R.id.rv_nice)
    RecyclerView mRvNice;
    @BindView(R.id.ll_felware)
    LinearLayout mLlFelware;
    @BindView(R.id.ll_nice)
    LinearLayout mLlNice;
    @BindView(R.id.rl_more_welfare_topic)
    RelativeLayout mRlMoreWelfareTopic;
    @BindView(R.id.tv_nice_title)
    TextView mTvNiceTitle;
    @BindView(R.id.tv_nice_desc)
    TextView mTvNiceDesc;
    @BindView(R.id.rl_more_nice_topic)
    RelativeLayout mRlMoreNiceTopic;
    @BindView(R.id.rl_more_artical)
    RelativeLayout mRlMoreArtical;
    @BindView(R.id.rv_artical)
    RecyclerView mRvArtical;
    @BindView(R.id.ll_artical)
    LinearLayout mLlArtical;
    private boolean isUpdateCache;
    private CBViewHolderCreator cbViewHolderCreator;
    private List<CommunalADActivityBean> adBeanList = new ArrayList<>();
    private List<HomeCommonBean> mTopList = new ArrayList<>();
    private List<HomeWelfareBean> mThemeList = new ArrayList<>();
    private List<ProductInfoListBean> mDoubleLeftList = new ArrayList<>();
    private List<ProductInfoListBean> mDoubleRightList = new ArrayList<>();
    private List<HomeNewUserEntity.HomeNewUserBean> mNewUserGoodsList = new ArrayList<>();
    private List<QualityGoodProductEntity.Attribute> goodsProList = new ArrayList<>();
    private List<CommunalArticleBean> articleTypeList = new ArrayList<>();
    private List<CommunalArticleBean> articleTypeAllList = new ArrayList<>();
    private HomeTopAdapter mHomeTopAdapter;
    private HomeNewUserAdapter mHomeNewUserAdapter;
    private HomeWelfareAdapter mHomeWelfareAdapter;
    private HomeDoubleAdapter mDoubleLeftAdapter;
    private HomeDoubleAdapter mDoubleRightAdapter;
    QualityGoodNewProAdapter qualityGoodNewProAdapter;
    private HomeCommonEntity mHomeCommonEntity;
    private String mDoubleLeftLink;
    private String mDoubleRightLink;
    private HomeWelfareEntity mHomeWelfareEntity;
    private HomeArticleNewAdapter homeArticleAdapter;
    private CommunalArticleEntity mCommunalArticleEntity;
    private int page = 1;

    @Override
    protected int getContentView() {
        return R.layout.fragment_home_default;
    }

    @Override
    protected void initViews() {
        mSmartLayout.setOnRefreshListener(refreshLayout -> {
            isUpdateCache = true;
            page = 1;
            loadData();
        });
        //初始化Top适配器
        LinearLayoutManager topManager = new LinearLayoutManager(getActivity()
                , LinearLayoutManager.HORIZONTAL, false);
        mRvTop.setLayoutManager(topManager);
        mHomeTopAdapter = new HomeTopAdapter(getActivity(), mTopList);
        mRvTop.setAdapter(mHomeTopAdapter);
        mHomeTopAdapter.setOnItemClickListener((adapter, view, position) -> {
            HomeCommonBean homeCommonBean = (HomeCommonBean) view.getTag();
            if (homeCommonBean != null) {
                setSkipPath(getActivity(), homeCommonBean.getLink(), false);
            }
        });

        //初始化新人专享适配器
        GridLayoutManager newUserManager = new GridLayoutManager(getActivity()
                , 2);
        mRvNewGoods.setLayoutManager(newUserManager);
        ItemDecoration itemDecoration = new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_fifteen_white)
                .create();
        mRvNewGoods.addItemDecoration(itemDecoration);
        mHomeNewUserAdapter = new HomeNewUserAdapter(getActivity(), mNewUserGoodsList);
        mHomeNewUserAdapter.setOnItemClickListener((adapter, view, position) -> {
            //跳转商品详情
        });
        mIvCover.setOnClickListener(view -> {
            //跳转新人专区
            Intent intent = new Intent(getActivity(), QualityNewUserActivity.class);
            if (getActivity() != null) {
                getActivity().startActivity(intent);
            }
        });
        mRvNewGoods.setAdapter(mHomeNewUserAdapter);

        //初始化Double专区左边适配器
        GridLayoutManager gridLeftManager = new GridLayoutManager(getActivity()
                , 2);
        mRvDoubleLeft.setLayoutManager(gridLeftManager);
        mDoubleLeftAdapter = new HomeDoubleAdapter(getActivity(), mDoubleLeftList);
        mDoubleLeftAdapter.setOnItemClickListener((adapter, view, position) -> {
            //跳转对应专区
            if (!TextUtils.isEmpty(mDoubleLeftLink)) {
                setSkipPath(getActivity(), mDoubleLeftLink, false);
            }
        });
        mRvDoubleLeft.setAdapter(mDoubleLeftAdapter);
        mRvDoubleLeft.addItemDecoration(new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_five_white)
                .create());

        //初始化Double专区右边适配器
        GridLayoutManager gridRightManager = new GridLayoutManager(getActivity()
                , 2);
        mRvDoubleRight.setLayoutManager(gridRightManager);
        mDoubleRightAdapter = new HomeDoubleAdapter(getActivity(), mDoubleRightList);
        mDoubleRightAdapter.setOnItemClickListener((adapter, view, position) -> {
            //跳转对应专区
            if (!TextUtils.isEmpty(mDoubleRightLink)) {
                setSkipPath(getActivity(), mDoubleRightLink, false);
            }
        });
        mRvDoubleRight.setAdapter(mDoubleRightAdapter);
        mRvDoubleRight.addItemDecoration(new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_five_white)
                .create());

        //初始化福利精选适配器
        LinearLayoutManager felwareManager = new LinearLayoutManager(getActivity()
                , LinearLayoutManager.HORIZONTAL, false);
        mRvFelware.setLayoutManager(felwareManager);
        mHomeWelfareAdapter = new HomeWelfareAdapter(getActivity(), mThemeList);
        mHomeWelfareAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_welfare_cover && getActivity() != null) {
                HomeWelfareBean homeWelfareBean = (HomeWelfareBean) view.getTag(R.id.iv_tag);
                Intent intent = new Intent(getActivity(), DoMoLifeWelfareDetailsActivity.class);
                intent.putExtra("welfareId", String.valueOf(homeWelfareBean.getId()));
                getActivity().startActivity(intent);
            }
        });
        mRvFelware.setAdapter(mHomeWelfareAdapter);

        //初始化好物适配器
        GridLayoutManager niceManager = new GridLayoutManager(getActivity()
                , 2);
        mRvNice.setLayoutManager(niceManager);
        mRvNice.addItemDecoration(new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_five_dp)
                .create());
        qualityGoodNewProAdapter = new QualityGoodNewProAdapter(getActivity(), goodsProList);
        qualityGoodNewProAdapter.setOnItemClickListener((adapter, view, position) -> {
            QualityGoodProductEntity.Attribute attribute = (QualityGoodProductEntity.Attribute) view.getTag();
            if (attribute != null) {
                switch (attribute.getObjectType()) {
                    case "product":
                        LikedProductBean likedProductBean = (LikedProductBean) attribute;
                        Intent intent = new Intent(getActivity(), ShopScrollDetailsActivity.class);
                        intent.putExtra("productId", String.valueOf(likedProductBean.getId()));
                        startActivity(intent);
                        break;
                    case "ad":
                        CommunalADActivityBean communalADActivityBean = (CommunalADActivityBean) attribute;
                        /**
                         * 3.1.9 加入好物广告统计
                         */
                        adClickTotal(getActivity(), communalADActivityBean.getId());
                        setSkipPath(getActivity(), getStrings(communalADActivityBean.getAndroidLink()), false);
                        break;
                }

            }
        });
        qualityGoodNewProAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            loadHud.show();
            QualityGoodProductEntity.Attribute attribute = (QualityGoodProductEntity.Attribute) view.getTag();
            if (attribute != null) {
                if (userId > 0) {
                    switch (view.getId()) {
                        case R.id.iv_pro_add_car:
                            LikedProductBean likedProductBean = (LikedProductBean) attribute;
                            BaseAddCarProInfoBean baseAddCarProInfoBean = new BaseAddCarProInfoBean();
                            baseAddCarProInfoBean.setProductId(likedProductBean.getId());
                            baseAddCarProInfoBean.setActivityCode(getStrings(likedProductBean.getActivityCode()));
                            baseAddCarProInfoBean.setProName(getStrings(likedProductBean.getName()));
                            baseAddCarProInfoBean.setProPic(getStrings(likedProductBean.getPicUrl()));
                            ConstantMethod constantMethod = new ConstantMethod();
                            constantMethod.addShopCarGetSku(getActivity(), baseAddCarProInfoBean, loadHud);
                            constantMethod.setAddOnCarListener(() -> EventBus.getDefault().post(new EventMessage(updateCarNum, updateCarNum)));
                            break;
                    }
                } else {
                    loadHud.dismiss();
                    getLoginStatus(HomeDefalutFragment.this);
                }
            }
        });
        mRvNice.setAdapter(qualityGoodNewProAdapter);

        //初始化文章适配器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRvArtical.setLayoutManager(linearLayoutManager);
        homeArticleAdapter = new HomeArticleNewAdapter(getActivity(), articleTypeList);
        homeArticleAdapter.setOnItemClickListener((adapter, view, position) -> {
            CommunalArticleBean communalArticleBean = (CommunalArticleBean) view.getTag();
            if (communalArticleBean != null) {
                Intent intent = new Intent(getActivity(), ArticleOfficialActivity.class);
                intent.putExtra("ArtId", String.valueOf(communalArticleBean.getId()));
                startActivity(intent);
            }
        });
        mRvArtical.setAdapter(homeArticleAdapter);
    }

    @Override
    protected void loadData() {
        getAdLoop();
        getHomeIndexType();
        getNewUserGoods();
        getWelfare();
        getHomeDouble();
        getGoodsPro();
        getArticleTypeList(false);
    }

    //获取Banner
    private void getAdLoop() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("vidoShow", "1");
        NetLoadUtils.getNetInstance().loadNetDataGetCache(getActivity(), Q_HOME_AD_LOOP, params, isUpdateCache, new NetCacheLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                getADJsonData(result);
            }
        });
    }

    private void getADJsonData(String result) {
        Gson gson = new Gson();
        CommunalADActivityEntity qualityAdLoop = gson.fromJson(result, CommunalADActivityEntity.class);
        if (qualityAdLoop != null) {
            List<CommunalADActivityBean> communalAdList = qualityAdLoop.getCommunalADActivityBeanList();
            if (communalAdList != null && communalAdList.size() > 0) {
                adBeanList.clear();
                adBeanList.addAll(qualityAdLoop.getCommunalADActivityBeanList());
                mCbBanner.setVisibility(View.VISIBLE);
                if (cbViewHolderCreator == null) {
                    cbViewHolderCreator = new CBViewHolderCreator() {
                        @Override
                        public Holder createHolder(View itemView) {
                            return new CommunalAdHolderView(itemView, getActivity(), true);
                        }

                        @Override
                        public int getLayoutId() {
                            return R.layout.layout_ad_image_video;
                        }
                    };
                }
                mCbBanner.setPages(getActivity(), cbViewHolderCreator, adBeanList).setCanLoop(true)
                        .setPointViewVisible(true).setCanScroll(true)
                        .setPageIndicator(new int[]{R.drawable.unselected_radius, R.drawable.selected_radius})
                        .startTurning(getShowNumber(adBeanList.get(0).getShowTime()) * 1000);
            } else {
                mCbBanner.setVisibility(View.GONE);
            }
        }
    }

    //Top活动位
    private void getHomeIndexType() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("source", "1");
        NetLoadUtils.getNetInstance().loadNetDataGetCache(getActivity(), GTE_HOME_TOP, map, isUpdateCache, new NetCacheLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                HomeCommonEntity homeNavbarEntity = gson.fromJson(result, HomeCommonEntity.class);
                if (homeNavbarEntity != null) {
                    List<HomeCommonBean> topList = homeNavbarEntity.getResult();
                    if (topList != null && topList.size() > 0) {
                        mTopList.clear();
                        for (int i = 0; i < (topList.size() > 5 ? 5 : topList.size()); i++) {
                            mTopList.add(topList.get(i));
                        }
                        mHomeTopAdapter.notifyDataSetChanged();
                    }
                }

                mRvTop.setVisibility(mTopList.size() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNotNetOrException() {
                mRvTop.setVisibility(mTopList.size() > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    //新人专享专区
    private void getNewUserGoods() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("source", 1);
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), Url.GTE_NEW_USER_GOODS, map, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                HomeNewUserEntity homeTypeEntity = gson.fromJson(result, HomeNewUserEntity.class);
                if (homeTypeEntity != null) {
                    GlideImageLoaderUtil.loadImage(getActivity(), mIvCover, homeTypeEntity.getCover());
                    mTvTitle.setText(getStrings(homeTypeEntity.getTitle()));
                    mTvDesc.setText(getStrings(homeTypeEntity.getDesc()));
                    List<HomeNewUserEntity.HomeNewUserBean> homeNewUserGoods = homeTypeEntity.getHomeNewUserGoods();
                    if (homeNewUserGoods != null && homeNewUserGoods.size() > 0) {
                        mLlNewUser.setVisibility(View.VISIBLE);
                        mNewUserGoodsList.clear();
                        for (int i = 0; i < (homeNewUserGoods.size() > 2 ? 2 : 1); i++) {
                            mNewUserGoodsList.add(homeNewUserGoods.get(i));
                        }
                        mHomeNewUserAdapter.notifyDataSetChanged();
                    }
                    mLlNewUser.setVisibility(mNewUserGoodsList.size() > 0 ? View.VISIBLE : View.GONE);

                }
            }

            @Override
            public void onNotNetOrException() {
                mLlNewUser.setVisibility(mNewUserGoodsList.size() > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    //获取并排专区
    private void getHomeDouble() {
        Map<String, Object> map = new HashMap<>();
        map.put("source", 1);
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), Url.GTE_HOME_DOUBLE, map, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                mHomeCommonEntity = gson.fromJson(result, HomeCommonEntity.class);
                List<HomeCommonBean> doubleList = mHomeCommonEntity.getResult();
                if (mHomeCommonEntity != null) {
                    if (doubleList != null && doubleList.size() > 0) {
                        //解析左边专区
                        HomeCommonBean homeCommonBeanLeft = doubleList.get(0);
                        if (homeCommonBeanLeft != null) {
                            mTvDoubleLeft.setText(getStrings(homeCommonBeanLeft.getName()));
                            mDoubleLeftLink = homeCommonBeanLeft.getLink();
                            mLlDoubleLeft.setOnClickListener(view -> {
                                setSkipPath(getActivity(), homeCommonBeanLeft.getLink(), false);
                            });
                            mDoubleLeftList.clear();
                            int size = homeCommonBeanLeft.getProductInfoList().size();
                            for (int i = 0; i < (size > 4 ? 4 : size); i++) {
                                mDoubleLeftList.add(homeCommonBeanLeft.getProductInfoList().get(i));
                            }
                        }
                        mDoubleLeftAdapter.notifyDataSetChanged();

                        //解析右边专区
                        if (doubleList.size() > 1) {
                            HomeCommonBean homeCommonBeanRight = doubleList.get(1);
                            if (homeCommonBeanRight != null) {
                                mTvDoubleRight.setText(getStrings(homeCommonBeanRight.getName()));
                                mDoubleRightLink = homeCommonBeanRight.getLink();
                                mDoubleRightList.clear();
                                int size = homeCommonBeanRight.getProductInfoList().size();
                                for (int i = 0; i < (size > 4 ? 4 : size); i++) {
                                    mDoubleRightList.add(homeCommonBeanRight.getProductInfoList().get(i));
                                }
                            }
                        }
                        mDoubleRightAdapter.notifyDataSetChanged();
                    }
                }

                mLlDoubleLeft.setVisibility(mDoubleLeftList.size() > 0 ? View.VISIBLE : View.GONE);
                mLlDoubleRight.setVisibility(mDoubleRightList.size() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNotNetOrException() {
                mLlDoubleLeft.setVisibility(mDoubleLeftList.size() > 0 ? View.VISIBLE : View.GONE);
                mLlDoubleRight.setVisibility(mDoubleRightList.size() > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    //获取福利社商品
    private void getWelfare() {
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", 1);
        params.put("showCount", TOTAL_COUNT_TEN);
        params.put("goodsCurrentPage", 1);
        params.put("goodsShowCount", 8);
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), H_DML_THEME
                , params, new NetLoadListenerHelper() {
                    @Override
                    public void onSuccess(String result) {
                        Gson gson = new Gson();
                        mHomeWelfareEntity = gson.fromJson(result, HomeWelfareEntity.class);
                        List<HomeWelfareBean> themeList = mHomeWelfareEntity.getResult();
                        if (mHomeWelfareEntity != null) {
                            if (themeList != null && themeList.size() > 0) {
                                mThemeList.clear();
                                mThemeList.addAll(mHomeWelfareEntity.getResult());
                                mHomeWelfareAdapter.notifyDataSetChanged();
                            }
                        }

                        mLlFelware.setVisibility(mThemeList.size() > 0 ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onNotNetOrException() {
                        mLlFelware.setVisibility(mThemeList.size() > 0 ? View.VISIBLE : View.GONE);
                    }
                });
    }

    //获取好物商品
    private void getGoodsPro() {
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", page);
        /**
         * version 1 区分是否带入广告页
         */
        params.put("version", 1);
        if (userId > 0) {
            params.put("uid", userId);
        }
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), QUALITY_SHOP_GOODS_PRO
                , params, new NetLoadListenerHelper() {
                    @Override
                    public void onSuccess(String result) {
                        mSmartLayout.finishRefresh();
                        MultiTypeJsonParser<QualityGoodProductEntity.Attribute> multiTypeJsonParser = new MultiTypeJsonParser.Builder<QualityGoodProductEntity.Attribute>()
                                .registerTypeElementName("objectType")
                                .registerTargetClass(QualityGoodProductEntity.Attribute.class)
                                .registerTypeElementValueWithClassType("product", LikedProductBean.class)
                                .registerTypeElementValueWithClassType("ad", CommunalADActivityBean.class)
                                .build();
                        QualityGoodProductEntity qualityGoodProductEntity = multiTypeJsonParser.fromJson(result, QualityGoodProductEntity.class);
                        if (qualityGoodProductEntity != null) {
                            List<QualityGoodProductEntity.Attribute> goodProductList = qualityGoodProductEntity.getGoodProductList();
                            if (goodProductList != null && goodProductList.size() > 0) {
                                goodsProList.clear();
                                for (int i = 0; i < (goodProductList.size() > 6 ? 6 : goodProductList.size()); i++) {
                                    goodsProList.add(goodProductList.get(i));
                                }
                            }
                        }
                        qualityGoodNewProAdapter.notifyDataSetChanged();
                        mLlNice.setVisibility(goodsProList.size() > 0 ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onNotNetOrException() {
                        mSmartLayout.finishRefresh();
                        mLlNice.setVisibility(goodsProList.size() > 0 ? View.VISIBLE : View.GONE);
                    }
                });
    }

    //获取全部类型文章
    private void getArticleTypeList(boolean isClick) {
        if (isClick && loadHud != null) {
            loadHud.show();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", page);
        params.put("shouCount", 10);
        if (userId > 0) {
            params.put("uid", userId);
        }
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), CATE_DOC_LIST, params, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                mCommunalArticleEntity = gson.fromJson(result, CommunalArticleEntity.class);
                if (mCommunalArticleEntity != null) {
                    List<CommunalArticleBean> communalArticleList = mCommunalArticleEntity.getCommunalArticleList();
                    if (communalArticleList != null && communalArticleList.size() >= 2) {
                        articleTypeAllList.clear();
                        articleTypeAllList.addAll(communalArticleList);
                        setArticalData();
                    }
                }
                loadHud.dismiss();
                mLlArtical.setVisibility(articleTypeList.size() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNotNetOrException() {
                loadHud.dismiss();
//                        NetLoadUtils.getNetInstance().showLoadSir(loadService, articleTypeList, mCommunalArticleEntity);
                mLlArtical.setVisibility(articleTypeList.size() > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    //设置文章列表数据并刷新
    private void setArticalData() {
        articleTypeList.clear();
        articleTypeList.add(articleTypeAllList.get(0));
        articleTypeList.add(articleTypeAllList.get(1));
        articleTypeAllList.removeAll(articleTypeList);
        homeArticleAdapter.notifyDataSetChanged();

    }

    @OnClick({R.id.rl_more_welfare_topic, R.id.rl_more_nice_topic, R.id.tv_more_nice_topic, R.id.rl_more_artical, R.id.tv_refresh_artical})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //跳转福利社列表
            case R.id.rl_more_welfare_topic:
                Intent intent = new Intent(getActivity(), DoMoLifeWelfareActivity.class);
                if (getActivity() != null) getActivity().startActivity(intent);
                break;
            //跳转好物列表
            case R.id.rl_more_nice_topic:
            case R.id.tv_more_nice_topic:

                break;
            //跳转文章列表
            case R.id.rl_more_artical:

                break;
            //换一批文章
            case R.id.tv_refresh_artical:
                if (articleTypeAllList.size() >= 2) {
                    setArticalData();
                } else {
                    //加载下一页数据
                    page++;
                    if (loadHud != null) {
                        loadHud.show();
                    }
                    getArticleTypeList(true);
                }

                break;
        }
    }
}