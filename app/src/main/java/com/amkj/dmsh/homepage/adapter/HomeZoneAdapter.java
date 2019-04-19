package com.amkj.dmsh.homepage.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.amkj.dmsh.R;
import com.amkj.dmsh.homepage.bean.HomeCommonEntity.HomeCommonBean;
import com.amkj.dmsh.homepage.bean.HomeCommonEntity.ProductInfoListBean;
import com.amkj.dmsh.utils.glide.GlideImageLoaderUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import static com.amkj.dmsh.constant.ConstantMethod.getStrings;

/**
 * Created by xiaoxin on 2019/4/18 0018
 * Version:v4.0.0
 * ClassDescription :
 */
public class HomeZoneAdapter extends BaseQuickAdapter<HomeCommonBean, BaseViewHolder> {

    private final Context mContext;

    public HomeZoneAdapter(Context context, @Nullable List<HomeCommonBean> data) {
        super(R.layout.item_home_zone, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeCommonBean item) {
        if (item == null) return;
        //专区名称
        helper.setText(R.id.tv_zone_name, item.getName());
        List<ProductInfoListBean> productInfoList = item.getProductInfoList();

        //专区左边商品
        ProductInfoListBean productInfoListBean = productInfoList.get(0);
        if (productInfoListBean!=null){
            GlideImageLoaderUtil.loadImage(mContext, helper.getView(R.id.iv_cover_left), productInfoListBean.getImg());
            helper.setText(R.id.tv_price_left, "¥" + getStrings(productInfoListBean.getPrice()));
            helper.setText(R.id.tv_market_price_left, "¥" + getStrings(productInfoListBean.getMarketPrice()));
            ((TextView) helper.getView(R.id.tv_market_price_left)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }


        //专区右边商品
        helper.getView(R.id.rl_right).setVisibility(productInfoList.size() > 1 ? View.VISIBLE : View.GONE);
        if (productInfoList.size() > 1) {
            productInfoListBean = productInfoList.get(1);
            if (productInfoListBean!=null){
                GlideImageLoaderUtil.loadImage(mContext, helper.getView(R.id.iv_cover_right), productInfoListBean.getImg());
                helper.setText(R.id.tv_price_right, "¥" + getStrings(productInfoListBean.getPrice()));
                helper.setText(R.id.tv_market_price_right, "¥" + getStrings(productInfoListBean.getMarketPrice()));
                ((TextView) helper.getView(R.id.tv_market_price_right)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        helper.itemView.setTag(item);
    }
}