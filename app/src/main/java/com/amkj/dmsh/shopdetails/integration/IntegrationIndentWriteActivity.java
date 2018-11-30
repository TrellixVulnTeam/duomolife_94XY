package com.amkj.dmsh.shopdetails.integration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.amkj.dmsh.R;
import com.amkj.dmsh.address.activity.SelectedAddressActivity;
import com.amkj.dmsh.address.bean.AddressInfoEntity;
import com.amkj.dmsh.base.BaseActivity;
import com.amkj.dmsh.constant.Url;
import com.amkj.dmsh.constant.XUtil;
import com.amkj.dmsh.shopdetails.activity.DirectPaySuccessActivity;
import com.amkj.dmsh.shopdetails.adapter.IndentDiscountAdapter;
import com.amkj.dmsh.shopdetails.alipay.AliPay;
import com.amkj.dmsh.shopdetails.bean.IndentDiscountsEntity.IndentDiscountsBean.PriceInfoBean;
import com.amkj.dmsh.shopdetails.bean.QualityCreateAliPayIndentBean;
import com.amkj.dmsh.shopdetails.bean.QualityCreateWeChatPayIndentBean;
import com.amkj.dmsh.shopdetails.bean.QualityCreateWeChatPayIndentBean.ResultBean.PayKeyBean;
import com.amkj.dmsh.shopdetails.bean.ShopCarGoodsSku;
import com.amkj.dmsh.shopdetails.integration.bean.CreateIntegralIndentInfo;
import com.amkj.dmsh.shopdetails.integration.bean.CreateIntegralIndentInfo.IntegrationIndentBean;
import com.amkj.dmsh.shopdetails.integration.bean.IntegralSettlementEntity;
import com.amkj.dmsh.shopdetails.integration.bean.IntegralSettlementEntity.IntegralSettlementBean;
import com.amkj.dmsh.shopdetails.integration.bean.IntegralSettlementEntity.IntegralSettlementBean.ProductInfoBean;
import com.amkj.dmsh.shopdetails.weixin.WXPay;
import com.amkj.dmsh.utils.NetWorkUtils;
import com.amkj.dmsh.utils.glide.GlideImageLoaderUtil;
import com.amkj.dmsh.utils.inteface.MyCallBack;
import com.google.gson.Gson;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.AutoSizeUtils;

import static android.view.View.VISIBLE;
import static com.amkj.dmsh.base.TinkerBaseApplicationLike.mAppContext;
import static com.amkj.dmsh.constant.ConstantMethod.getLoginStatus;
import static com.amkj.dmsh.constant.ConstantMethod.getStringFilter;
import static com.amkj.dmsh.constant.ConstantMethod.getStrings;
import static com.amkj.dmsh.constant.ConstantMethod.showToast;
import static com.amkj.dmsh.constant.ConstantMethod.userId;
import static com.amkj.dmsh.constant.ConstantVariable.EMPTY_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.INDENT_INTEGRAL_PRODUCT;
import static com.amkj.dmsh.constant.ConstantVariable.INDENT_PRODUCT_TYPE;
import static com.amkj.dmsh.constant.ConstantVariable.IS_LOGIN_CODE;
import static com.amkj.dmsh.constant.ConstantVariable.PAY_ALI_PAY;
import static com.amkj.dmsh.constant.ConstantVariable.PAY_WX_PAY;
import static com.amkj.dmsh.constant.ConstantVariable.REGEX_NUM;
import static com.amkj.dmsh.constant.ConstantVariable.SUCCESS_CODE;

/**
 * @author Liuguipeng
 * @des 积分订单填写
 */
