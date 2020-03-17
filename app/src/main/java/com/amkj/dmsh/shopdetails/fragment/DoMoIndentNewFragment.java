package com.amkj.dmsh.shopdetails.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.amkj.dmsh.R;
import com.amkj.dmsh.base.BaseFragment;
import com.amkj.dmsh.bean.RequestStatus;
import com.amkj.dmsh.constant.Url;
import com.amkj.dmsh.dao.GroupDao;
import com.amkj.dmsh.dominant.bean.GroupShopDetailsEntity.GroupShopDetailsBean;
import com.amkj.dmsh.mine.bean.CartProductInfoBean;
import com.amkj.dmsh.network.NetLoadListenerHelper;
import com.amkj.dmsh.network.NetLoadUtils;
import com.amkj.dmsh.shopdetails.activity.DirectApplyRefundActivity;
import com.amkj.dmsh.shopdetails.activity.DirectExchangeDetailsActivity;
import com.amkj.dmsh.shopdetails.activity.DirectIndentWriteActivity;
import com.amkj.dmsh.shopdetails.activity.DirectLogisticsDetailsActivity;
import com.amkj.dmsh.shopdetails.adapter.DoMoIndentListAdapter;
import com.amkj.dmsh.shopdetails.bean.DirectApplyRefundBean;
import com.amkj.dmsh.shopdetails.bean.DirectApplyRefundBean.DirectRefundProBean;
import com.amkj.dmsh.shopdetails.bean.InquiryOrderEntry;
import com.amkj.dmsh.shopdetails.bean.InquiryOrderEntry.OrderInquiryDateEntry.OrderListBean;
import com.amkj.dmsh.shopdetails.bean.InquiryOrderEntry.OrderInquiryDateEntry.OrderListBean.GoodsBean;
import com.amkj.dmsh.utils.alertdialog.AlertDialogHelper;
import com.amkj.dmsh.utils.itemdecoration.ItemDecoration;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static com.amkj.dmsh.base.TinkerBaseApplicationLike.mAppContext;
import static com.amkj.dmsh.constant.ConstantMethod.getLoginStatus;
import static com.amkj.dmsh.constant.ConstantMethod.showToast;
import static com.amkj.dmsh.constant.ConstantMethod.showToastRequestMsg;
import static com.amkj.dmsh.constant.ConstantMethod.userId;
import static com.amkj.dmsh.constant.ConstantVariable.BUY_AGAIN;
import static com.amkj.dmsh.constant.ConstantVariable.CANCEL_ORDER;
import static com.amkj.dmsh.constant.ConstantVariable.CANCEL_PAY_ORDER;
import static com.amkj.dmsh.constant.ConstantVariable.CHECK_LOG;
import static com.amkj.dmsh.constant.ConstantVariable.CONFIRM_ORDER;
import static com.amkj.dmsh.constant.ConstantVariable.DEL;
import static com.amkj.dmsh.constant.ConstantVariable.EMPTY_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.INDENT_PRO_STATUS;
import static com.amkj.dmsh.constant.ConstantVariable.INVITE_GROUP;
import static com.amkj.dmsh.constant.ConstantVariable.LITTER_CONSIGN;
import static com.amkj.dmsh.constant.ConstantVariable.PAY;
import static com.amkj.dmsh.constant.ConstantVariable.REFUND_TYPE;
import static com.amkj.dmsh.constant.ConstantVariable.REMIND_DELIVERY;
import static com.amkj.dmsh.constant.ConstantVariable.SUCCESS_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.TOTAL_COUNT_TEN;
import static com.amkj.dmsh.constant.Url.Q_INQUIRY_WAIT_SEND_EXPEDITING;


/**
 * Created by xiaoxin on 2020/3/14
 * Version:v4.4.3
 * ClassDescription :订单列表重构
 */
public class DoMoIndentNewFragment extends BaseFragment {
    @BindView(R.id.smart_communal_refresh)
    SmartRefreshLayout smart_communal_refresh;
    @BindView(R.id.communal_recycler)
    RecyclerView communal_recycler;
    @BindView(R.id.download_btn_communal)
    public FloatingActionButton download_btn_communal;
    List<OrderListBean> orderListBeanList = new ArrayList<>();
    private int page = 1;
    private DoMoIndentListAdapter doMoIndentListAdapter;
    private OrderListBean orderBean;
    private boolean isOnPause = false;
    private InquiryOrderEntry inquiryOrderEntry;
    private AlertDialogHelper delOrderDialogHelper;
    private AlertDialogHelper cancelOrderDialogHelper;
    private AlertDialogHelper confirmOrderDialogHelper;
    //0.全部订单  1.待付款  2.待发货  3.待收货  4.待评价
    private String[] urls = new String[]{Url.Q_INQUIRY_ALL_ORDER, Url.Q_INQUIRY_WAIT_PAY, Url.Q_INQUIRY_WAIT_SEND, Url.Q_INQUIRY_DEL_IVERED, Url.Q_INQUIRY_FINISH};
    private int mType;

