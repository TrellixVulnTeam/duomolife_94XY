package com.amkj.dmsh.shopdetails.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amkj.dmsh.R;
import com.amkj.dmsh.address.activity.SelectedAddressActivity;
import com.amkj.dmsh.address.bean.AddressInfoEntity;
import com.amkj.dmsh.address.widget.WheelView;
import com.amkj.dmsh.address.widget.adapters.ArrayWheelAdapter;
import com.amkj.dmsh.base.BaseActivity;
import com.amkj.dmsh.network.NetLoadUtils;
import com.amkj.dmsh.base.TinkerBaseApplicationLike;
import com.amkj.dmsh.bean.ImageBean;
import com.amkj.dmsh.bean.RequestStatus;
import com.amkj.dmsh.constant.ConstantMethod;
import com.amkj.dmsh.constant.Url;
import com.amkj.dmsh.constant.XUtil;
import com.amkj.dmsh.find.activity.ImagePagerActivity;
import com.amkj.dmsh.release.adapter.ImgGridRecyclerAdapter;
import com.amkj.dmsh.release.bean.ImagePathBean;
import com.amkj.dmsh.shopdetails.adapter.DirectProductListAdapter;
import com.amkj.dmsh.shopdetails.bean.DirectApplyRefundBean;
import com.amkj.dmsh.shopdetails.bean.DirectApplyRefundBean.DirectRefundProBean;
import com.amkj.dmsh.shopdetails.bean.RefundApplyEntity;
import com.amkj.dmsh.shopdetails.bean.RefundApplyEntity.RefundApplyBean;
import com.amkj.dmsh.shopdetails.bean.RefundApplyEntity.RefundApplyBean.MoneyAndGoodsRefundReasonBean;
import com.amkj.dmsh.shopdetails.bean.RefundApplyEntity.RefundApplyBean.MoneyRefundReasonBean;
import com.amkj.dmsh.shopdetails.bean.RefundApplyEntity.RefundApplyBean.WaitDeliveryRefundReasonBean;
import com.amkj.dmsh.utils.CommonUtils;
import com.amkj.dmsh.utils.ImgUrlHelp;
import com.amkj.dmsh.utils.alertdialog.AlertDialogHelper;
import com.amkj.dmsh.utils.inteface.MyCallBack;
import com.amkj.dmsh.utils.itemdecoration.ItemDecoration;
import com.amkj.dmsh.utils.pictureselector.PictureSelectorUtils;
import com.google.gson.Gson;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfigC;
import com.luck.picture.lib.entity.LocalMediaC;
import com.tencent.bugly.beta.tinker.TinkerManager;
import com.yanzhenjie.permission.Permission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.AutoSizeUtils;

import static android.view.View.VISIBLE;
import static com.amkj.dmsh.base.TinkerBaseApplicationLike.mAppContext;
import static com.amkj.dmsh.constant.ConstantMethod.getFloatNumber;
import static com.amkj.dmsh.constant.ConstantMethod.getLoginStatus;
import static com.amkj.dmsh.constant.ConstantMethod.getStrings;
import static com.amkj.dmsh.constant.ConstantMethod.getStringsChNPrice;
import static com.amkj.dmsh.constant.ConstantMethod.showToast;
import static com.amkj.dmsh.constant.ConstantMethod.userId;
import static com.amkj.dmsh.constant.ConstantVariable.DEFAULT_ADD_IMG;
import static com.amkj.dmsh.constant.ConstantVariable.EMPTY_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.IS_LOGIN_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.REFUND_REPAIR;
import static com.amkj.dmsh.constant.ConstantVariable.REFUND_TYPE;
import static com.amkj.dmsh.constant.ConstantVariable.SUCCESS_CODE;
import static com.amkj.dmsh.utils.ImageFormatUtils.getImageFormatInstance;

;
;


/**
 * Created by atd48 on 2016/10/27.
 * 申请退款
 */
public class DirectApplyRefundActivity extends BaseActivity{
    @BindView(R.id.tv_header_title)
    TextView tv_header_titleAll;
    @BindView(R.id.tv_header_shared)
    TextView header_shared;
    @BindView(R.id.communal_recycler_wrap)
    RecyclerView communal_recycler_wrap;
    //    退款类型
    @BindView(R.id.tv_dir_indent_apply_type)
    TextView tv_dir_indent_apply_reason_type;
    //    退款类型选择
    @BindView(R.id.tv_dir_indent_apply_type_sel)
    TextView tv_dir_indent_apply_type_sel;
    //    退款说明 退款金额
    @BindView(R.id.ll_refund_price)
    LinearLayout ll_refund_price;
    @BindView(R.id.tv_indent_reply_reason_tint)
    TextView tv_indent_reply_reason_tint;
    //    维修地址
    @BindView(R.id.rel_repair_address)
    RelativeLayout rel_repair_address;
    //    地址为空
    @BindView(R.id.ll_indent_address_null)
    LinearLayout ll_indent_address_null;
    //   有默认地址
    @BindView(R.id.ll_indent_address_default)
    LinearLayout ll_indent_address_default;
    //    收件人名字
    @BindView(R.id.tv_consignee_name)
    TextView tv_consignee_name;
    //    收件人手机号码
    @BindView(R.id.tv_consignee_mobile_number)
    TextView tv_address_mobile_number;
    //    订单地址
    @BindView(R.id.tv_indent_details_address)
    TextView tv_indent_details_address;
    //    订单地址
    @BindView(R.id.img_skip_address)
    ImageView img_skip_address;

