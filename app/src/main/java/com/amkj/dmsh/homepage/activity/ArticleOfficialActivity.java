package com.amkj.dmsh.homepage.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amkj.dmsh.R;
import com.amkj.dmsh.base.BaseActivity;
import com.amkj.dmsh.base.EventMessage;
import com.amkj.dmsh.base.TinkerBaseApplicationLike;
import com.amkj.dmsh.bean.RequestStatus;
import com.amkj.dmsh.constant.CommunalComment;
import com.amkj.dmsh.constant.CommunalDetailBean;
import com.amkj.dmsh.constant.ConstantMethod;
import com.amkj.dmsh.constant.ConstantVariable;
import com.amkj.dmsh.constant.Url;
import com.amkj.dmsh.dominant.adapter.ArticleCommentAdapter;
import com.amkj.dmsh.dominant.adapter.WelfareSlideProAdapter;
import com.amkj.dmsh.dominant.bean.DmlSearchCommentEntity;
import com.amkj.dmsh.dominant.bean.DmlSearchCommentEntity.DmlSearchCommentBean;
import com.amkj.dmsh.dominant.bean.DmlSearchCommentEntity.DmlSearchCommentBean.ReplyCommListBean;
import com.amkj.dmsh.dominant.bean.DmlSearchDetailEntity;
import com.amkj.dmsh.dominant.bean.DmlSearchDetailEntity.DmlSearchDetailBean;
import com.amkj.dmsh.dominant.bean.DmlSearchDetailEntity.DmlSearchDetailBean.ProductListBean;
import com.amkj.dmsh.homepage.adapter.CommunalDetailAdapter;
import com.amkj.dmsh.homepage.bean.CommunalOnlyDescription;
import com.amkj.dmsh.homepage.bean.CommunalOnlyDescription.ComOnlyDesBean;
import com.amkj.dmsh.network.NetLoadListenerHelper;
import com.amkj.dmsh.network.NetLoadUtils;
import com.amkj.dmsh.shopdetails.bean.CommunalDetailObjectBean;
import com.amkj.dmsh.user.activity.UserPagerActivity;
import com.amkj.dmsh.utils.CommonUtils;
import com.amkj.dmsh.utils.OffsetLinearLayoutManager;
import com.amkj.dmsh.utils.glide.GlideImageLoaderUtil;
import com.amkj.dmsh.utils.itemdecoration.ItemDecoration;
import com.amkj.dmsh.utils.webformatdata.CommunalWebDetailUtils;
import com.amkj.dmsh.utils.webformatdata.ShareDataBean;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.melnykov.fab.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tencent.bugly.beta.tinker.TinkerManager;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import emojicon.EmojiconEditText;
import me.jessyan.autosize.utils.AutoSizeUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.amkj.dmsh.base.TinkerBaseApplicationLike.mAppContext;
import static com.amkj.dmsh.constant.ConstantMethod.getLoginStatus;
import static com.amkj.dmsh.constant.ConstantMethod.getStringChangeBoolean;
import static com.amkj.dmsh.constant.ConstantMethod.getStrings;
import static com.amkj.dmsh.constant.ConstantMethod.insertNewTotalData;
import static com.amkj.dmsh.constant.ConstantMethod.showToast;
import static com.amkj.dmsh.constant.ConstantMethod.skipProductUrl;
import static com.amkj.dmsh.constant.ConstantMethod.totalProNum;
import static com.amkj.dmsh.constant.ConstantMethod.userId;
import static com.amkj.dmsh.constant.ConstantVariable.COMMENT_TYPE;
import static com.amkj.dmsh.constant.ConstantVariable.DEFAULT_COMMENT_TOTAL_COUNT;
import static com.amkj.dmsh.constant.ConstantVariable.EMPTY_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.IS_LOGIN_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.SUCCESS_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.TOTAL_COUNT_TEN;
import static com.amkj.dmsh.constant.Url.FIND_AND_COMMENT_FAV;
import static com.amkj.dmsh.constant.Url.F_ARTICLE_COLLECT;
import static com.amkj.dmsh.constant.Url.F_ARTICLE_DETAILS_FAVOR;
import static com.amkj.dmsh.constant.Url.F_INVITATION_DETAIL;
import static com.amkj.dmsh.constant.Url.Q_DML_SEARCH_COMMENT;
import static com.amkj.dmsh.constant.Url.SHARE_COMMUNAL_ARTICLE;
import static com.amkj.dmsh.shopdetails.bean.CommunalDetailObjectBean.TYPE_PRODUCT_RECOMMEND;
import static com.amkj.dmsh.shopdetails.bean.CommunalDetailObjectBean.TYPE_PRODUCT_TAG;
import static com.amkj.dmsh.utils.CommunalCopyTextUtils.showPopWindow;

;

/**
 * Created by atd48 on 2016/6/30.
 */
