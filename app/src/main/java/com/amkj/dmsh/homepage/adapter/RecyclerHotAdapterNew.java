package com.amkj.dmsh.homepage.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amkj.dmsh.R;
import com.amkj.dmsh.base.TinkerBaseApplicationLike;
import com.amkj.dmsh.constant.BaseViewHolderHelper;
import com.amkj.dmsh.homepage.bean.CommunalADActivityEntity.CommunalADActivityBean;
import com.amkj.dmsh.utils.glide.GlideImageLoaderUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tencent.bugly.beta.tinker.TinkerManager;
import com.zhy.autolayout.AutoLayoutInfo;
import com.zhy.autolayout.attr.WidthAttr;
import com.zhy.autolayout.utils.AutoLayoutHelper;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

import static com.amkj.dmsh.constant.ConstantMethod.getStrings;

;

/**
 * Created by LGuipeng on 2016/8/28.
 */
public class RecyclerHotAdapterNew extends BaseQuickAdapter<CommunalADActivityBean, RecyclerHotAdapterNew.HotViewHolderHelper> {
    private final List<CommunalADActivityBean> communalADActivityBeanList;
    private Context context;
    private final TinkerBaseApplicationLike app;
    private final int screenWidth;

    public RecyclerHotAdapterNew(Context context, List<CommunalADActivityBean> communalADActivityBeanList) {
        super(R.layout.adapter_homepage_hot_activity, communalADActivityBeanList);
        this.context = context;
        this.communalADActivityBeanList = communalADActivityBeanList;
        app = (TinkerBaseApplicationLike) TinkerManager.getTinkerApplicationLike();
        screenWidth = app.getScreenWidth();
    }

    @Override
    protected void convert(HotViewHolderHelper holder, CommunalADActivityBean communalADActivityBean) {
        GlideImageLoaderUtil.loadFitCenter(context, (ImageView) holder.getView(R.id.iv_home_hot_img), communalADActivityBean.getPicUrl());
        holder.setText(R.id.tv_home_hot_title, getStrings(communalADActivityBean.getTitle()));
        holder.itemView.setTag(communalADActivityBean);
    }

    public class HotViewHolderHelper extends BaseViewHolderHelper {
        LinearLayout ll_hot_layout;

        public HotViewHolderHelper(View view) {
            super(view);
            ll_hot_layout = (LinearLayout) view.findViewById(R.id.ll_hot_layout);
            ll_hot_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ll_hot_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int width = (int) ((screenWidth / (communalADActivityBeanList.size() > 4 ? 4 : communalADActivityBeanList.size()) + 1) / AutoUtils.getPercentWidth1px());
                    AutoLayoutHelper.AutoLayoutParams autoLayoutParams = (AutoLayoutHelper.AutoLayoutParams) ll_hot_layout.getLayoutParams();
                    AutoLayoutInfo autoLayoutInfo = autoLayoutParams.getAutoLayoutInfo();
                    autoLayoutInfo.addAttr(new WidthAttr(width, 0, 0));
                    ll_hot_layout.setLayoutParams(ll_hot_layout.getLayoutParams());
                }
            });
        }
    }
}