    //    退款原因
    @BindView(R.id.tv_dir_indent_apply_reason)
    TextView tv_dir_indent_apply_reason;
    //    退款原因选择
    @BindView(R.id.tv_dir_indent_apply_reason_sel)
    TextView tv_dir_indent_apply_reason_sel;
    //    退款金额
    @BindView(R.id.tv_dir_indent_apply_price)
    TextView tv_dir_indent_apply_price;
    //    退款说明
    @BindView(R.id.tv_dir_indent_apply_msg)
    TextView tv_dir_indent_apply_msg;
    //    退款说明输入
    @BindView(R.id.et_dir_indent_apply_explain)
    EditText et_dir_indent_apply_explain;
    //    添加图片
    @BindView(R.id.rel_up_evidence)
    RelativeLayout rel_up_evidence;
    @BindView(R.id.rv_apply_refund_img)
    RecyclerView rv_apply_refund_img;
    //    选择属性
    @BindView(R.id.ll_communal_sel_wheel)
    LinearLayout ll_communal_sel_wheel;
    @BindView(R.id.wv_communal_one)
    WheelView wv_communal_one;
    //    提交按钮
    @BindView(R.id.tv_submit_apply_refund)
    TextView tv_submit_apply_refund;
    @BindView(R.id.sv_layout_refund)
    NestedScrollView sv_layout_refund;
    private List<String> refundReasonList = new ArrayList<>();
    //    退款类型--》原因
    private Map<String, Integer> refundReasonMap = new HashMap<>();
    //    退款类型
    private Map<String, String> refundTypeMap = new HashMap<>();
    private DirectApplyRefundBean refundBean;
    public final String APPLY_TYPE = "服务类型*";
    public final String APPLY_REASON = "退款理由*";
    public final String REPAIR_CONTENT = "问题描述*";
    public final String REFUND_CONTENT = "退款说明";
    private DirectProductListAdapter indentProAdapter;
    private List<DirectRefundProBean> proList = new ArrayList<>();
    //    退款类型
    private List<String> refundTypeList = new ArrayList<>();
    public static final String APPLY_REFUND = "applyRefund";
    //    正则匹配金额加货币
    private String regexNum = "￥+\\d+(\\.\\d+)?";
    private boolean isSelType = false;
    private ImgGridRecyclerAdapter imgGridRecyclerAdapter;
    private ArrayList<String> mSelectPath = new ArrayList();
    private List<ImagePathBean> imagePathBeans = new ArrayList<>();
    //    已上传图片保存
    private List<String> updatedImages = new ArrayList<>();

    private final int REQUEST_PERMISSIONS = 60;
    private final int SEL_ADDRESS_REQ = 56;
    private int maxSelImg = 5;
    private int adapterPosition;
    private RefundApplyBean refundApplyBean;
    private boolean cancelRefund;
    private int addressId;
    private final String REPAIR_TYPE = "3";
    private AlertDialogHelper commitDialogHelper;

    @Override
    protected int getContentView() {
        return R.layout.activity_direct_indent_apply_refund;
    }