    @Override
    protected int getContentView() {
        return R.layout.layout_communal_smart_refresh_recycler_float_loading;
    }

    @Override
    protected void initViews() {
        getLoginStatus(this);
        communal_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        communal_recycler.addItemDecoration(new ItemDecoration.Builder()
                // 设置分隔线资源ID
                .setDividerId(R.drawable.item_divider_ten_dp).create());
        doMoIndentListAdapter = new DoMoIndentListAdapter(getActivity(), orderListBeanList);
        communal_recycler.setAdapter(doMoIndentListAdapter);
        setFloatingButton(download_btn_communal, communal_recycler);
        smart_communal_refresh.setOnRefreshListener(refreshLayout -> loadData());
        doMoIndentListAdapter.setOnLoadMoreListener(() -> {
            page++;
            getIndentList();
        }, communal_recycler);

        doMoIndentListAdapter.setOnClickViewListener((type, orderListBean) -> {
            orderBean = orderListBean;
            if (orderBean == null) return;
            Intent intent = new Intent();
            Bundle bundle;
            switch (type) {
                //再次购买
                case BUY_AGAIN:
                    intent.setClass(getActivity(), DirectIndentWriteActivity.class);
                    intent.putExtra("orderNo", orderListBean.getNo());
                    startActivity(intent);
                    break;
                //提醒发货
                case REMIND_DELIVERY:
                    if (loadHud != null) {
                        loadHud.show();
                    }
                    setRemindDelivery(orderBean);
                    break;
                //取消订单（待支付）
                case CANCEL_ORDER:
                    if (cancelOrderDialogHelper == null) {
                        cancelOrderDialogHelper = new AlertDialogHelper(getActivity());
                        cancelOrderDialogHelper.setTitleVisibility(View.GONE).setMsgTextGravity(Gravity.CENTER)
                                .setMsg("确定要取消当前订单？").setCancelText("取消").setConfirmText("确定")
                                .setCancelTextColor(getResources().getColor(R.color.text_login_gray_s));
                        cancelOrderDialogHelper.setAlertListener(new AlertDialogHelper.AlertConfirmCancelListener() {
                            @Override
                            public void confirm() {
                                cancelOrder();
                            }

                            @Override
                            public void cancel() {
                            }
                        });
                    }
                    cancelOrderDialogHelper.show();
                    break;
                //取消订单(待发货)
                case CANCEL_PAY_ORDER:
                    DirectApplyRefundBean refundBean = new DirectApplyRefundBean();
                    refundBean.setType(3);
                    refundBean.setOrderNo(orderListBean.getNo());
                    List<DirectRefundProBean> directProList = new ArrayList<>();
                    List<CartProductInfoBean> cartProductInfoList;
                    DirectRefundProBean directRefundProBean;
                    for (int i = 0; i < orderListBean.getGoods().size(); i++) {
                        GoodsBean goodsBean = orderListBean.getGoods().get(i);
                        cartProductInfoList = new ArrayList<>();
                        directRefundProBean = new DirectRefundProBean();
                        directRefundProBean.setId(goodsBean.getId());
                        directRefundProBean.setOrderProductId(goodsBean.getOrderProductId());
                        directRefundProBean.setCount(goodsBean.getCount());
                        directRefundProBean.setName(goodsBean.getName());
                        directRefundProBean.setPicUrl(goodsBean.getPicUrl());
                        directRefundProBean.setSaleSkuValue(goodsBean.getSaleSkuValue());
                        directRefundProBean.setPrice(goodsBean.getPrice());
                        if (goodsBean.getPresentProductInfoList() != null && goodsBean.getPresentProductInfoList().size() > 0) {
                            cartProductInfoList.addAll(goodsBean.getPresentProductInfoList());
                        }
                        if (goodsBean.getCombineProductInfoList() != null && goodsBean.getCombineProductInfoList().size() > 0) {
                            cartProductInfoList.addAll(goodsBean.getCombineProductInfoList());
                        }
                        if (cartProductInfoList.size() > 0) {
                            directRefundProBean.setCartProductInfoList(cartProductInfoList);
                        }
                        directProList.add(directRefundProBean);
                    }
                    refundBean.setDirectRefundProList(directProList);
                    intent.setClass(getActivity(), DirectApplyRefundActivity.class);
                    intent.putExtra(REFUND_TYPE, REFUND_TYPE);
                    bundle = new Bundle();
                    bundle.putParcelable("refundPro", refundBean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                //订单支付
                case PAY:
                    intent.setClass(getActivity(), DirectExchangeDetailsActivity.class);
                    intent.putExtra("orderNo", orderListBean.getNo());
                    startActivity(intent);
                    break;
                //查看物流，部分发货
                case CHECK_LOG:
                case LITTER_CONSIGN:
                    intent.setClass(getActivity(), DirectLogisticsDetailsActivity.class);
                    intent.putExtra("orderNo", orderListBean.getNo());
                    startActivity(intent);
                    break;
                //确认收货
                case CONFIRM_ORDER:
                    if (confirmOrderDialogHelper == null) {
                        confirmOrderDialogHelper = new AlertDialogHelper(getActivity());
                        confirmOrderDialogHelper.setTitleVisibility(View.GONE).setMsgTextGravity(Gravity.CENTER)
                                .setMsg("确定已收到货物?").setCancelText("取消").setConfirmText("确定")
                                .setCancelTextColor(getResources().getColor(R.color.text_login_gray_s));
                        confirmOrderDialogHelper.setAlertListener(new AlertDialogHelper.AlertConfirmCancelListener() {
                            @Override
                            public void confirm() {
                                confirmOrder();
                            }

                            @Override
                            public void cancel() {
                            }
                        });
                    }
                    confirmOrderDialogHelper.show();
                    break;
                //删除订单
                case DEL:
                    if (delOrderDialogHelper == null) {
                        delOrderDialogHelper = new AlertDialogHelper(getActivity());
                        delOrderDialogHelper.setTitleVisibility(View.GONE).setMsgTextGravity(Gravity.CENTER)
                                .setMsg("确定要删除该订单？").setCancelText("取消").setConfirmText("确定")
                                .setCancelTextColor(getResources().getColor(R.color.text_login_gray_s));
                        delOrderDialogHelper.setAlertListener(new AlertDialogHelper.AlertConfirmCancelListener() {
                            @Override
                            public void confirm() {
                                delOrder();
                            }

                            @Override
                            public void cancel() {
                            }
                        });
                    }
                    delOrderDialogHelper.show();
                    break;
                //邀请参团
                case INVITE_GROUP:
                    List<GoodsBean> goods = orderListBean.getGoods();
                    if (goods != null && goods.size() > 0) {
                        GoodsBean goodsBean = goods.get(0);
                        GroupShopDetailsBean groupShopDetailsBean = new GroupShopDetailsBean();
                        groupShopDetailsBean.setCoverImage(goodsBean.getPicUrl());
                        groupShopDetailsBean.setGpName(goodsBean.getName());
                        groupShopDetailsBean.setType(orderListBean.getType());
                        GroupDao.invitePartnerGroup(getActivity(), groupShopDetailsBean, orderListBean.getNo());
                    }
                    break;
            }
        });
    }

    @Override
    protected void loadData() {
        page = 1;
        getIndentList();
    }

    @Override
    protected boolean isAddLoad() {
        return true;
    }

    //获取订单列表数据
    private void getIndentList() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("showCount", TOTAL_COUNT_TEN);
        params.put("currentPage", page);
        if (mType == 0 || mType == 2 || mType == 3) {
            params.put("orderType", "currency");
        }
        //版本号控制 3 组合商品赠品
        params.put("version", 3);
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), urls[mType], params, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                smart_communal_refresh.finishRefresh();
                if (page == 1) {
                    orderListBeanList.clear();
                }
                Gson gson = new Gson();
                String code = "";
                String msg = "";
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    code = (String) jsonObject.get("code");
                    msg = (String) jsonObject.get("msg");
                    inquiryOrderEntry = gson.fromJson(result, InquiryOrderEntry.class);
                    if (inquiryOrderEntry != null) {
                        InquiryOrderEntry.OrderInquiryDateEntry orderInquiryDateEntry = inquiryOrderEntry.getOrderInquiryDateEntry();
                        List<OrderListBean> orderList = orderInquiryDateEntry.getOrderList();
                        if (orderList == null || orderList.size() < 1 || EMPTY_CODE.equals(code)) {
                            doMoIndentListAdapter.loadMoreEnd();
                        } else if (SUCCESS_CODE.equals(code)) {
                            if (!TextUtils.isEmpty(orderInquiryDateEntry.getCurrentTime())) {
                                for (int i = 0; i < orderInquiryDateEntry.getOrderList().size(); i++) {
                                    OrderListBean orderListBean = orderList.get(i);
                                    orderListBean.setCurrentTime(orderInquiryDateEntry.getCurrentTime());
                                }
                            }
                            INDENT_PRO_STATUS = inquiryOrderEntry.getOrderInquiryDateEntry().getStatus();
                            orderListBeanList.addAll(orderList);
                            //不满一页
                            if (orderListBeanList.size() < TOTAL_COUNT_TEN) {
                                doMoIndentListAdapter.loadMoreEnd();
                            } else {
                                doMoIndentListAdapter.loadMoreComplete();
                            }
                        } else {
                            showToast(getActivity(), msg);
                            doMoIndentListAdapter.loadMoreFail();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                doMoIndentListAdapter.notifyDataSetChanged();
                NetLoadUtils.getNetInstance().showLoadSirString(loadService, orderListBeanList, code);
            }

            @Override
            public void onNotNetOrException() {
                smart_communal_refresh.finishRefresh();
                doMoIndentListAdapter.loadMoreEnd(true);
                NetLoadUtils.getNetInstance().showLoadSir(loadService, orderListBeanList, inquiryOrderEntry);
            }
        });
    }

    //  订单删除
    private void delOrder() {
        String url = Url.Q_INDENT_DEL;
        Map<String, Object> params = new HashMap<>();
        params.put("no", orderBean.getNo());
        params.put("userId", userId);
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), url, params, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                RequestStatus requestStatus = gson.fromJson(result, RequestStatus.class);
                if (requestStatus != null) {
                    if (requestStatus.getCode().equals(SUCCESS_CODE)) {
                        loadData();
                        showToast(getActivity(), String.format(getResources().getString(R.string.doSuccess), "删除订单"));
                    } else {
                        showToastRequestMsg(getActivity(), requestStatus);
                    }
                }
            }
        });
    }

    private void confirmOrder() {
        String url = Url.Q_INDENT_CONFIRM;
        Map<String, Object> params = new HashMap<>();
        params.put("no", orderBean.getNo());
        params.put("userId", userId);
        params.put("orderProductId",/*orderBean.getId()*/0);
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), url, params, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                RequestStatus requestStatus = gson.fromJson(result, RequestStatus.class);
                if (requestStatus != null) {
                    if (requestStatus.getCode().equals(SUCCESS_CODE)) {
                        loadData();
                        showToastRequestMsg(getActivity(), requestStatus);
                    } else {
                        showToastRequestMsg(getActivity(), requestStatus);
                    }
                }
            }
        });
    }

    //  取消订单
    private void cancelOrder() {
        String url = Url.Q_INDENT_CANCEL;
        Map<String, Object> params = new HashMap<>();
        params.put("no", orderBean.getNo());
        params.put("userId", userId);
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), url, params, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                RequestStatus requestStatus = gson.fromJson(result, RequestStatus.class);
                if (requestStatus != null) {
                    if (requestStatus.getCode().equals(SUCCESS_CODE)) {
                        showToastRequestMsg(getActivity(), requestStatus);
                        loadData();
                    } else {
                        showToastRequestMsg(getActivity(), requestStatus);
                    }
                }
            }
        });
    }

    //提醒发货
    private void setRemindDelivery(OrderListBean orderBean) {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", userId);
        params.put("orderNo", orderBean.getNo());
        NetLoadUtils.getNetInstance().loadNetDataPost(getActivity(), Q_INQUIRY_WAIT_SEND_EXPEDITING, params, new NetLoadListenerHelper() {
            @Override
            public void onSuccess(String result) {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                Gson gson = new Gson();
                RequestStatus requestStatus = gson.fromJson(result, RequestStatus.class);
                if (requestStatus != null) {
                    if (requestStatus.getCode().equals(SUCCESS_CODE)) {
                        orderBean.setWaitDeliveryFlag(false);
                        showToast(mAppContext, "已提醒商家尽快发货，请耐心等候~");
                    } else {
                        showToastRequestMsg(mAppContext, requestStatus);
                    }
                }
            }

            @Override
            public void onNotNetOrException() {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
            }

            @Override
            public void netClose() {
                showToast(mAppContext, R.string.unConnectedNetwork);
            }

            @Override
            public void onError(Throwable throwable) {
                showToast(mAppContext, R.string.do_failed);
            }
        });
    }

    @Override
    protected boolean isDataInitiated() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isOnPause) {
            loadData();
        }
        isOnPause = true;
    }

    @Override
    protected void getReqParams(Bundle bundle) {
        if (bundle != null) {
            mType = (int) bundle.get("type");
        }
    }
}
