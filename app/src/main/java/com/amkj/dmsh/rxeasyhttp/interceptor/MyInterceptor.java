package com.amkj.dmsh.rxeasyhttp.interceptor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.amkj.dmsh.BuildConfig;
import com.amkj.dmsh.constant.ConstantMethod;
import com.amkj.dmsh.constant.Url;
import com.amkj.dmsh.mine.activity.MineLoginActivity;
import com.amkj.dmsh.network.NetLoadListenerHelper;
import com.amkj.dmsh.network.NetLoadUtils;
import com.amkj.dmsh.rxeasyhttp.utils.DeviceUtils;
import com.amkj.dmsh.user.bean.TokenExpireBean;
import com.amkj.dmsh.utils.SharedPreUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.amkj.dmsh.constant.ConstantMethod.savePersonalInfoCache;
import static com.amkj.dmsh.constant.ConstantVariable.TOKEN;
import static com.amkj.dmsh.constant.ConstantVariable.TOKEN_EXPIRE_TIME;

/**
 * Created by xiaoxin on 2018/1/16 0016
 */

public class MyInterceptor implements Interceptor {
    private Context mContext;

    public MyInterceptor(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        //获取api公用参数
        String mDomoCommon = getCommonApiParameter(mContext);
        builder.addHeader("domo-custom", mDomoCommon);
//        String token = (String) SharedPreUtils.getParam(ShareConstants.TOKEN, "");
//        if (!TextUtils.isEmpty(token) && request.url().toString().startsWith(UrlConstants.BASE_URL + "/oauthapi")) {
//            builder.addHeader("Authorization", token);
//        }

        Response response = chain.proceed(builder.build());

        //打印响应结果
        if (BuildConfig.DEBUG) {
            ResponseBody body = response.peekBody(1024 * 1024);
            String ss = body.string();

            Log.d("retrofit", "----------Start-----------");
            //打印请求Ulr
            Log.d("retrofitRequest", String.format("Sending request %s",
                    request.url()));

            //打印请求参数
            String method = request.method();
            if ("POST".equals(method)) {
                StringBuilder sb = new StringBuilder();
                if (request.body() instanceof FormBody) {
                    FormBody formBody = (FormBody) request.body();
                    for (int i = 0; i < formBody.size(); i++) {
                        sb.append(formBody.encodedName(i) + "=" + formBody.encodedValue(i) + ",");
                    }
                    sb.delete(sb.length() - 1, sb.length());
                    Log.d("retrofitRequestBody", "{" + sb.toString() + "}");
                }
            }

            Log.d("retrofitResponse", ss);
            Log.d("retrofit", "----------end-----------");
            Log.d("retrofit", "                        ");
        }

//        //判断token是否过期
//        if (response.code() == 401) {
//            Request.Builder newbuilder = request.newBuilder();
//            Response newresponse = chain.proceed(newbuilder.build());
//            SharedPreUtils.setParam(ShareConstants.TOKEN, "");
//            return newresponse;
//        }
        return response;
    }

    //多么生活api通用参数
    public static String getCommonApiParameter(Context context) {
        Map<String, Object> map = new HashMap<>();
        map.put("appVersion", DeviceUtils.getVersionName(context));
        map.put("device", DeviceUtils.getModel());
        map.put("sysVersion", DeviceUtils.getSDKVersion());
        map.put("timestamp", System.currentTimeMillis());
        map.put("source", 1);//访问来源 0：ios  1:android 2:移动web 3：小程序 4：官网
        map.put("clientIp", DeviceUtils.getIpAddress(context));
        map.put("clientMac", DeviceUtils.getMacAddress(context));
        map.put("deviceId", DeviceUtils.getAndroidID(context));
        if (ConstantMethod.userId > 0) {
            String token = (String) SharedPreUtils.getParam(TOKEN, "");
            long tokenExpireTime = (long) SharedPreUtils.getParam(TOKEN_EXPIRE_TIME, 0L);
            long currentTimeMillis = System.currentTimeMillis();
            //token不为空
            if (!TextUtils.isEmpty(token)) {
                map.put("token", token);
                //如果token过期
                if (currentTimeMillis > tokenExpireTime) {
                    //请求接口判断是否真的过期
                    NetLoadUtils.getNetInstance().loadNetDataPost(context, Url.CONFIRM_LOGIN_TOKEN_EXPIRE, null, new NetLoadListenerHelper() {
                        @Override
                        public void onSuccess(String result) {
                            Gson gson = new Gson();
                            TokenExpireBean tokenExpireBean = gson.fromJson(result, TokenExpireBean.class);
                            if (tokenExpireBean != null) {
                                //未过期(更新本地过期时间)
                                if (tokenExpireBean.getStatus() == 1) {
                                    SharedPreUtils.setParam(TOKEN_EXPIRE_TIME, System.currentTimeMillis() + tokenExpireBean.getExpireTime());
                                } else {
                                    //Token过期,清除本地登录信息
                                    savePersonalInfoCache(context, null);
                                    //调用登出接口
                                    NetLoadUtils.getNetInstance().loadNetDataPost(context, Url.LOG_OUT, null, null);
                                    context.startActivity(new Intent(context, MineLoginActivity.class));
                                    ConstantMethod.showToast("长期未登录，请重新登录");
                                }
                            }
                        }
                    });
                }
            }

        }
        JSONObject jsonObject = new JSONObject(map);
        return Base64.encodeToString(jsonObject.toString().getBytes(), Base64.NO_WRAP);
    }
}