    @Override
    protected void initViews() {
        getLoginStatus(this);
        proList.clear();
        header_shared.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && extras.getParcelable("refundPro") != null) {
            refundBean = extras.getParcelable("refundPro");
        }
        cancelRefund = intent.getBooleanExtra("cancelRefund", false);
        tv_dir_indent_apply_reason.setText(APPLY_REASON);
        Link link = new Link("*");
        //        @用户昵称
        link.setTextColor(Color.parseColor("#ff5e5e"));
        link.setUnderlined(false);
        link.setHighlightAlpha(0f);
        LinkBuilder.on(tv_dir_indent_apply_reason)
                .setText(APPLY_REASON)
                .addLink(link)
                .build();
        if (refundBean != null) {
            if (refundBean.getType() == 1 || refundBean.getType() == 3) {
                tv_header_titleAll.setText("申请退款");
                tv_dir_indent_apply_reason_type.setText("退款类型：仅退款");
                tv_dir_indent_apply_type_sel.setVisibility(View.GONE);
                ll_refund_price.setVisibility(VISIBLE);
            } else {
                tv_dir_indent_apply_reason_type.setText(APPLY_TYPE);
                tv_dir_indent_apply_type_sel.setVisibility(View.VISIBLE);
                LinkBuilder.on(tv_dir_indent_apply_reason_type)
                        .setText(APPLY_TYPE)
                        .addLink(link)
                        .build();
                tv_header_titleAll.setText("申请售后");
            }
            proList.addAll(refundBean.getDirectRefundProList());
        }
        communal_recycler_wrap.setNestedScrollingEnabled(false);
        communal_recycler_wrap.setLayoutManager(new LinearLayoutManager(DirectApplyRefundActivity.this));
        indentProAdapter = new DirectProductListAdapter(DirectApplyRefundActivity.this, proList, APPLY_REFUND);
        communal_recycler_wrap.setAdapter(indentProAdapter);
        if (refundBean != null && refundBean.getType() == 3) {
            rel_up_evidence.setVisibility(View.GONE);
        } else {
            rel_up_evidence.setVisibility(View.VISIBLE);
            //        图片
            TinkerBaseApplicationLike app = (TinkerBaseApplicationLike) TinkerManager.getTinkerApplicationLike();
            if (app.getScreenWidth() >= AutoSizeUtils.mm2px(mAppContext, 600)) {
                rv_apply_refund_img.setLayoutManager(new GridLayoutManager(DirectApplyRefundActivity.this, 5));
            } else {
                rv_apply_refund_img.setLayoutManager(new GridLayoutManager(DirectApplyRefundActivity.this, 3));
            }
            if (imagePathBeans.size() < 1) {
                imagePathBeans.add(getImageFormatInstance().getDefaultAddImage());
            }
            rv_apply_refund_img.addItemDecoration(new ItemDecoration.Builder()
                    // 设置分隔线资源ID
                    .setDividerId(R.drawable.item_divider_img_white)






                    .create());
            imgGridRecyclerAdapter = new ImgGridRecyclerAdapter(this, imagePathBeans);
            rv_apply_refund_img.setAdapter(imgGridRecyclerAdapter);
            rv_apply_refund_img.setNestedScrollingEnabled(false);
            imgGridRecyclerAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                if (view.getId() == R.id.delete) {
                    adapterPosition = (int) view.getTag();
                    getImageFormatInstance().delImageBean(imagePathBeans, adapterPosition);
                    mSelectPath.clear();
                    mSelectPath.addAll(getImageFormatInstance().formatStringPathRemoveDefault(imagePathBeans));
                    imgGridRecyclerAdapter.notifyDataSetChanged();
                }
            });
            imgGridRecyclerAdapter.setOnItemClickListener((adapter, view, position) -> {
//                隐藏键盘
                if (((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).isActive()) {
                    CommonUtils.hideSoftInput(DirectApplyRefundActivity.this, et_dir_indent_apply_explain);
                }
                pickImage(position);
            });
        }
        sv_layout_refund.setVisibility(View.GONE);
    }

    @Override
    protected void loadData() {
        getApplyRefund();
    }

    @Override
    protected View getLoadView() {
        return sv_layout_refund;
    }

    @Override
    protected boolean isAddLoad() {
        return true;
    }

    private void getApplyRefund() {
        if(userId<1){
            return;
        }
        String url = Url.BASE_URL + Url.Q_INDENT_APPLY_REFUND;
        Map<String, Object> params = new HashMap<>();
        params.put("no", refundBean.getOrderNo());
        params.put("userId", userId);
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject;
            for (int i = 0; i < refundBean.getDirectRefundProList().size(); i++) {
                DirectRefundProBean directRefundProBean = refundBean.getDirectRefundProList().get(i);
                jsonObject = new JSONObject();
                jsonObject.put("id", directRefundProBean.getId());
                jsonObject.put("orderProductId", directRefundProBean.getOrderProductId());
                jsonObject.put("count", directRefundProBean.getCount());
                jsonArray.put(jsonObject);
            }
            params.put("goods", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("type", refundBean.getType());
        if (refundBean.getType() == 1) {
            params.put("refundPrice", refundBean.getRefundPrice());
        }
        NetLoadUtils.getQyInstance().loadNetDataPost(mAppContext, url, params, new NetLoadUtils.NetLoadListener() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                RefundApplyEntity refundApplyEntity = gson.fromJson(result, RefundApplyEntity.class);
                if (refundApplyEntity != null) {
                    if (refundApplyEntity.getCode().equals(SUCCESS_CODE)) {
                        sv_layout_refund.setVisibility(View.VISIBLE);
                        tv_submit_apply_refund.setVisibility(View.VISIBLE);
                        refundApplyBean = refundApplyEntity.getRefundApplyBean();
                        setRefundApplyData(refundApplyBean);
                    } else if (refundApplyEntity.getCode().equals(EMPTY_CODE)) {
                        showToast(DirectApplyRefundActivity.this, R.string.invalidData);
                    } else {
                        showToast(DirectApplyRefundActivity.this, refundApplyEntity.getMsg());
                    }
                }
                NetLoadUtils.getQyInstance().showLoadSirSuccess(loadService);
            }

            @Override
            public void netClose() {
                sv_layout_refund.setVisibility(View.GONE);
                showToast(DirectApplyRefundActivity.this, R.string.invalidData);
                NetLoadUtils.getQyInstance().showLoadSirSuccess(loadService);
            }

            @Override
            public void onError(Throwable throwable) {
                sv_layout_refund.setVisibility(View.GONE);
                showToast(DirectApplyRefundActivity.this, R.string.unConnectedNetwork);
                NetLoadUtils.getQyInstance().showLoadSirSuccess(loadService);
            }
        });
    }

    private void setRefundApplyData(RefundApplyBean refundApplyBean) {
//        退款金额说明
        String priceName;
        if (refundApplyBean.getRefundIntegralPrice() > 0) {
            float moneyPrice = getFloatNumber(refundApplyBean.getRefundPrice());
            if (moneyPrice > 0) {
                priceName = String.format(getResources().getString(R.string.integral_product_and_price)
                        , refundApplyBean.getRefundIntegralPrice(), getStrings(refundApplyBean.getRefundPrice()));
            } else {
                priceName = String.format(getResources().getString(R.string.integral_indent_product_price)
                        , refundApplyBean.getRefundIntegralPrice());
            }
        } else {
            priceName = getStringsChNPrice(DirectApplyRefundActivity.this, refundApplyBean.getRefundPrice());
        }
        if (!TextUtils.isEmpty(refundApplyBean.getRefundMsg())) {
            tv_dir_indent_apply_msg.setVisibility(VISIBLE);
            String refundMsg = refundApplyBean.getRefundMsg();
            tv_dir_indent_apply_msg.setText(getStrings(refundMsg));
            Link link = new Link(Pattern.compile(regexNum));
            link.setTextColor(0xffff5e6b);
            link.setUnderlined(false);
            link.setHighlightAlpha(0f);
            LinkBuilder.on(tv_dir_indent_apply_msg)
                    .setText(refundMsg)
                    .addLink(link)
                    .build();
        } else {
            tv_dir_indent_apply_msg.setVisibility(View.GONE);
            //        退款金额
        }
        tv_dir_indent_apply_price.setText(("退款金额：" + priceName));

        //使用迭代器，获取key
        if (refundApplyBean.getRefundType() != null) {
            refundTypeList.clear();
            Iterator<Map.Entry<String, String>> iterator = refundApplyBean.getRefundType().entrySet().iterator();
            refundTypeMap.clear();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                refundTypeList.add(entry.getValue());
                refundTypeMap.put(entry.getValue(), entry.getKey());
            }
            if (0 < refundTypeList.size() && refundTypeList.size() < 2) {
                tv_dir_indent_apply_type_sel.setText(refundTypeList.get(refundTypeList.size() - 1));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            if (requestCode == IS_LOGIN_CODE) {
                finish();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IS_LOGIN_CODE) {
            loadData();
        } else if (requestCode == PictureConfigC.CHOOSE_REQUEST) {
            List<LocalMediaC> localMediaList = PictureSelector.obtainMultipleResult(data);
            if (localMediaList != null && localMediaList.size() > 0) {
                imagePathBeans.clear();
                for (LocalMediaC localMedia : localMediaList) {
                    if (!TextUtils.isEmpty(localMedia.getPath())) {
                        imagePathBeans.add(new ImagePathBean(localMedia.getPath(), true));
                    }
                }
                if (imagePathBeans.size() < maxSelImg) {
                    imagePathBeans.add(getImageFormatInstance().getDefaultAddImage());
                }
                mSelectPath.clear();
                mSelectPath.addAll(getImageFormatInstance().formatStringPathRemoveDefault(imagePathBeans));
                imgGridRecyclerAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == SEL_ADDRESS_REQ) {
            //            获取地址成功
            addressId = data.getIntExtra("addressId", 0);
            getAddressDetails();
        } else if (requestCode == REQUEST_PERMISSIONS) {
            showToast(this, "请到应用管理授予权限");
        }
    }

    private void pickImage(final int position) {
        ConstantMethod constantMethod = new ConstantMethod();
        constantMethod.setOnGetPermissionsSuccess(() -> {
            int imgLength = imagePathBeans.size() - 1;
            if (position == imgLength && DEFAULT_ADD_IMG.equals(imagePathBeans.get(imgLength).getPath())) {
                PictureSelectorUtils.getInstance()
                        .resetVariable()
                        .isCrop(false)
                        .selImageList(mSelectPath)
                        .selMaxNum(maxSelImg)
                        .imageMode(PictureConfigC.MULTIPLE)
                        .isShowGif(true)
                        .openGallery(DirectApplyRefundActivity.this);
            } else {
                Intent intent = new Intent(DirectApplyRefundActivity.this, ImagePagerActivity.class);
                intent.putExtra(ImagePagerActivity.INTENT_POSITION, position);
                List<ImageBean> imageBeanList = new ArrayList<>();
                ImageBean imageBean;
                for (String imgUrl : mSelectPath) {
                    imageBean = new ImageBean();
                    imageBean.setPicUrl(imgUrl);
                    imageBeanList.add(imageBean);
                }
                intent.putParcelableArrayListExtra(ImagePagerActivity.INTENT_IMGURLS, (ArrayList<? extends Parcelable>) imageBeanList);
                startActivity(intent);
            }
        });
        constantMethod.getPermissions(this, Permission.Group.STORAGE);
    }

    /**
     * 获取默认地址
     */
    private void getDefaultAddress() {
        String url = Url.BASE_URL + Url.DELIVERY_ADDRESS + userId;
        XUtil.Get(url, null, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                AddressInfoEntity addressInfoEntity = gson.fromJson(result, AddressInfoEntity.class);
                if (addressInfoEntity != null) {
                    if (addressInfoEntity.getCode().equals(SUCCESS_CODE)) {
                        setAddressData(addressInfoEntity.getAddressInfoBean());
                    } else if (addressInfoEntity.getCode().equals(EMPTY_CODE)) {
                        setAddressData(null);
                    } else {
                        showToast(DirectApplyRefundActivity.this, addressInfoEntity.getMsg());
                    }
                }
            }
        });
    }

    /**
     * 获取地址详情
     */
    private void getAddressDetails() {
        //地址详情内容
        String url = Url.BASE_URL + Url.ADDRESS_DETAILS + addressId;
        XUtil.Get(url, null, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                AddressInfoEntity addressInfoEntity = gson.fromJson(result, AddressInfoEntity.class);
                if (addressInfoEntity != null) {
                    if (addressInfoEntity.getCode().equals(SUCCESS_CODE)) {
                        setAddressData(addressInfoEntity.getAddressInfoBean());
                    } else if (addressInfoEntity.getCode().equals(EMPTY_CODE)) {
                        setAddressData(null);
                    } else {
                        showToast(DirectApplyRefundActivity.this, addressInfoEntity.getMsg());
                    }
                }
            }
        });
    }

    private void setAddressData(AddressInfoEntity.AddressInfoBean addressInfoBean) {
        rel_repair_address.setVisibility(VISIBLE);
        if (addressInfoBean != null) {
            addressId = addressInfoBean.getId();
            ll_indent_address_default.setVisibility(VISIBLE);
            ll_indent_address_null.setVisibility(View.GONE);
            tv_consignee_name.setText(addressInfoBean.getConsignee());
            tv_address_mobile_number.setText(addressInfoBean.getMobile());
            tv_indent_details_address.setText((addressInfoBean.getAddress_com() + addressInfoBean.getAddress() + " "));
        } else {
            addressId = 0;
            ll_indent_address_default.setVisibility(View.GONE);
            ll_indent_address_null.setVisibility(VISIBLE);
        }
    }

    //    地址列表为空 跳转新建地址
    @OnClick(R.id.tv_lv_top)
    void skipNewAddress(View view) {
        Intent intent = new Intent(DirectApplyRefundActivity.this, SelectedAddressActivity.class);
        intent.putExtra("hasDefaultAddress", false);
        startActivityForResult(intent, SEL_ADDRESS_REQ);
    }

    //  更换地址  跳转地址列表
    @OnClick({R.id.ll_indent_address_default})
    void skipAddressList(View view) {
        Intent intent = new Intent(DirectApplyRefundActivity.this, SelectedAddressActivity.class);
        intent.putExtra("addressId", addressId);
        intent.putExtra("hasDefaultAddress", true);
        startActivityForResult(intent, SEL_ADDRESS_REQ);
    }

    /**
     * 点击提交 条件判断
     *
     * @param view
     */
    @OnClick(R.id.tv_submit_apply_refund)
    void submitApplications(View view) {
//        退款金额
//        String price = et_dir_indent_apply_price.getText().toString().trim();
//        退款原因
        String reason = tv_dir_indent_apply_reason_sel.getText().toString().trim();
//        退款说明
        String content = et_dir_indent_apply_explain.getText().toString().trim();
//        退款类型
        String refundType = tv_dir_indent_apply_type_sel.getText().toString().trim();

        if (refundBean != null) {
            if (refundBean.getType() == 1 || refundBean.getType() == 3) {
                if (!TextUtils.isEmpty(reason)) {
                    refundBean.setContent(getStrings(content));
                    refundBean.setReason(reason);
                    refundBean.setRefundType(refundTypeMap.get(refundType));
                    refundBean.setRefundReasonId(refundReasonMap.get(reason));
                    submit(refundBean);
                } else if (TextUtils.isEmpty(reason)) {
                    showToast(this, R.string.refund_reason);
                }
            } else if (refundBean.getType() == 2) {
                if (!TextUtils.isEmpty(refundType)) {
                    if (!TextUtils.isEmpty(refundTypeMap.get(refundType))) {
                        String type = refundTypeMap.get(refundType);
                        if (type.equals(REPAIR_TYPE)) {
                            if (addressId != 0 && !TextUtils.isEmpty(content)) {
                                refundBean.setRefundType(refundTypeMap.get(refundType));
                                refundBean.setContent(getStrings(content));
                                submit(refundBean);
                            } else if (addressId < 1) {
                                showToast(this, R.string.refund_address_sel);
                            } else {
                                showToast(this, R.string.refund_repair_content);
                            }
                        } else {
                            if (!TextUtils.isEmpty(reason)) {
                                refundBean.setRefundType(refundTypeMap.get(refundType));
                                refundBean.setContent(getStrings(content));
                                refundBean.setRefundReasonId(refundReasonMap.get(reason));
                                refundBean.setReason(reason);
                                submit(refundBean);
                            } else {
                                showToast(this, R.string.refund_reason);
                            }
                        }
                    } else {
                        showToast(this, R.string.refund_type_sel_errror);
                    }
                } else {
                    showToast(this, R.string.refund_type);
                }
            }
        }

    }

    /**
     * 申诉信息 图片上传
     *
     * @param refundBean
     */
    private void submit(final DirectApplyRefundBean refundBean) {
        loadHud.show();
        if (mSelectPath.size() < 1) {
            submitRefundData(refundBean);
        } else {
            if (updatedImages.size() > 0) {
                setRefundImageData(updatedImages, refundBean);
            } else {
                ImgUrlHelp imgUrlHelp = new ImgUrlHelp();
                imgUrlHelp.setUrl(DirectApplyRefundActivity.this, mSelectPath);
                imgUrlHelp.setOnFinishListener(new ImgUrlHelp.OnFinishDataListener() {
                    @Override
                    public void finishData(@NonNull List<String> data, Handler handler) {
                        updatedImages.clear();
                        updatedImages.addAll(data);
                        //                            已上传不可删除 不可更换图片
                        getImageFormatInstance().submitChangeIconStatus(imagePathBeans, false);
                        ;
                        imgGridRecyclerAdapter.notifyDataSetChanged();
                        setRefundImageData(data, refundBean);
                        submitRefundData(refundBean);
                        handler.removeCallbacksAndMessages(null);
                    }

                    @Override
                    public void finishError(String error) {
                        if (loadHud != null) {
                            loadHud.dismiss();
                        }
                        showToast(DirectApplyRefundActivity.this, "网络异常");
                    }

                    @Override
                    public void finishSingleImg(String singleImg, Handler handler) {
                    }
                });
            }
        }
    }

    private void setRefundImageData(@NonNull List<String> data, DirectApplyRefundBean refundBean) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < data.size(); i++) {
            if (i == 0) {
                stringBuffer.append(data.get(i));
            } else {
                stringBuffer.append("," + data.get(i));
            }
        }
        refundBean.setImages(stringBuffer.toString());
    }

    /**
     * 提交申诉信息
     *
     * @param refundBean
     */
    private void submitRefundData(final DirectApplyRefundBean refundBean) {
        Map<String, Object> params = new HashMap<>();
        final DirectRefundProBean directRefundProBean = refundBean.getDirectRefundProList().get(0);
        if (refundBean.getType() == 3) {
            cancelIndent(refundBean, params);
        } else {
            if (!TextUtils.isEmpty(refundBean.getRefundType())
                    && refundBean.getRefundType().equals("3")) {
                refundRepair(refundBean, params, directRefundProBean);
            } else {
                refundPrice(refundBean, params, directRefundProBean);
            }
        }
    }

    /**
     * 售后维修
     *
     * @param refundBean
     * @param params
     * @param directRefundProBean
     */
    private void refundRepair(final DirectApplyRefundBean refundBean, Map<String, Object> params
            , final DirectRefundProBean directRefundProBean) {
        String url = Url.BASE_URL + Url.Q_INDENT_REFUND_REPAIR_SUB;
        params.put("no", refundBean.getOrderNo());
        params.put("userId", userId);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", directRefundProBean.getId());
            jsonObject.put("orderProductId", directRefundProBean.getOrderProductId());
            jsonObject.put("count", directRefundProBean.getCount());
            if (!TextUtils.isEmpty(refundBean.getImages())) {
                jsonObject.put("images", refundBean.getImages());
            }
            jsonObject.put("content", refundBean.getContent());
            jsonObject.put("refundType", 3);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            params.put("goods", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("userAddressId", addressId);
        XUtil.Post(url, params, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                Gson gson = new Gson();
                RequestStatus requestInfo = gson.fromJson(result, RequestStatus.class);
                if (requestInfo != null) {
                    if (requestInfo.getCode().equals(SUCCESS_CODE)) {
                        showToast(DirectApplyRefundActivity.this, "提交完成");
                        Intent intent = new Intent(DirectApplyRefundActivity.this, DoMoRefundDetailActivity.class);
                        intent.putExtra("orderProductId", String.valueOf(directRefundProBean.getOrderProductId()));
                        intent.putExtra("no", String.valueOf(refundBean.getOrderNo()));
                        intent.putExtra("orderRefundProductId", String.valueOf(requestInfo.getOrderRefundProductId()));
                        intent.putExtra(REFUND_TYPE, REFUND_REPAIR);
                        startActivity(intent);
                        finish();
                    } else {
                        showToast(DirectApplyRefundActivity.this, requestInfo.getResult() != null ?
                                requestInfo.getResult().getMsg() : requestInfo.getMsg());
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                showToast(DirectApplyRefundActivity.this, ex.getMessage() + "");
            }
        });
    }

    /**
     * 退货退款 单个商品退款
     *
     * @param refundBean
     * @param params
     * @param directRefundProBean
     */
    private void refundPrice(final DirectApplyRefundBean refundBean, Map<String, Object> params, final DirectRefundProBean directRefundProBean) {
        String url;
        if (!cancelRefund) {
            url = Url.BASE_URL + Url.Q_INDENT_APPLY_REFUND_SUB;
            params.put("no", refundBean.getOrderNo());
            params.put("userId", userId);
            params.put("version", 3);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", directRefundProBean.getId());
                jsonObject.put("orderProductId", directRefundProBean.getOrderProductId());
                jsonObject.put("count", directRefundProBean.getCount());
                if (!TextUtils.isEmpty(refundBean.getImages())) {
                    jsonObject.put("images", refundBean.getImages());
                }
                jsonObject.put("reason", refundBean.getReason());
                jsonObject.put("refundReasonId", refundBean.getRefundReasonId());
                if (!TextUtils.isEmpty(refundBean.getReason())) {
                    jsonObject.put("content", refundBean.getContent());
                }
                jsonObject.put("refundType", refundBean.getRefundType());
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                params.put("goods", jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            params.put("type", refundBean.getType());
        } else {
            url = Url.BASE_URL + Url.Q_INDENT_CHANGE_REFUND_SUB;
            params.put("orderRefundProductId", refundBean.getOrderRefundProductId());
            if (!TextUtils.isEmpty(refundBean.getImages())) {
                params.put("images", refundBean.getImages());
            }
            params.put("reason", refundBean.getReason());
            params.put("refundReasonId", refundBean.getRefundReasonId());
            if (!TextUtils.isEmpty(refundBean.getReason())) {
                params.put("content", refundBean.getContent());
            }
            if (refundBean.getRefundReasonId() > 0) {
                params.put("refundReasonId", refundBean.getRefundReasonId());
            }
            params.put("refundType", refundBean.getRefundType());
        }
        XUtil.Post(url, params, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                Gson gson = new Gson();
                RequestStatus requestInfo = gson.fromJson(result, RequestStatus.class);
                if (requestInfo != null) {
                    if (requestInfo.getCode().equals(SUCCESS_CODE)) {
                        finish();
                    } else {
                        showToast(DirectApplyRefundActivity.this, requestInfo.getResult() != null ?
                                requestInfo.getResult().getMsg() : requestInfo.getMsg());
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                showToast(DirectApplyRefundActivity.this, ex.getMessage() + "");
            }
        });
    }

    /**
     * 取消订单
     *
     * @param refundBean
     * @param params
     */
    private void cancelIndent(DirectApplyRefundBean refundBean, Map<String, Object> params) {
        String url = Url.BASE_URL + Url.Q_CANCEL_INDENT_REFUND;
        params.put("no", refundBean.getOrderNo());
        params.put("userId", userId);
        if (!TextUtils.isEmpty(refundBean.getContent())) {
            params.put("msg", refundBean.getContent());
        }
        params.put("reason", refundBean.getReason());
        XUtil.Post(url, params, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                Gson gson = new Gson();
                RequestStatus indentInfo = gson.fromJson(result, RequestStatus.class);
                if (indentInfo != null) {
                    if (indentInfo.getCode().equals(SUCCESS_CODE)) {
                        if (commitDialogHelper == null) {
                            commitDialogHelper = new AlertDialogHelper(DirectApplyRefundActivity.this);
                            commitDialogHelper.setTitle("操作提示").setTitleGravity(Gravity.CENTER).setMsgTextGravity(Gravity.CENTER)
                                    .setMsg(getString(R.string.Submit_Success)).setConfirmText("确定").setSingleButton(true)
                                    .setCancelable(false)
                                    .setCancelTextColor(getResources().getColor(R.color.text_login_gray_s));
                            commitDialogHelper.setAlertListener(new AlertDialogHelper.AlertConfirmCancelListener() {
                                @Override
                                public void confirm() {
                                    finish();
                                }

                                @Override
                                public void cancel() {
                                }
                            });
                        }
                        commitDialogHelper.show();
                    } else {
                        showToast(DirectApplyRefundActivity.this, indentInfo.getResult() != null ?
                                indentInfo.getResult().getMsg() : indentInfo.getMsg());
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (loadHud != null) {
                    loadHud.dismiss();
                }
                showToast(DirectApplyRefundActivity.this, R.string.Submit_Failed);
                super.onError(ex, isOnCallback);
            }
        });
    }

    //  退款原因选择
    @OnClick(R.id.tv_dir_indent_apply_reason_sel)
    void selRefundReason(View view) {
        String refundType = tv_dir_indent_apply_type_sel.getText().toString();
        if (!TextUtils.isEmpty(refundType)) {
            if (ll_communal_sel_wheel.getVisibility() == View.GONE) {
                isSelType = false;
                if (refundBean.getType() == 2) {
                    if (!TextUtils.isEmpty(refundType)) {
                        if (refundTypeMap.get(refundType).equals("1")) {
                            refundReasonList.clear();
                            refundReasonMap.clear();
                            for (int i = 0; i < refundApplyBean.getMoneyRefundReason().size(); i++) {
                                MoneyRefundReasonBean moneyRefundReasonBean = refundApplyBean.getMoneyRefundReason().get(i);
                                refundReasonList.add(moneyRefundReasonBean.getReason());
                                refundReasonMap.put(moneyRefundReasonBean.getReason(), moneyRefundReasonBean.getId());
                            }
                        } else {
                            refundReasonList.clear();
                            refundReasonMap.clear();
                            for (int i = 0; i < refundApplyBean.getMoneyAndGoodsRefundReason().size(); i++) {
                                MoneyAndGoodsRefundReasonBean moneyAndGoodsBean = refundApplyBean.getMoneyAndGoodsRefundReason().get(i);
                                refundReasonList.add(moneyAndGoodsBean.getReason());
                                refundReasonMap.put(moneyAndGoodsBean.getReason(), moneyAndGoodsBean.getId());
                            }
                        }
                        wv_communal_one.setViewAdapter(new ArrayWheelAdapter<>(DirectApplyRefundActivity.this, refundReasonList.toArray()));
                        wv_communal_one.setVisibleItems(5);
                        wv_communal_one.setCurrentItem(0);
                        tv_dir_indent_apply_reason_sel.setSelected(true);
                        ll_communal_sel_wheel.setVisibility(View.VISIBLE);
                        tv_submit_apply_refund.setVisibility(View.GONE);
                    } else {
                        showToast(this, R.string.refund_type);
                    }
                } else {
                    refundReasonList.clear();
                    refundReasonMap.clear();
                    for (int i = 0; i < refundApplyBean.getWaitDeliveryRefundReason().size(); i++) {
                        WaitDeliveryRefundReasonBean waitDeliveryBean = refundApplyBean.getWaitDeliveryRefundReason().get(i);
                        refundReasonList.add(waitDeliveryBean.getReason());
                        refundReasonMap.put(waitDeliveryBean.getReason(), waitDeliveryBean.getId());
                    }
                    wv_communal_one.setViewAdapter(new ArrayWheelAdapter<>(DirectApplyRefundActivity.this, refundReasonList.toArray()));
                    wv_communal_one.setVisibleItems(5);
                    wv_communal_one.setCurrentItem(0);
                    tv_dir_indent_apply_reason_sel.setSelected(true);
                    ll_communal_sel_wheel.setVisibility(View.VISIBLE);
                    tv_submit_apply_refund.setVisibility(View.GONE);
                }
            } else {
                tv_dir_indent_apply_reason_sel.setSelected(false);
                ll_communal_sel_wheel.setVisibility(View.GONE);
                tv_submit_apply_refund.setVisibility(View.VISIBLE);
            }
        } else {
            showToast(this, R.string.refund_type);
        }
    }

    //    退款类型选择
    @OnClick(R.id.tv_dir_indent_apply_type_sel)
    void selRefundType(View view) {
        if (ll_communal_sel_wheel.getVisibility() == View.GONE) {
            isSelType = true;
            wv_communal_one.setViewAdapter(new ArrayWheelAdapter<>(DirectApplyRefundActivity.this, refundTypeList.toArray()));
            wv_communal_one.setVisibleItems(5);
            wv_communal_one.setCurrentItem(0);
            tv_dir_indent_apply_reason_sel.setSelected(true);
            ll_communal_sel_wheel.setVisibility(View.VISIBLE);
            tv_submit_apply_refund.setVisibility(View.GONE);
        } else {
            tv_dir_indent_apply_reason_sel.setSelected(false);
            ll_communal_sel_wheel.setVisibility(View.GONE);
            tv_submit_apply_refund.setVisibility(View.VISIBLE);
        }
    }

    //    取消 确认 选择
    @OnClick({R.id.tv_one_click_cancel, R.id.tv_one_click_confirmed})
    void selLogisticCancelConfirm(View view) {
        if (view.getId() == R.id.tv_one_click_confirmed) {
//            选择服务类型
            if (isSelType) {
                String refundTypeText = getStrings(refundTypeList.get(wv_communal_one.getCurrentItem()));
                if (refundTypeMap != null
                        && !TextUtils.isEmpty(refundTypeMap.get(refundTypeText))) {
                    String refundType = refundTypeMap.get(refundTypeText);
                    switch (refundType) {
                        case "3":
//                            售后维修
                            Link link = new Link("*");
                            //        @用户昵称
                            link.setTextColor(Color.parseColor("#ff5e5e"));
                            link.setUnderlined(false);
                            link.setHighlightAlpha(0f);
                            LinkBuilder.on(tv_indent_reply_reason_tint)
                                    .setText(REPAIR_CONTENT)
                                    .addLink(link)
                                    .build();
                            ll_refund_price.setVisibility(View.GONE);
                            getDefaultAddress();
                            break;
                        default:
//                            退款 退货退款
                            tv_indent_reply_reason_tint.setText(REFUND_CONTENT);
                            ll_refund_price.setVisibility(View.VISIBLE);
                            rel_repair_address.setVisibility(View.GONE);
                            break;
                    }
                    tv_dir_indent_apply_type_sel.setText(refundTypeText);
                } else {
                    showToast(DirectApplyRefundActivity.this, R.string.refund_type_sel_errror);
                }
            } else {
                tv_dir_indent_apply_reason_sel.setText(getStrings(refundReasonList.get(wv_communal_one.getCurrentItem())));
            }
        }
        isSelType = false;
        ll_communal_sel_wheel.setVisibility(View.GONE);
        tv_submit_apply_refund.setVisibility(View.VISIBLE);
        tv_dir_indent_apply_reason_sel.setSelected(false);
    }

    @OnClick(R.id.tv_life_back)
    void goBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(commitDialogHelper!=null&&commitDialogHelper.isShowing()){
            commitDialogHelper.dismiss();
        }
    }
}
