package com.amkj.dmsh.dominant.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.amkj.dmsh.R;
import com.amkj.dmsh.user.bean.UserLikedProductEntity;
import com.amkj.dmsh.user.bean.UserLikedProductEntity.LikedProductBean;
import com.amkj.dmsh.user.bean.UserLikedProductEntity.LikedProductBean.MarketLabelBean;
import com.amkj.dmsh.utils.glide.GlideImageLoaderUtil;
import com.amkj.dmsh.utils.itemdecoration.ItemDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

import static com.amkj.dmsh.constant.ConstantMethod.getStrings;
import static com.amkj.dmsh.utils.ProductLabelCreateUtils.getLabelInstance;

/**
 * Created by xiaoxin on 2019/4/16 0016
 * Version:v4.0.0
 * ClassDescription :新版首页分类商品适配器
 */

public class HomeProductAdapter extends BaseQuickAdapter<UserLikedProductEntity, BaseViewHolder> {
    private final Context context;

    public HomeProductAdapter(Context context, List<UserLikedProductEntity> productList) {
        super(R.layout.item_home_catergory_product, productList);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, UserLikedProductEntity userLikedProductEntity) {
        if (userLikedProductEntity == null) return;
        helper.setText(R.id.tv_catergory_name, userLikedProductEntity.getCatergoryName());
        GlideImageLoaderUtil.loadImage(mContext, helper.getView(R.id.iv_ad), userLikedProductEntity.getAdCover());
        RecyclerView rvGoods = helper.getView(R.id.rv_catergory_goods);
        //初始化新人专享适配器
        GridLayoutManager newUserManager = new GridLayoutManager(mContext
                , 3);
        rvGoods.setLayoutManager(newUserManager);
        ItemDecoration itemDecoration = new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_fifteen_white)
                .create();
        rvGoods.addItemDecoration(itemDecoration);

        BaseQuickAdapter<LikedProductBean, BaseViewHolder> baseQuickAdapter = new BaseQuickAdapter<LikedProductBean, BaseViewHolder>(R.layout.adapter_layout_qt_pro) {
            @Override
            protected void convert(BaseViewHolder helper, LikedProductBean likedProductBean) {
                if (likedProductBean == null) return;
                GlideImageLoaderUtil.loadThumbCenterCrop(context, (ImageView) helper.getView(R.id.iv_qt_pro_img)
                        , likedProductBean.getPicUrl(), likedProductBean.getWaterRemark(), true);
                helper.setGone(R.id.iv_com_pro_tag_out, likedProductBean.getQuantity() < 1)
                        .setGone(R.id.iv_pro_add_car, true)
                        .setText(R.id.tv_qt_pro_descrip, getStrings(likedProductBean.getSubtitle()))
                        .setText(R.id.tv_qt_pro_name, !TextUtils.isEmpty(likedProductBean.getName()) ?
                                getStrings(likedProductBean.getName()) : getStrings(likedProductBean.getTitle()))
                        .setText(R.id.tv_qt_pro_price, "￥" + likedProductBean.getPrice())
                        .addOnClickListener(R.id.iv_pro_add_car).setTag(R.id.iv_pro_add_car, likedProductBean);
                FlexboxLayout fbl_market_label = helper.getView(R.id.fbl_market_label);
                if (!TextUtils.isEmpty(likedProductBean.getActivityTag()) || (likedProductBean.getMarketLabelList() != null
                        && likedProductBean.getMarketLabelList().size() > 0)) {
                    fbl_market_label.setVisibility(View.VISIBLE);
                    fbl_market_label.removeAllViews();
                    if (!TextUtils.isEmpty(likedProductBean.getActivityTag())) {
                        fbl_market_label.addView(getLabelInstance().createLabelText(context, likedProductBean.getActivityTag(), 1));
                    }
                    if (likedProductBean.getMarketLabelList() != null
                            && likedProductBean.getMarketLabelList().size() > 0) {
                        for (MarketLabelBean marketLabelBean : likedProductBean.getMarketLabelList()) {
                            if (!TextUtils.isEmpty(marketLabelBean.getTitle())) {
                                fbl_market_label.addView(getLabelInstance().createLabelText(context, marketLabelBean.getTitle(), 0));
                            }
                        }
                    }
                } else {
                    fbl_market_label.setVisibility(View.GONE);
                }
            }
        };
        rvGoods.setAdapter(baseQuickAdapter);
        helper.itemView.setTag(userLikedProductEntity);
    }
}