public class IntegrationIndentWriteActivity extends BaseActivity {
    @BindView(R.id.tv_header_title)
    TextView tvHeaderTitle;
    @BindView(R.id.tv_header_shared)
    TextView tvHeaderShared;
    @BindView(R.id.tl_normal_bar)
    Toolbar tlNormalBar;
    @BindView(R.id.ll_indent_address_empty_default)
    LinearLayout ll_indent_address_empty_default;
    //    进关信息提示
    @BindView(R.id.tv_oversea_buy_tint)
    TextView tvOverseaBuyTint;
    @BindView(R.id.ll_indent_address_null)
    LinearLayout llIndentAddressNull;

    @BindView(R.id.tv_consignee_name)
    TextView tvConsigneeName;
    @BindView(R.id.tv_consignee_mobile_number)
    TextView tvConsigneeMobileNumber;
    @BindView(R.id.tv_indent_details_address)
    TextView tvIndentDetailsAddress;
    @BindView(R.id.ll_indent_address_default)
    LinearLayout llIndentAddressDefault;
    //进关信息
    @BindView(R.id.ll_oversea_info)
    LinearLayout llOverseaInfo;

    @BindView(R.id.iv_integral_product_image)
    ImageView ivIntegralProductImage;
    @BindView(R.id.tv_integral_product_name)
    TextView tvIntegralProductName;
    @BindView(R.id.tv_integral_sku_value)
    TextView tvIntegralSkuValue;
    @BindView(R.id.tv_integral_product_count)
    TextView tv_integral_product_count;
    @BindView(R.id.tv_integral_product_price)
    TextView tvIntegralProductPrice;
    @BindView(R.id.edt_integral_product_note)
    EditText edtIntegralProductNote;
    //    订单结算信息
    @BindView(R.id.rv_integral_write_info)
    RecyclerView rvIntegralWriteInfo;

    @BindView(R.id.ll_integral_pay_way)
    LinearLayout ll_integral_pay_way;
    @BindView(R.id.rb_checked_alipay)
    RadioButton rbCheckedAlipay;
    @BindView(R.id.rb_checked_wechat_pay)
    RadioButton rbCheckedWechatPay;

    @BindView(R.id.tv_integral_details_price)
    TextView tvIntegralDetailsPrice;
    @BindView(R.id.tv_integral_details_create_int)
    TextView tvIntegralDetailsCreateInt;
    private final int NEW_CREATE_ADDRESS_REQ = 101;
    private final int SEL_ADDRESS_REQ = 102;
    private int addressId;
    private boolean isFirst = true;
    private ShopCarGoodsSku shopCarGoodsSku;
    //    订单价格优惠列表
    private List<PriceInfoBean> priceInfoList = new ArrayList<>();
    private IndentDiscountAdapter indentDiscountAdapter;
    private IntegralSettlementBean integralSettlementBean;
    private String payType;
    private String orderCreateNo;

    @Override
    protected int getContentView() {
        return R.layout.activity_indent_write_integration;
    }