public class ArticleOfficialActivity extends BaseActivity {
    @BindView(R.id.smart_communal_refresh)
    SmartRefreshLayout smart_communal_refresh;
    @BindView(R.id.communal_recycler)
    RecyclerView communal_recycler;
    @BindView(R.id.tl_normal_bar)
    Toolbar tl_normal_bar;
    @BindView(R.id.tv_header_title)
    TextView tv_header_titleAll;
    @BindView(R.id.tv_header_shared)
    TextView tv_header_shared;
    //  滑动界面
    @BindView(R.id.dl_art_detail_pro)
    DrawerLayout dl_art_detail_pro;
    @BindView(R.id.ll_communal_pro_list)
    LinearLayout ll_communal_pro_list;
    @BindView(R.id.tv_communal_pro_title)
    TextView tv_communal_pro_title;
    @BindView(R.id.rv_communal_pro)
    RecyclerView rv_communal_pro;

    @BindView(R.id.tv_communal_pro_tag)
    TextView tv_communal_pro_tag;
    //    底栏
    @BindView(R.id.rel_article_bottom)
    RelativeLayout rel_article_bottom;
    //    评论输入
    @BindView(R.id.ll_input_comment)
    LinearLayout ll_input_comment;
    @BindView(R.id.emoji_edit_comment)
    EmojiconEditText emoji_edit_comment;
    @BindView(R.id.tv_publish_comment)
    TextView tv_publish_comment;
    @BindView(R.id.tv_send_comment)
    TextView tv_send_comment;
    //    点赞底栏
    @BindView(R.id.ll_article_comment)
    LinearLayout ll_article_comment;
    @BindView(R.id.tv_article_bottom_like)
    TextView tv_article_bottom_like;
    @BindView(R.id.tv_article_bottom_collect)
    TextView tv_article_bottom_collect;
    //    滚动至顶部
    @BindView(R.id.download_btn_communal)
    public FloatingActionButton download_btn_communal;

    //    评论列表
    private List<DmlSearchCommentBean> articleCommentList = new ArrayList<>();
    //    详细描述
    private List<CommunalDetailObjectBean> descripDetailList = new ArrayList<>();
    //侧滑商品列表
    private List<ProductListBean> searchProductList = new ArrayList();

    private ArticleCommentAdapter adapterArticleComment;
    private String artId;
    private CoverTitleView coverTitleView;
    private CommunalDetailAdapter communalDescripAdapter;
    private WelfareSlideProAdapter searchSlideProAdapter;
    private int page = 1;
    private DmlSearchDetailBean dmlSearchDetailBean;
    //    按下点击位置
    private float locationY;
    private CommentCountView commentCountView;
    private View commentHeaderView;
    private DmlSearchDetailEntity dmlSearchDetailEntity;
    private boolean isScrollToComment;
    private float screenHeight;

    @Override
    protected int getContentView() {
        return R.layout.activity_article_details;
    }