    @Override
    protected void initViews() {
        getLoginStatus(this);
        tlNormalBar.setSelected(true);
        tvHeaderTitle.setText("确认订单");
        tvHeaderShared.setVisibility(View.GONE);
        tvOverseaBuyTint.setVisibility(View.GONE);
        llOverseaInfo.setVisibility(View.GONE);
        rvIntegralWriteInfo.setVisibility(View.GONE);
        ll_integral_pay_way.setVisibility(View.GONE);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            shopCarGoodsSku = bundle.getParcelable("integralProduct");
        }
        if (shopCarGoodsSku == null) {
            showToast(this, "数据缺失，请刷新重试");
            finish();
            return;
        }
        setIntegralData();
        rvIntegralWriteInfo.setLayoutManager(new LinearLayoutManager(this));
        indentDiscountAdapter = new IndentDiscountAdapter(priceInfoList);
        rvIntegralWriteInfo.setAdapter(indentDiscountAdapter);
    }

    @Override
    protected void loadData() {
        if(shopCarGoodsSku.getIntegralProductType()==1){
            ll_indent_address_empty_default.setVisibility(View.GONE);
            getProductSettlementInfo();
        }else{
            if (isFirst) {
                getDefaultAddress();
            } else {
                getAddressDetails();
            }
        }
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
                        showToast(IntegrationIndentWriteActivity.this, addressInfoEntity.getMsg());
                    }
                }
            }
        });
    }

    private void setAddressData(AddressInfoEntity.AddressInfoBean addressInfoBean) {
        if (addressInfoBean != null) {
            addressId = addressInfoBean.getId();
            llIndentAddressDefault.setVisibility(VISIBLE);
            llIndentAddressNull.setVisibility(View.GONE);
            tvConsigneeName.setText(addressInfoBean.getConsignee());
            tvConsigneeMobileNumber.setText(addressInfoBean.getMobile());
            tvIndentDetailsAddress.setText((addressInfoBean.getAddress_com() + addressInfoBean.getAddress() + " "));
            getProductSettlementInfo();
        } else {
            llIndentAddressDefault.setVisibility(View.GONE);
            llIndentAddressNull.setVisibility(VISIBLE);
        }
    }

    /**
     * 获取结算信息
     */
    private void getProductSettlementInfo() {
        if (NetWorkUtils.checkNet(IntegrationIndentWriteActivity.this)) {
            String url = Url.BASE_URL + Url.INTEGRAL_DIRECT_SETTLEMENT;
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            if (addressId > 0) {
                params.put("addressId", addressId);
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("saleSkuId", shopCarGoodsSku.getSaleSkuId());
                jsonObject.put("id", shopCarGoodsSku.getProductId());
                jsonObject.put("count", shopCarGoodsSku.getCount());
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                params.put("goods", jsonArray.toString().trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            XUtil.Post(url, params, new MyCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    Gson gson = new Gson();
                    IntegralSettlementEntity settlementEntity = gson.fromJson(result, IntegralSettlementEntity.class);
                    if (settlementEntity != null) {
                        if (settlementEntity.getCode().equals(SUCCESS_CODE)) {
                            integralSettlementBean = settlementEntity.getIntegralSettlementBean();
                            setIntegralSettlementInfo(integralSettlementBean);
                        }
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    showToast(IntegrationIndentWriteActivity.this, R.string.connectedFaile);
                    super.onError(ex, isOnCallback);
                }
            });
        } else {
            showToast(IntegrationIndentWriteActivity.this, R.string.unConnectedNetwork);
        }
    }

    /**
     * 设置结算信息
     *
     * @param integralSettlementBean 结算数据
     */
    private void setIntegralSettlementInfo(IntegralSettlementBean integralSettlementBean) {
        List<PriceInfoBean> indentDiscountsList = integralSettlementBean.getPriceInfo();
        if (indentDiscountsList != null) {
            rvIntegralWriteInfo.setVisibility(VISIBLE);
            priceInfoList.clear();
            priceInfoList.addAll(indentDiscountsList);
            PriceInfoBean priceInfoBean = priceInfoList.get(priceInfoList.size() - 1);
            tvIntegralDetailsPrice.setText(getStrings(priceInfoBean.getTotalPriceName()));
            priceInfoList.remove(priceInfoList.get(priceInfoList.size() - 1));
            indentDiscountAdapter.setNewData(priceInfoList);
        } else {
            rvIntegralWriteInfo.setVisibility(View.GONE);
        }
        if (integralSettlementBean.getTotalPrice() > 0) {
            ll_integral_pay_way.setVisibility(VISIBLE);
        } else {
            ll_integral_pay_way.setVisibility(View.GONE);
        }
    }

    /**
     * 设置积分商品信息
     */
    private void setIntegralData() {
        if (shopCarGoodsSku != null) {
            GlideImageLoaderUtil.loadCenterCrop(this, ivIntegralProductImage, shopCarGoodsSku.getPicUrl());
            tvIntegralProductName.setText(getStringFilter(shopCarGoodsSku.getIntegralName()));
            tvIntegralSkuValue.setText(getStringFilter(shopCarGoodsSku.getValuesName()));
            tv_integral_product_count.setText(String.format(getResources().getString(R.string.integral_lottery_award_count), shopCarGoodsSku.getCount()));
            String priceName;
            if(shopCarGoodsSku.getIntegralType() ==0){
                priceName = String.format(getResources().getString(R.string.integral_indent_product_price),(int)shopCarGoodsSku.getPrice());
            }else{
                priceName = String.format(getResources().getString(R.string.integral_product_and_price)
                        ,(int)shopCarGoodsSku.getPrice(),getStrings(shopCarGoodsSku.getMoneyPrice()));
            }
            tvIntegralProductPrice.setText(priceName);
            Pattern p = Pattern.compile(REGEX_NUM);
            Link redNum = new Link(p);
            //        @用户昵称
            redNum.setTextColor(getResources().getColor(R.color.text_normal_red));
            redNum.setUnderlined(false);
            redNum.setTextSize(AutoSizeUtils.mm2px(mAppContext,32));
            redNum.setHighlightAlpha(0f);
            LinkBuilder.on(tvIntegralProductPrice)
                    .addLink(redNum)
                    .build();
        }
    }

    @OnClick(R.id.tv_life_back)
    void goBack() {
        finish();
    }


    //  确定兑换
    @OnClick({R.id.tv_integral_details_create_int})
    void confirmExchange() {
        if (integralSettlementBean != null) {
            if(shopCarGoodsSku.getIntegralProductType()==1){
                createSettlementIndent();
            }else{
                if (addressId != 0) {
                    createSettlementIndent();
                } else {
                    showToast(this, "地址未填写,请重新填写地址");
                }
            }
        }
    }

    private void createSettlementIndent() {
        List<ProductInfoBean> productInfoBeans = integralSettlementBean.getProductInfo();
        if (productInfoBeans == null || productInfoBeans.size() < 1) {
            return;
        }
        ProductInfoBean productInfoBean = productInfoBeans.get(0);
        tvIntegralDetailsCreateInt.setEnabled(false);
        String message = edtIntegralProductNote.getText().toString().trim();
        //  创建订单
        String url = Url.BASE_URL + Url.INTEGRAL_CREATE_INDENT;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if(addressId>0){
            params.put("userAddressId", addressId);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", productInfoBean.getId());
            jsonObject.put("saleSkuId", productInfoBean.getSaleSkuId());
            jsonObject.put("count", productInfoBean.getCount());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            params.put("goods", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(message)) {
            params.put("remark", message);
        }
//                结算金额 1 积分+金额 0 纯积分
        params.put("integralType", integralSettlementBean.getTotalPrice() > 0 ? 1 : 0);
        if (integralSettlementBean.getTotalPrice() > 0) {
            if (rbCheckedWechatPay.isChecked() && !rbCheckedAlipay.isChecked()) {
                payType = PAY_WX_PAY;
            } else {
                payType = PAY_ALI_PAY;
            }
            params.put("buyType", payType);
        }
//                默认实物
        params.put("integralProductType", shopCarGoodsSku.getIntegralProductType());
        params.put("source", 0);
        XUtil.Post(url, params, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                tvIntegralDetailsCreateInt.setEnabled(true);
                Gson gson = new Gson();
                if(!TextUtils.isEmpty(payType)){
                    if (payType.equals(PAY_WX_PAY)) {
                        QualityCreateWeChatPayIndentBean qualityWeChatIndent = gson.fromJson(result, QualityCreateWeChatPayIndentBean.class);
                        if (qualityWeChatIndent != null) {
                            if (SUCCESS_CODE.equals(qualityWeChatIndent.getCode())) {
                                if(qualityWeChatIndent.getResult() == null){
                                    showToast(IntegrationIndentWriteActivity.this,"创建订单失败，请重新提交订单");
                                    return;
                                }
                                QualityCreateWeChatPayIndentBean.ResultBean weChatIndentResult = qualityWeChatIndent.getResult();
                                if (weChatIndentResult.getCode().equals(SUCCESS_CODE)) {
                                    orderCreateNo = qualityWeChatIndent.getResult().getNo();
                                    if (weChatIndentResult.getPayKey()!=null){
                                        //返回成功，调起微信支付接口
                                        doWXPay(qualityWeChatIndent.getResult().getPayKey());
                                    }else{
                                        skipDirectIndent();
                                    }
                                } else {
                                    showToast(IntegrationIndentWriteActivity.this, qualityWeChatIndent.getResult().getMsg());
                                }
                            }else {
                                showToast(IntegrationIndentWriteActivity.this, qualityWeChatIndent.getResult()==null?
                                        qualityWeChatIndent.getMsg():qualityWeChatIndent.getResult().getMsg());
                            }
                        }
                    } else {
                        QualityCreateAliPayIndentBean qualityAliPayIndent = gson.fromJson(result, QualityCreateAliPayIndentBean.class);
                        if (qualityAliPayIndent != null) {
                            if (qualityAliPayIndent.getCode().equals(SUCCESS_CODE)) {
                                QualityCreateAliPayIndentBean.ResultBean aliPayIndentResult = qualityAliPayIndent.getResult();
                                if(aliPayIndentResult == null){
                                    showToast(IntegrationIndentWriteActivity.this,"创建订单失败，请重新提交订单");
                                    return;
                                }
                                orderCreateNo = aliPayIndentResult.getNo();
                                if(!TextUtils.isEmpty(aliPayIndentResult.getPayKey())){
                                    //返回成功，调起支付宝支付接口
                                    doAliPay(qualityAliPayIndent.getResult().getPayKey());
                                }else{
                                    skipDirectIndent();
                                }
                            } else {
                                showToast(IntegrationIndentWriteActivity.this, qualityAliPayIndent.getResult() == null
                                        ? qualityAliPayIndent.getMsg() : qualityAliPayIndent.getResult().getMsg());
                            }
                        }
                    }
                }else{
                    CreateIntegralIndentInfo createIntegralIndentInfo = gson.fromJson(result, CreateIntegralIndentInfo.class);
                    if(createIntegralIndentInfo!=null){
                        if(SUCCESS_CODE.equals(createIntegralIndentInfo.getCode())&&createIntegralIndentInfo.getIntegrationIndentBean()!=null){
                            IntegrationIndentBean integrationIndentBean = createIntegralIndentInfo.getIntegrationIndentBean();
                            if(!TextUtils.isEmpty(integrationIndentBean.getNo())){
                                orderCreateNo = integrationIndentBean.getNo();
                                skipDirectIndent();
                            }else{
                                showToast(IntegrationIndentWriteActivity.this,"创建订单失败，请重新提交订单");
                            }
                        }else{
                            showToast(IntegrationIndentWriteActivity.this, createIntegralIndentInfo.getIntegrationIndentBean()==null?
                            createIntegralIndentInfo.getMsg():createIntegralIndentInfo.getIntegrationIndentBean().getMsg());
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                tvIntegralDetailsCreateInt.setEnabled(true);
                showToast(IntegrationIndentWriteActivity.this, R.string.unConnectedNetwork);
                super.onError(ex, isOnCallback);
            }
        });
    }

    private void doAliPay(String pay_param) {
        new AliPay(this, pay_param, new AliPay.AliPayResultCallBack() {
            @Override
            public void onSuccess() {
                showToast(getApplication(), "支付成功");
                skipDirectIndent();
            }

            @Override
            public void onDealing() {
                showToast(getApplication(), "支付处理中...");
            }

            @Override
            public void onError(int error_code) {
                switch (error_code) {
                    case AliPay.ERROR_RESULT:
                        showToast(getApplication(), "支付失败:支付结果解析错误");
                        break;

                    case AliPay.ERROR_NETWORK:
                        showToast(getApplication(), "支付失败:网络连接错误");
                        break;

                    case AliPay.ERROR_PAY:
                        showToast(getApplication(), "支付错误:支付码支付失败");
                        break;

                    default:
                        showToast(getApplication(), "支付错误");
                        break;
                }
                cancelIntegralIndent();
            }

            @Override
            public void onCancel() {
                cancelIntegralIndent();
                showToast(getApplication(), "支付取消");
            }
        }).doPay();
    }

    private void doWXPay(PayKeyBean pay_param) {
        WXPay.init(getApplicationContext());//要在支付前调用
        WXPay.getInstance().doPayDateObject(pay_param, new WXPay.WXPayResultCallBack() {
            @Override
            public void onSuccess() {
                showToast(getApplication(), "支付成功");
                skipDirectIndent();
            }

            @Override
            public void onError(int error_code) {
                cancelIntegralIndent();
                switch (error_code) {
                    case WXPay.NO_OR_LOW_WX:
                        showToast(getApplication(), "未安装微信或微信版本过低");
                        break;
                    case WXPay.ERROR_PAY_PARAM:
                        showToast(getApplication(), "参数错误");
                        break;
                    case WXPay.ERROR_PAY:
                        showToast(getApplication(), "支付失败");
                        break;
                }

            }

            @Override
            public void onCancel() {
                cancelIntegralIndent();
                showToast(getApplication(), "支付取消");
            }
        });
    }

    /**
     * 订单取消
     */
    private void cancelIntegralIndent() {
        String url = Url.BASE_URL + Url.Q_INDENT_CANCEL;
        Map<String, Object> params = new HashMap<>();
        params.put("no", orderCreateNo);
        params.put("userId", userId);
        XUtil.Post(url, params, new MyCallBack<String>() {
            @Override
            public void onSuccess(String result) {
            }
        });
    }

    /**
     * 跳转支付完成页面
     */
    private void skipDirectIndent() {
        Intent intent = new Intent(this, DirectPaySuccessActivity.class);
        intent.putExtra("indentNo", orderCreateNo);
        intent.putExtra(INDENT_PRODUCT_TYPE, INDENT_INTEGRAL_PRODUCT);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_CREATE_ADDRESS_REQ || requestCode == SEL_ADDRESS_REQ) {
//            获取地址成功
            addressId = data.getIntExtra("addressId", 0);
            isFirst = false;
            getAddressDetails();
        } else if (requestCode == IS_LOGIN_CODE) {
            loadData();
        }
    }

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
                    } else {
                        showToast(IntegrationIndentWriteActivity.this, addressInfoEntity.getMsg());
                    }
                }
            }
        });
    }

    //支付宝方式
    @OnClick(R.id.ll_aliPay)
    void aliPay() {
        rbCheckedAlipay.setChecked(true);
        rbCheckedWechatPay.setChecked(false);
    }

    //微信支付方式
    @OnClick(R.id.ll_Layout_weChat)
    void weChat() {
        rbCheckedAlipay.setChecked(false);
        rbCheckedWechatPay.setChecked(true);
    }

    //    地址列表为空 跳转新建地址
    @OnClick(R.id.tv_lv_top)
    void skipNewAddress(View view) {
        Intent intent = new Intent(IntegrationIndentWriteActivity.this, SelectedAddressActivity.class);
        startActivityForResult(intent, NEW_CREATE_ADDRESS_REQ);
    }

    //  更换地址  跳转地址列表
    @OnClick(R.id.ll_indent_address_default)
    void skipAddressList() {
        Intent intent = new Intent(IntegrationIndentWriteActivity.this, SelectedAddressActivity.class);
        intent.putExtra("addressId", addressId);
        startActivityForResult(intent, SEL_ADDRESS_REQ);
    }
}