    @Override
    protected void initViews() {
        tl_normal_bar.setSelected(true);
        tv_header_titleAll.setText("");
        Intent intent = getIntent();
        artId = intent.getStringExtra("ArtId");
        isScrollToComment = getStringChangeBoolean(intent.getStringExtra("scrollToComment"));
        TinkerBaseApplicationLike app = (TinkerBaseApplicationLike) TinkerManager.getTinkerApplicationLike();
        screenHeight = app.getScreenHeight();
        rel_article_bottom.setVisibility(GONE);
        communal_recycler.setLayoutManager(new OffsetLinearLayoutManager(ArticleOfficialActivity.this));
        adapterArticleComment = new ArticleCommentAdapter(ArticleOfficialActivity.this, articleCommentList, COMMENT_TYPE);
        adapterArticleComment.setHeaderAndEmpty(true);
        View coverView = LayoutInflater.from(ArticleOfficialActivity.this)
                .inflate(R.layout.layout_article_detailes_header, (ViewGroup) communal_recycler.getParent(), false);
        commentHeaderView = LayoutInflater.from(ArticleOfficialActivity.this)
                .inflate(R.layout.layout_comm_comment_header, (ViewGroup) communal_recycler.getParent(), false);
        coverTitleView = new CoverTitleView();
        ButterKnife.bind(coverTitleView, coverView);
        commentCountView = new CommentCountView();
        ButterKnife.bind(commentCountView, commentHeaderView);
        coverTitleView.initView();
        adapterArticleComment.addHeaderView(coverView);
        communal_recycler.setAdapter(adapterArticleComment);
        communal_recycler.addItemDecoration(new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_gray_f_two_px).create());
        adapterArticleComment.setOnItemChildClickListener((adapter, view, position) -> {
            DmlSearchCommentBean dmlSearchCommentBean = (DmlSearchCommentBean) view.getTag(R.id.iv_tag);
            if (dmlSearchCommentBean == null) {
                dmlSearchCommentBean = (DmlSearchCommentBean) view.getTag();
            }
            if (dmlSearchCommentBean != null) {
                switch (view.getId()) {
                    case R.id.tv_comm_comment_like:
                        if (userId > 0) {
                            dmlSearchCommentBean.setFavor(!dmlSearchCommentBean.isFavor());
                            int likeNum = dmlSearchCommentBean.getLike_num();
                            dmlSearchCommentBean.setLike_num(dmlSearchCommentBean.isFavor()
                                    ? likeNum + 1 : likeNum - 1 > 0 ? likeNum - 1 : 0);
                            articleCommentList.set(position, dmlSearchCommentBean);
                            adapterArticleComment.notifyItemChanged(position + adapterArticleComment.getHeaderLayoutCount());
                            setCommentLike(dmlSearchCommentBean);
                        } else {
                            getLoginStatus(ArticleOfficialActivity.this);
                        }
                        break;
                    case R.id.tv_comm_comment_receive:
//                            打开评论
                        if (userId > 0) {
                            if (VISIBLE == ll_input_comment.getVisibility()) {
                                commentViewVisible(GONE, dmlSearchCommentBean);
                            } else {
                                commentViewVisible(VISIBLE, dmlSearchCommentBean);
                            }
                        } else {
                            getLoginStatus(ArticleOfficialActivity.this);
                        }
                        break;
                    case R.id.civ_comm_comment_avatar:
                        Intent intent1 = new Intent();
                        intent1.setClass(ArticleOfficialActivity.this, UserPagerActivity.class);
                        intent1.putExtra("userId", String.valueOf(dmlSearchCommentBean.getUid()));
                        startActivity(intent1);
                        break;
                    default:
                        break;
                }
            }
        });
        adapterArticleComment.setOnItemChildLongClickListener((adapter, view, position) -> {
            DmlSearchCommentBean dmlSearchCommentBean = (DmlSearchCommentBean) view.getTag();
            if (dmlSearchCommentBean != null && !TextUtils.isEmpty(dmlSearchCommentBean.getContent())) {
                showPopWindow(ArticleOfficialActivity.this, (TextView) view, dmlSearchCommentBean.getContent());
            }
            return false;
        });
        adapterArticleComment.setOnLoadMoreListener(() -> {
            page++;
            getArticleComment();
        }, communal_recycler);
//侧滑布局
        rv_communal_pro.setLayoutManager(new LinearLayoutManager(ArticleOfficialActivity.this));
        rv_communal_pro.addItemDecoration(new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_gray_f_two_px).create());
        searchSlideProAdapter = new WelfareSlideProAdapter(ArticleOfficialActivity.this, searchProductList);
        searchSlideProAdapter.setEnableLoadMore(false);
        rv_communal_pro.setAdapter(searchSlideProAdapter);
        searchSlideProAdapter.setOnItemClickListener((adapter, view, position) -> {
            ProductListBean productListBean = (ProductListBean) view.getTag();
            if (productListBean != null) {
                dl_art_detail_pro.closeDrawers();
                skipProductUrl(ArticleOfficialActivity.this, productListBean.getItemTypeId(), productListBean.getId());
//                    统计商品点击
                totalProNum(productListBean.getId(), dmlSearchDetailBean.getId());
            }
        });

        smart_communal_refresh.setOnRefreshListener(refreshLayout -> loadData());
        communal_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                OffsetLinearLayoutManager layoutManager = (OffsetLinearLayoutManager) recyclerView.getLayoutManager();
                int scrollY = layoutManager.computeVerticalScrollOffset();
                if (scrollY > screenHeight * 1.5) {
                    if (download_btn_communal.getVisibility() == GONE) {
                        download_btn_communal.setVisibility(VISIBLE);
                        download_btn_communal.hide(false);
                    }
                    if (!download_btn_communal.isVisible()) {
                        download_btn_communal.show(true);
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
                download_btn_communal.hide();
                linearLayoutManager.scrollToPositionWithOffset(0, 0);
            }
        });
        //          关闭手势滑动
        dl_art_detail_pro.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
        totalPersonalTrajectory =

                insertNewTotalData(ArticleOfficialActivity.this, artId);
        tv_publish_comment.setText(R.string.comment_article_hint);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IS_LOGIN_CODE) {
            loadData();
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected View getLoadView() {
        return smart_communal_refresh;
    }

    @Override
    protected boolean isAddLoad() {
        return true;
    }

    @Override
    protected void loadData() {
        page = 1;
        getArticleData();
        getArticleComment();
    }

    private void getArticleComment() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", artId);
        params.put("currentPage", page);
        if (userId > 0) {
            params.put("uid", userId);
        }
        params.put("showCount", TOTAL_COUNT_TEN);
        params.put("replyCurrentPage", 1);
        params.put("replyShowCount", DEFAULT_COMMENT_TOTAL_COUNT);
        params.put("comtype", "doc");
        NetLoadUtils.getNetInstance().loadNetDataPost(this, Q_DML_SEARCH_COMMENT, params, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                adapterArticleComment.loadMoreComplete();
                if (page == 1) {
                    articleCommentList.clear();
                }
                Gson gson = new Gson();
                DmlSearchCommentEntity dmlSearchCommentEntity = gson.fromJson(result, DmlSearchCommentEntity.class);
                if (dmlSearchCommentEntity != null) {
                    if (dmlSearchCommentEntity.getCode().equals(SUCCESS_CODE)) {
                        articleCommentList.addAll(dmlSearchCommentEntity.getDmlSearchCommentList());
                    } else if (!dmlSearchCommentEntity.getCode().equals(EMPTY_CODE)) {
                        showToast(ArticleOfficialActivity.this, dmlSearchCommentEntity.getMsg());
                    } else {
                        adapterArticleComment.loadMoreEnd();
                    }
                    adapterArticleComment.removeHeaderView(commentHeaderView);
                    if (articleCommentList.size() > 0) {
                        adapterArticleComment.addHeaderView(commentHeaderView);
                        commentCountView.tv_comm_comment_count.setText(String.format(getString(R.string.comment_handpick_count), dmlSearchCommentEntity.getCommentSize()));
                    }
                    adapterArticleComment.notifyDataSetChanged();
                }
                if (isScrollToComment) {
                    scrollToComment();
                    isScrollToComment = false;
                }
            }

            @Override
            public void onNotNetOrException() {
                adapterArticleComment.loadMoreEnd(true);
            }

            @Override
            public void onError(Throwable throwable) {
                showToast(ArticleOfficialActivity.this, R.string.invalidData);
            }

            @Override
            public void netClose() {
                showToast(ArticleOfficialActivity.this, R.string.unConnectedNetwork);
            }
        });
    }

    private void getArticleData() {
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(artId));
        /**
         * 3.1.8 加入并列商品 两排 三排
         */
        params.put("version", "1");
        if (userId > 0) {
            params.put("fuid", String.valueOf(userId));
        }
        NetLoadUtils.getNetInstance().loadNetDataPost(this, F_INVITATION_DETAIL, params, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                smart_communal_refresh.finishRefresh();
                descripDetailList.clear();
                Gson gson = new Gson();
                dmlSearchDetailEntity = gson.fromJson(result, DmlSearchDetailEntity.class);
                if (dmlSearchDetailEntity != null) {
                    if (dmlSearchDetailEntity.getCode().equals(SUCCESS_CODE)) {
                        dmlSearchDetailBean = dmlSearchDetailEntity.getDmlSearchDetailBean();
                        setSearchData(dmlSearchDetailBean);
                    } else if (!dmlSearchDetailEntity.getCode().equals(EMPTY_CODE)) {
                        showToast(ArticleOfficialActivity.this, dmlSearchDetailEntity.getMsg());
                    }
                }
                NetLoadUtils.getNetInstance().showLoadSir(loadService, dmlSearchDetailBean, dmlSearchDetailEntity);
            }

            @Override
            public void onNotNetOrException() {
                smart_communal_refresh.finishRefresh();
                NetLoadUtils.getNetInstance().showLoadSir(loadService, dmlSearchDetailBean, dmlSearchDetailEntity);
            }

            @Override
            public void netClose() {
                showToast(ArticleOfficialActivity.this, R.string.unConnectedNetwork);
            }

            @Override
            public void onError(Throwable throwable) {
                showToast(ArticleOfficialActivity.this, R.string.invalidData);
            }
        });
    }

    private void setSearchData(DmlSearchDetailBean dmlSearchDetailBean) {
//        是否有分享数据
        tv_header_titleAll.setText(getStrings(dmlSearchDetailBean.getTitle()));
        GlideImageLoaderUtil.loadCenterCrop(ArticleOfficialActivity.this, coverTitleView.img_article_details_bg, dmlSearchDetailBean.getPath());
        coverTitleView.tv_article_details_title.setText(getStrings(dmlSearchDetailBean.getTitle()));
        coverTitleView.tv_article_details_time.setText(getStrings(dmlSearchDetailBean.getCtime()));
        if (!TextUtils.isEmpty(dmlSearchDetailBean.getCategory_title())) {
            String tagName = dmlSearchDetailBean.getCategory_title();
            String content = "来自于 " + tagName;

            Link replyNameLink = new Link(tagName);
//                    回复颜色
            replyNameLink.setTextColor(Color.parseColor("#0a88fa"));
            replyNameLink.setUnderlined(false);
            replyNameLink.setHighlightAlpha(0f);
            coverTitleView.tv_article_label.setText(content);
            LinkBuilder.on(coverTitleView.tv_article_label)
                    .setText(content)
                    .addLink(replyNameLink)
                    .build();
        } else {
            coverTitleView.tv_article_label.setVisibility(GONE);
        }
        searchProductList.clear();
        if (dmlSearchDetailBean.getProductList() != null
                && dmlSearchDetailBean.getProductList().size() > 0) {
            searchProductList.addAll(dmlSearchDetailBean.getProductList());
            searchSlideProAdapter.setNewData(searchProductList);
            setPrepareData(dmlSearchDetailBean);
        } else {
            tv_communal_pro_tag.setVisibility(GONE);
            ll_communal_pro_list.setVisibility(GONE);
        }
        rel_article_bottom.setVisibility(VISIBLE);
        tv_article_bottom_like.setSelected(dmlSearchDetailBean.isIsFavor());
        tv_article_bottom_like.setText(dmlSearchDetailBean.getFavor() > 0 ? String.valueOf(dmlSearchDetailBean.getFavor()) : "赞");
        tv_article_bottom_collect.setSelected(dmlSearchDetailBean.isIsCollect());
        tv_article_bottom_collect.setText("收藏");
        List<CommunalDetailBean> descriptionList = dmlSearchDetailBean.getDescription();
        if (descriptionList != null) {
            descripDetailList.clear();
            descripDetailList.addAll(CommunalWebDetailUtils.getCommunalWebInstance().getWebDetailsFormatDataList(descriptionList));
            if (dmlSearchDetailBean.getDocumentProductList() != null
                    && dmlSearchDetailBean.getDocumentProductList().size() > 0) {
                CommunalDetailObjectBean communalDetailBean = new CommunalDetailObjectBean();
                communalDetailBean.setItemType(TYPE_PRODUCT_RECOMMEND);
                communalDetailBean.setProductList(dmlSearchDetailBean.getDocumentProductList());
                descripDetailList.add(communalDetailBean);
            }
            if (dmlSearchDetailBean.getTags() != null
                    && dmlSearchDetailBean.getTags().size() > 0) {
                CommunalDetailObjectBean communalDetailBean = new CommunalDetailObjectBean();
                communalDetailBean.setItemType(TYPE_PRODUCT_TAG);
                communalDetailBean.setTagsBeans(dmlSearchDetailBean.getTags());
                descripDetailList.add(communalDetailBean);
            }
            getShareData();
            communalDescripAdapter.setNewData(descripDetailList);
        }
    }

    private void getShareData() {
        NetLoadUtils.getNetInstance().loadNetDataPost(this, SHARE_COMMUNAL_ARTICLE, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                CommunalOnlyDescription communalOnlyDescription = gson.fromJson(result, CommunalOnlyDescription.class);
                if (communalOnlyDescription != null) {
                    if (communalOnlyDescription.getCode().equals(SUCCESS_CODE)
                            && communalOnlyDescription.getComOnlyDesBean() != null) {
                        ComOnlyDesBean comOnlyDesBean = communalOnlyDescription.getComOnlyDesBean();
                        if (comOnlyDesBean.getDescriptionList() != null && comOnlyDesBean.getDescriptionList().size() > 0) {
                            CommunalDetailObjectBean communalDetailObjectBean = new CommunalDetailObjectBean();
                            communalDetailObjectBean.setItemType(CommunalDetailObjectBean.TYPE_SHARE);
                            descripDetailList.add(communalDetailObjectBean);
                            descripDetailList.addAll(CommunalWebDetailUtils.getCommunalWebInstance().getWebDetailsFormatDataList(comOnlyDesBean.getDescriptionList()));
                            communalDescripAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private void setPrepareData(DmlSearchDetailBean dmlSearchDetailBean) {
        tv_communal_pro_tag.setVisibility(VISIBLE);
        ll_communal_pro_list.setVisibility(VISIBLE);
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
        tv_communal_pro_tag.setBackground(drawable);
        tv_communal_pro_tag.setText((searchProductList.size() + "商品"));
        tv_communal_pro_title.setText(getStrings(dmlSearchDetailBean.getTitle()));
    }

    private void setCommentLike(DmlSearchCommentBean dmlSearchCommentBean) {
        Map<String, Object> params = new HashMap<>();
        //用户id
        params.put("tuid", userId);
        //评论id
        params.put("id", dmlSearchCommentBean.getId());
        NetLoadUtils.getNetInstance().loadNetDataPost(this, FIND_AND_COMMENT_FAV, params, null);
    }

    private void setArticleLike() {
        Map<String, Object> params = new HashMap<>();
        //用户id
        params.put("tuid", userId);
        //关注id
        params.put("id", dmlSearchDetailBean.getId());
        params.put("favortype", "doc");
        NetLoadUtils.getNetInstance().loadNetDataPost(this, F_ARTICLE_DETAILS_FAVOR, params, null);
        tv_article_bottom_like.setSelected(!tv_article_bottom_like.isSelected());
        String likeCount = getNumber(tv_article_bottom_like.getText().toString().trim());
        int likeNum = Integer.parseInt(likeCount);
        tv_article_bottom_like.setText(String.valueOf(tv_article_bottom_like.isSelected()
                ? likeNum + 1 : likeNum - 1 > 0 ? likeNum - 1 : "赞"));
    }

    private String getNumber(String content) {
        String regex = "[0-9]\\d*\\.?\\d*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            return m.group();
        }
        return "0";
    }

    //    文章收藏
    private void setArticleCollect() {
        Map<String, Object> params = new HashMap<>();
        //用户id
        params.put("uid", userId);
        //文章id
        params.put("object_id", dmlSearchDetailBean.getId());
        params.put("type", ConstantVariable.TYPE_C_ARTICLE);
        NetLoadUtils.getNetInstance().loadNetDataPost(this, F_ARTICLE_COLLECT, params, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                loadHud.dismiss();
                Gson gson = new Gson();
                RequestStatus requestStatus = gson.fromJson(result, RequestStatus.class);
                if (requestStatus != null) {
                    if (requestStatus.getCode().equals(SUCCESS_CODE)) {
                        tv_article_bottom_collect.setSelected(!tv_article_bottom_collect.isSelected());
                    }
                }
            }

            @Override
            public void onNotNetOrException() {
                loadHud.dismiss();
            }

            @Override
            public void onError(Throwable throwable) {
                showToast(ArticleOfficialActivity.this, String.format(getResources().getString(R.string.collect_failed), "文章"));
            }

            @Override
            public void netClose() {
                showToast(ArticleOfficialActivity.this, R.string.unConnectedNetwork);
            }
        });
    }

    //发送评论
    private void sendComment(CommunalComment communalComment) {
        loadHud.setCancellable(true);
        loadHud.show();
        tv_send_comment.setText("发送中…");
        tv_send_comment.setEnabled(false);
        ConstantMethod constantMethod = new ConstantMethod();
        constantMethod.setOnSendCommentFinish(new ConstantMethod.OnSendCommentFinish() {
            @Override
            public void onSuccess() {
                loadHud.dismiss();
                tv_send_comment.setText("发送");
                tv_send_comment.setEnabled(true);
                showToast(ArticleOfficialActivity.this, R.string.comment_article_send_success);
                commentViewVisible(GONE, null);
                page = 1;
                getArticleComment();
                emoji_edit_comment.setText("");
            }

            @Override
            public void onError() {
                loadHud.dismiss();
                tv_send_comment.setText("发送");
                tv_send_comment.setEnabled(true);
            }
        });
        constantMethod.setSendComment(ArticleOfficialActivity.this, communalComment);
    }


    private void commentViewVisible(int visibility,
                                    final DmlSearchCommentBean dmlSearchCommentBean) {
        ll_input_comment.setVisibility(visibility);
        ll_article_comment.setVisibility(visibility == VISIBLE ? GONE : VISIBLE);
        if (VISIBLE == visibility) {
            emoji_edit_comment.requestFocus();
            //弹出键盘
            if (dmlSearchCommentBean != null) {
                emoji_edit_comment.setHint("回复" + dmlSearchCommentBean.getNickname() + ":");
            } else {
                emoji_edit_comment.setHint(getString(R.string.comment_article_hint));
            }
            CommonUtils.showSoftInput(emoji_edit_comment.getContext(), emoji_edit_comment);
            tv_send_comment.setOnClickListener((v) -> {
                //判断有内容调用接口
                String comment = emoji_edit_comment.getText().toString();
                if (!TextUtils.isEmpty(comment)) {
                    comment = emoji_edit_comment.getText().toString();
                    CommunalComment communalComment = new CommunalComment();
                    communalComment.setCommType(COMMENT_TYPE);
                    communalComment.setContent(comment);
                    if (dmlSearchCommentBean != null) {
                        communalComment.setIsReply(1);
                        communalComment.setReplyUserId(dmlSearchCommentBean.getUid());
                        communalComment.setPid(dmlSearchCommentBean.getId());
                        communalComment.setMainCommentId(dmlSearchCommentBean.getMainContentId() > 0
                                ? dmlSearchCommentBean.getMainContentId() : dmlSearchCommentBean.getId());
                    }
                    communalComment.setObjId(dmlSearchDetailBean.getId());
                    communalComment.setUserId(userId);
                    communalComment.setToUid(dmlSearchDetailBean.getUid());
                    sendComment(communalComment);
                } else {
                    showToast(ArticleOfficialActivity.this, "请正确输入内容");
                }
            });
        } else if (GONE == visibility) {
            //隐藏键盘
            CommonUtils.hideSoftInput(emoji_edit_comment.getContext(), emoji_edit_comment);
        }
    }

    @OnTouch(R.id.communal_recycler)
    boolean onTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                locationY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY() - locationY;//y轴距离
                if (Math.abs(moveY) > 250) {
                    commentViewVisible(GONE, null);
                }
                break;
            case MotionEvent.ACTION_UP:
                locationY = 0;
                break;
        }
        return false;
    }

    //    页面分享
    @OnClick(R.id.tv_header_shared)
    void sendShare() {
        if (dmlSearchDetailBean != null) {
            CommunalWebDetailUtils.getCommunalWebInstance()
                    .setShareData(this, new ShareDataBean(dmlSearchDetailBean.getPath()
                            , getStrings(dmlSearchDetailBean.getTitle())
                            , getStrings(dmlSearchDetailBean.getDigest())
                            , Url.BASE_SHARE_PAGE_TWO + ("m/template/goods/study_detail.html" + "?id="
                            + dmlSearchDetailBean.getId() + (userId > 0 ? "&sid=" + userId : "")), dmlSearchDetailBean.getId()));
        }
    }

    @OnClick(R.id.tv_life_back)
    void goBack(View view) {
        finish();
    }


    @OnClick(R.id.tv_publish_comment)
    void publishComment(View view) {
        if (dmlSearchDetailBean != null) {
            if (userId > 0) {
                if (VISIBLE == ll_input_comment.getVisibility()) {
                    commentViewVisible(GONE, null);
                } else {
                    clickScrollToComment();
                    commentViewVisible(VISIBLE, null);
                }
            } else {
                getLoginStatus(ArticleOfficialActivity.this);
            }
        }
    }

    //文章点赞
    @OnClick(R.id.tv_article_bottom_like)
    void likeArticle(View view) {
        if (dmlSearchDetailBean != null) {
            if (userId > 0) {
                setArticleLike();
            } else {
                getLoginStatus(ArticleOfficialActivity.this);
            }
        }
    }

    //文章收藏
    @OnClick(R.id.tv_article_bottom_collect)
    void collectArticle(View view) {
        if (dmlSearchDetailBean != null) {
            if (userId > 0) {
                loadHud.show();
                setArticleCollect();
            } else {
                getLoginStatus(ArticleOfficialActivity.this);
            }
        }
    }

    @OnClick(R.id.tv_communal_pro_tag)
    void openSlideProList() {
//            商品列表
        if (dl_art_detail_pro.isDrawerOpen(ll_communal_pro_list)) {
            dl_art_detail_pro.closeDrawers();
        } else {
            dl_art_detail_pro.openDrawer(ll_communal_pro_list);
        }
    }

    @Override
    protected void postEventResult(@NonNull EventMessage message) {
        if (message.type.equals("showEditView")) {
            ReplyCommListBean replyCommListBean = (ReplyCommListBean) message.result;
            DmlSearchCommentBean dmlSearchCommentBean = new DmlSearchCommentBean();
            dmlSearchCommentBean.setNickname(replyCommListBean.getNickname());
            dmlSearchCommentBean.setUid(replyCommListBean.getUid());
            dmlSearchCommentBean.setId(replyCommListBean.getId());
            dmlSearchCommentBean.setMainContentId(replyCommListBean.getMainContentId());
            dmlSearchCommentBean.setObj_id(replyCommListBean.getObj_id());
            if (VISIBLE == ll_input_comment.getVisibility()) {
                commentViewVisible(GONE, dmlSearchCommentBean);
            } else {
                commentViewVisible(VISIBLE, dmlSearchCommentBean);
            }
        } else if (message.type.equals("replyComm")) {
            List<ReplyCommListBean> replyCommList = (List<ReplyCommListBean>) message.result;
            ReplyCommListBean replyCommListBean = replyCommList.get(replyCommList.size() - 1);
            for (int i = 0; i < articleCommentList.size(); i++) {
                DmlSearchCommentBean dmlSearchCommentBean = articleCommentList.get(i);
                if (dmlSearchCommentBean.getId() == replyCommListBean.getMainContentId()) {
                    dmlSearchCommentBean.setReplyCommList(replyCommList);
                    articleCommentList.set(i, dmlSearchCommentBean);
                }
            }
        }
    }

    class CoverTitleView {
        //        标题
        @BindView(R.id.tv_article_details_title)
        TextView tv_article_details_title;
        //        封面图
        @BindView(R.id.img_article_details_bg)
        ImageView img_article_details_bg;
        //        发布时间
        @BindView(R.id.tv_article_details_time)
        TextView tv_article_details_time;
        //        文章类别
        @BindView(R.id.tv_article_label)
        TextView tv_article_label;
        //        文章内容
        @BindView(R.id.communal_recycler_wrap)
        RecyclerView communal_recycler_wrap;
        //        评论数

        public void initView() {
            communal_recycler_wrap.setLayoutManager(new LinearLayoutManager(ArticleOfficialActivity.this));
            communal_recycler_wrap.setNestedScrollingEnabled(false);
            communalDescripAdapter = new CommunalDetailAdapter(ArticleOfficialActivity.this, descripDetailList);
            communalDescripAdapter.setEnableLoadMore(false);
            communal_recycler_wrap.setAdapter(communalDescripAdapter);
            communalDescripAdapter.setOnItemClickListener((adapter, view, position) -> {
                CommunalDetailObjectBean communalDetailBean = (CommunalDetailObjectBean) view.getTag();
                if (communalDetailBean != null) {
                    skipProductUrl(ArticleOfficialActivity.this, communalDetailBean.getItemTypeId(), communalDetailBean.getId());
//                    统计商品点击
                    totalProNum(communalDetailBean.getId(), dmlSearchDetailBean.getId());
                }
            });
            communalDescripAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                ShareDataBean shareDataBean = null;
                if (view.getId() == R.id.tv_communal_share && dmlSearchDetailBean != null) {
                    shareDataBean = new ShareDataBean(dmlSearchDetailBean.getPath()
                            , getStrings(dmlSearchDetailBean.getTitle())
                            , getStrings(dmlSearchDetailBean.getDigest())
                            , Url.BASE_SHARE_PAGE_TWO + ("m/template/goods/study_detail.html" + "?id="
                            + dmlSearchDetailBean.getId() + (userId > 0 ? "&sid=" + userId : "")), dmlSearchDetailBean.getId());

                }
                CommunalWebDetailUtils.getCommunalWebInstance()
                        .setWebDataClick(ArticleOfficialActivity.this, shareDataBean, view, loadHud);
            });
        }

        @OnClick(R.id.tv_article_label)
        void skipArticleLabel() {
            if (dmlSearchDetailBean.getCategory_id() > 0
                    && !TextUtils.isEmpty(dmlSearchDetailBean.getCategory_title())) {
                Intent intent = new Intent(ArticleOfficialActivity.this, ArticleTypeActivity.class);
                intent.putExtra("categoryId", String.valueOf(dmlSearchDetailBean.getCategory_id()));
                intent.putExtra("categoryTitle", getStrings(dmlSearchDetailBean.getCategory_title()));
                startActivity(intent);
            }
        }
    }

    public class CommentCountView {
        @BindView(R.id.tv_comm_comment_count)
        TextView tv_comm_comment_count;
    }

    @Override
    protected void onPause() {
        super.onPause();
        insertTotalData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        insertTotalData();
    }

    private void insertTotalData() {
        if (totalPersonalTrajectory != null) {
            Map<String, String> totalMap = new HashMap<>();
            totalMap.put("relate_id", artId);
            totalPersonalTrajectory.stopTotal(totalMap);
        }
    }

    /**
     * 滚动到评论区
     */
    private void scrollToComment() {
        int headerLayoutCount = adapterArticleComment.getHeaderLayoutCount();
        LinearLayout headerLayout = adapterArticleComment.getHeaderLayout();
        if (headerLayout != null) {
            final boolean[] isFirstSame = {true};
            final int[] measureHeight = {0};
            headerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int measuredNewHeight = headerLayout.getMeasuredHeight();
                    if (measuredNewHeight > screenHeight || (measureHeight[0] == measuredNewHeight && !isFirstSame[0])) {
                        headerLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (measuredNewHeight > screenHeight) {
                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) communal_recycler.getLayoutManager();
                            linearLayoutManager.scrollToPositionWithOffset(headerLayoutCount, 300);
                            linearLayoutManager.setStackFromEnd(true);
                        }
                    } else if (measureHeight[0] == measuredNewHeight && isFirstSame[0]) {
                        /**
                         * 因会出现两次测量结果一致，故避免这情况
                         */
                        isFirstSame[0] = false;
                    } else {
                        measureHeight[0] = measuredNewHeight;
                    }
                }
            });
        }
    }

    /**
     * 点击评论滑动到评论区
     */
    private void clickScrollToComment() {
        int headerLayoutCount = adapterArticleComment.getHeaderLayoutCount();
        LinearLayout headerLayout = adapterArticleComment.getHeaderLayout();
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) communal_recycler.getLayoutManager();
        int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        if (lastCompletelyVisibleItemPosition < headerLayoutCount) {
            int measuredNewHeight = headerLayout.getMeasuredHeight();
            if (measuredNewHeight > screenHeight) {
                linearLayoutManager.scrollToPositionWithOffset(headerLayoutCount, 300);
                linearLayoutManager.setStackFromEnd(true);
            }
        }
    }

    @Override
    public void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.colorPrimary).titleBar(tl_normal_bar).keyboardEnable(true).navigationBarEnable(false)
                .statusBarDarkFont(true).init();
    }
}
