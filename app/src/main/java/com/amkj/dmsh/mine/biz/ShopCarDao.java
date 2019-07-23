package com.amkj.dmsh.mine.biz;

import android.text.TextUtils;

import com.amkj.dmsh.mine.adapter.ShopCarGoodsAdapter;
import com.amkj.dmsh.mine.bean.ActivityInfoBean;
import com.amkj.dmsh.mine.bean.ShopCarEntity.ShopCartBean;
import com.amkj.dmsh.mine.bean.ShopCarEntity.ShopCartBean.CartBean;
import com.amkj.dmsh.mine.bean.ShopCarEntity.ShopCartBean.CartBean.CartInfoBean;
import com.amkj.dmsh.shopdetails.bean.CombineCartBean;
import com.amkj.dmsh.shopdetails.bean.CombineCartBean.CombineMatchsBean;
import com.amkj.dmsh.shopdetails.bean.CombineGoodsBean;
import com.amkj.dmsh.shopdetails.bean.GroupGoodsEntity.GroupGoodsBean.CombineCommonBean;
import com.amkj.dmsh.shopdetails.bean.IndentWriteEntity.IndentWriteBean.ProductsBean;
import com.amkj.dmsh.shopdetails.bean.IndentWriteEntity.IndentWriteBean.ProductsBean.ProductInfoBean;
import com.amkj.dmsh.shopdetails.bean.CombineGoodsBean.MatchProductsBean;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.amkj.dmsh.constant.ConstantVariable.PRODUCT;
import static com.amkj.dmsh.constant.ConstantVariable.TITLE;


public class ShopCarDao {

    /**
     * 选择全部，点下全部按钮，改变所有商品选中状态
     */
    public static void selectDeleteAll(List<MultiItemEntity> shopGoodsList, boolean isSelect) {
        for (MultiItemEntity multiItemEntity : shopGoodsList) {
            if (multiItemEntity.getItemType() == PRODUCT) {
                ((CartInfoBean) multiItemEntity).setDelete(isSelect);
            }
        }
    }

    /**
     * 正常商品选择
     *
     * @param isChecked true全部选中 false全部取消选中
     */
    public static void selectBuyAll(List<MultiItemEntity> shopGoodsList, boolean isChecked) {
        for (int i = 0; i < shopGoodsList.size(); i++) {
            if (shopGoodsList.get(i).getItemType() == PRODUCT) {
                CartInfoBean cartInfoBean = (CartInfoBean) shopGoodsList.get(i);
                if (cartInfoBean.getStatus() == 1 && cartInfoBean.isValid()) {
                    cartInfoBean.setSelected(isChecked);
                }
            }
        }
    }

    /**
     * 获取所有被选中的删除商品的购物车id以及数量总和
     */
    public static String[] getSelGoodsInfo(List<MultiItemEntity> shopGoodsList) {
        String[] infos = new String[2];
        StringBuilder carIds = new StringBuilder();
        int totalDelNum = 0;
        int index = 0;
        for (MultiItemEntity multiItemEntity : shopGoodsList) {
            if (multiItemEntity.getItemType() == PRODUCT) {
                CartInfoBean cartInfoBean = (CartInfoBean) multiItemEntity;
                if (cartInfoBean.isDelete()) {
                    index++;
                    if (index == 1) {
                        carIds.append(cartInfoBean.getId());
                    } else {
                        carIds.append(",").append(cartInfoBean.getId());
                    }
                    totalDelNum = totalDelNum + cartInfoBean.getCount();
                }
            }

        }

        infos[0] = carIds.toString();
        infos[1] = String.valueOf(totalDelNum);
        return infos;
    }


    /**
     * 删除选中的商品
     */
    public static List<ActivityInfoBean> removeSelGoods(List<MultiItemEntity> shopGoodsList, ShopCarGoodsAdapter shopCarGoodsAdapter) {
        List<MultiItemEntity> delGoods = new ArrayList<>();
        List<ActivityInfoBean> activityList = new ArrayList<>();
        for (int i = 0; i < shopGoodsList.size(); i++) {
            if (shopGoodsList.get(i).getItemType() == PRODUCT) {
                CartInfoBean cartInfoBean = ((CartInfoBean) shopGoodsList.get(i));
                int parentPosition = shopCarGoodsAdapter.getParentPosition(cartInfoBean);
                ActivityInfoBean activityInfoBean = ((ActivityInfoBean) shopGoodsList.get(parentPosition));
                if (cartInfoBean.isDelete()) {
                    //如果选中的是组合主商品，删掉整个组合商品
                    if (cartInfoBean.isMainProduct()) {
                        if (activityInfoBean != null && activityInfoBean.getSubItems() != null) {
                            List<CartInfoBean> subItems = activityInfoBean.getSubItems();
                            delGoods.addAll(subItems);
                            subItems.clear();
                        }
                    } else {
                        if (activityInfoBean != null && !TextUtils.isEmpty(activityInfoBean.getActivityCode()) && !activityList.contains(activityInfoBean)) {
                            activityList.add(activityInfoBean);
                        }

                        if (activityInfoBean != null) {
                            activityInfoBean.removeSubItem(cartInfoBean);
                        }

                        delGoods.add(cartInfoBean);
                    }
                }
            }
        }
        shopGoodsList.removeAll(delGoods);
        shopCarGoodsAdapter.notifyDataSetChanged();
        return activityList;
    }


    /**
     * 单个图标的处理
     *
     * @param isEditStatus 是否编辑状态
     */
    public static void selectOne(List<MultiItemEntity> shopGoodsList, CartInfoBean cartInfoBean, boolean isEditStatus) {
        if (cartInfoBean != null) {
            Boolean isSelected = !cartInfoBean.isSelected();
            Boolean isDelete = !cartInfoBean.isDelete();
            for (MultiItemEntity multiItemEntity : shopGoodsList) {
                if (multiItemEntity.getItemType() == PRODUCT) {
                    CartInfoBean bean = (CartInfoBean) multiItemEntity;
                    if (bean.getId() == cartInfoBean.getId()) {
                        if (isEditStatus) {
                            bean.setDelete(isDelete);
                        } else {
                            bean.setSelected(isSelected);
                        }
                    }
                }
            }
        }
    }

    /**
     * @return 0=选中的商品数量；
     */
    public static String[] getShoppingCount(List<MultiItemEntity> shopGoodsList) {
        String[] infos = new String[2];
        int selectedCount = 0;
        for (int i = 0; i < shopGoodsList.size(); i++) {
            MultiItemEntity multiItemEntity = shopGoodsList.get(i);
            if (multiItemEntity.getItemType() == TITLE) {
                ActivityInfoBean activityInfoBean = (ActivityInfoBean) multiItemEntity;
                List<CartInfoBean> subItems = activityInfoBean.getSubItems();
                if (subItems != null) {
                    for (CartInfoBean cartInfoBean : subItems) {
                        boolean isSelected = cartInfoBean.isSelected();
                        if (isSelected && cartInfoBean.isValid()) {
                            selectedCount = selectedCount + (cartInfoBean.isMainProduct() ? subItems.size() : cartInfoBean.getCount());
                        }
                    }
                }
            }
        }
        infos[0] = (selectedCount + "");
        return infos;
    }

    /**
     * 获取普通结算商品
     */
    public static List<CartInfoBean> getSettlementGoods(List<MultiItemEntity> shopGoodsList) {
        List<CartInfoBean> shopCarGoodsSkuList = new ArrayList<>();
        for (MultiItemEntity multiItemEntity : shopGoodsList) {
            if (multiItemEntity.getItemType() == PRODUCT) {
                CartInfoBean cartInfoBean = (CartInfoBean) multiItemEntity;
                if (cartInfoBean.isSelected() && !cartInfoBean.isMainProduct() && !cartInfoBean.isCombineProduct()) {
                    shopCarGoodsSkuList.add(cartInfoBean);
                }
            }
        }
        return shopCarGoodsSkuList;
    }

    /**
     * 获取组合结算商品
     */
    public static List<CombineGoodsBean> getCombineGoods(List<MultiItemEntity> shopGoodsList) {
        List<CombineGoodsBean> combineGoods = new ArrayList<>();
        for (MultiItemEntity multiItemEntity : shopGoodsList) {
            if (multiItemEntity.getItemType() == TITLE) {
                ActivityInfoBean activityInfoBean = (ActivityInfoBean) multiItemEntity;
                if (!TextUtils.isEmpty(activityInfoBean.getActivityCode()) && (activityInfoBean.getActivityCode().contains("ZH") || activityInfoBean.getActivityType() == 6)) {
                    List<CartInfoBean> subItems = activityInfoBean.getSubItems();
                    if (subItems != null && subItems.size() > 0 && subItems.get(0).isMainProduct() && subItems.get(0).isSelected() && subItems.get(0).isValid()) {
                        CombineGoodsBean combineGoodsBean = new CombineGoodsBean();
                        for (CartInfoBean cartInfoBean : subItems) {
                            if (cartInfoBean.isMainProduct()) {
                                combineGoodsBean.setMainId(0);
                                combineGoodsBean.setCount(cartInfoBean.getCount());
                                combineGoodsBean.setProductId(cartInfoBean.getProductId());
                                combineGoodsBean.setCartId(cartInfoBean.getId());
                                if (cartInfoBean.getSaleSku() != null) {
                                    combineGoodsBean.setSkuId(cartInfoBean.getSaleSku().getId());
                                }
                            } else if (cartInfoBean.isCombineProduct()) {
                                CombineGoodsBean.MatchProductsBean matchProductsBean = new CombineGoodsBean.MatchProductsBean();
                                matchProductsBean.setCombineMatchId(0);
                                matchProductsBean.setProductId(cartInfoBean.getProductId());
                                if (cartInfoBean.getSaleSku() != null) {
                                    matchProductsBean.setSkuId(cartInfoBean.getSaleSku().getId());
                                }
                                combineGoodsBean.getMatchProducts().add(matchProductsBean);
                            }
                        }
                        combineGoods.add(combineGoodsBean);
                    }
                }
            }
        }
        return combineGoods;
    }


    //获取该层级所有商品
    private static List<CartInfoBean> getSubItem(ShopCarGoodsAdapter shopCarGoodsAdapter, CartInfoBean cartInfoBean) {
        List<CartInfoBean> subItems = new ArrayList<>();
        int parentPosition = shopCarGoodsAdapter.getParentPosition(cartInfoBean);
        if (parentPosition != -1) {
            ActivityInfoBean activityInfoBean = (ActivityInfoBean) shopCarGoodsAdapter.getData().get(parentPosition);
            if (activityInfoBean != null && activityInfoBean.getSubItems() != null) {
                subItems.addAll(activityInfoBean.getSubItems());
            }
        }

        return subItems;
    }

    /**
     * 获取选中的商品购物车id集合
     */
    public static List<Integer> getCartIds(List<MultiItemEntity> shopGoodsList) {
        List<Integer> cartIds = new ArrayList<>();
        for (MultiItemEntity multiItemEntity : shopGoodsList) {
            if (multiItemEntity.getItemType() == PRODUCT) {
                CartInfoBean cartInfoBean = (CartInfoBean) multiItemEntity;
                if (cartInfoBean.isSelected() && cartInfoBean.isValid()) {
                    cartIds.add(cartInfoBean.getId());
                }
            }
        }
        return cartIds;
    }

    /**
     * 判断商品是否有效
     */
    public static boolean isValid(CartInfoBean cartInfoBean) {
        //1.没有失效 2.有库存 3.非待售 4.sku属性正常
        return cartInfoBean.getStatus() == 1 && cartInfoBean.getSaleSku() != null && cartInfoBean.getSaleSku().getQuantity() > 0 && !cartInfoBean.isForSale();
    }

    /**
     * 刷新商品信息
     *
     * @param cartInfoBean 被修改的商品
     * @param activityList 需要更新的活动集合
     */
    public static void updateGoodsInfo(ShopCarGoodsAdapter shopCarGoodsAdapter, ShopCartBean
            shopCartBean, CartInfoBean cartInfoBean, List<ActivityInfoBean> activityList) {
        List<CartBean> carts = shopCartBean.getCarts();
        if (activityList != null) {
            for (ActivityInfoBean bean : activityList) {
                for (MultiItemEntity multiItemEntity : shopCarGoodsAdapter.getData()) {
                    if (multiItemEntity.getItemType() == TITLE && multiItemEntity.equals(bean)) {
                        ActivityInfoBean activityInfoBean = (ActivityInfoBean) multiItemEntity;
                        for (CartBean cartBean : carts) {
                            ActivityInfoBean activityInfo = cartBean.getActivityInfo();
                            if (activityInfo != null && activityInfo.getActivityCode().equals(activityInfoBean.getActivityCode())) {
                                activityInfoBean.setActivityRule(activityInfo.getActivityRule());
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        } else if (cartInfoBean != null && carts != null && carts.size() > 0) {
            for (CartBean cartBean : carts) {
                List<CartInfoBean> cartInfoList = cartBean.getCartInfoList();
                ActivityInfoBean activityInfoBean = cartBean.getActivityInfo();
                if (cartInfoList != null && cartInfoList.size() > 0) {
                    for (CartInfoBean bean : cartInfoList) {
                        ActivityInfoBean parent = (ActivityInfoBean) shopCarGoodsAdapter.getData().get(shopCarGoodsAdapter.getParentPosition(cartInfoBean));
                        String activityCode = parent.getActivityCode();
                        //判断是否是活动商品(如果是，更新活动规则)
                        if (!TextUtils.isEmpty(activityCode) && activityInfoBean != null && activityInfoBean.getActivityCode().equals(activityCode)) {
                            parent.setActivityRule(cartBean.getActivityInfo().getActivityRule());
                        }

                        //匹配被修改的商品
                        if (bean.getId() == cartInfoBean.getId()) {
                            cartInfoBean.update(bean);
                            break;
                        }
                    }
                }
            }
        }

        shopCarGoodsAdapter.notifyDataSetChanged();
    }

    //获取活动组是否有商品被选中
    public static boolean subItemCheceked(ActivityInfoBean activityInfoBean) {
        int num = 0;
        List<CartInfoBean> cartInfoBeans = activityInfoBean.getSubItems();
        for (CartInfoBean cartInfoBean : cartInfoBeans) {
            if (cartInfoBean.isSelected()) {
                num++;
            }
        }

        return num > 0;
    }

    //获取所有活动信息
    public static List<ActivityInfoBean> getActivityInfos(List<MultiItemEntity> shopGoodsList) {
        List<ActivityInfoBean> ActivityInfos = new ArrayList<>();
        for (MultiItemEntity multiItemEntity : shopGoodsList) {
            if (multiItemEntity.getItemType() == TITLE) {
                ActivityInfoBean activityInfoBean = (ActivityInfoBean) multiItemEntity;
                if (!TextUtils.isEmpty(activityInfoBean.getActivityCode())) {
                    ActivityInfos.add(activityInfoBean);
                }
            }
        }

        return ActivityInfos;
    }

    //判断组合商品库存（组合商品无库存或者所有搭配商品无库存时返回false）
    public static boolean checkStock(List<CombineCommonBean> goods) {
        int num = 0;//无库存的搭配商品数量
        for (CombineCommonBean commonBean : goods) {
            if (commonBean.isMainProduct()) {
                if (commonBean.getStock() == 0) {
                    //主商品无库存直接返回false
                    return false;
                }
            } else {
                if (commonBean.getStock() == 0) {
                    num++;
                }
            }
        }

        return num < goods.size() - 1;
    }

    //组合搭配加入购物车
    public static String getCombinesCart(List<CombineCommonBean> groupList) {
        try {
            CombineCartBean combineBean = new CombineCartBean();
            List<CombineMatchsBean> combineMatchs = new ArrayList<>();
            for (CombineCommonBean bean : groupList) {
                if (bean.isMainProduct()) {
                    combineBean.setCount(bean.getCount());
                    combineBean.setProductId(bean.getProductId());
                    combineBean.setSkuId(bean.getSkuId());
                    combineBean.setActivityCode(bean.getActivityCode());
                } else if (bean.isSelected()) {
                    CombineMatchsBean combineMatchsBean = new CombineMatchsBean();
                    combineMatchsBean.setProductId(bean.getProductId());
                    combineMatchsBean.setSkuId(bean.getSkuId());
                    combineMatchs.add(combineMatchsBean);
                }
            }
            combineBean.setCombineMatchs(combineMatchs);
            return new Gson().toJson(combineBean);
        } catch (Exception e) {
            return "";
        }
    }


    public static String[] getIndentInfo(List<ProductsBean> products) {
        String[] info = new String[2];
        List<CombineGoodsBean> combineGoodsList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        for (ProductsBean productsBean : products) {
            List<ProductInfoBean> productInfos = productsBean.getProductInfos();
            ProductInfoBean combineMainProduct = productsBean.getCombineMainProduct();
            List<ProductInfoBean> combineMatchProducts = productsBean.getCombineMatchProducts();
            //组合商品
            if (combineMainProduct != null) {
                CombineGoodsBean combineGoodsBean = new CombineGoodsBean();
                List<MatchProductsBean> matchProducts = new ArrayList<>();
                combineGoodsBean.setSkuId(combineMainProduct.getSaleSkuId());
                combineGoodsBean.setProductId(combineMainProduct.getId());
                combineGoodsBean.setCount(combineMainProduct.getCount());
                combineGoodsBean.setMainId(combineMainProduct.getCombineMainId());
                if (combineMatchProducts != null && combineMatchProducts.size() > 0) {
                    for (ProductInfoBean productInfoBean : combineMatchProducts) {
                        MatchProductsBean matchProductsBean = new MatchProductsBean();
                        matchProductsBean.setProductId(productInfoBean.getId());
                        matchProductsBean.setCombineMatchId(productInfoBean.getCombineMatchId());
                        matchProductsBean.setSkuId(productInfoBean.getSaleSkuId());
                        matchProducts.add(matchProductsBean);
                    }
                }
                combineGoodsBean.setMatchProducts(matchProducts);
                combineGoodsList.add(combineGoodsBean);
            } else if (productInfos != null && productInfos.size() > 0) {
                for (ProductInfoBean productInfoBean : productInfos) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("saleSkuId", productInfoBean.getSaleSkuId());
                        jsonObject.put("id", productInfoBean.getId());
                        jsonObject.put("count", productInfoBean.getCount());
                        jsonArray.put(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        if (jsonArray.length()>0){
            info[0] = jsonArray.toString().trim();
        }
        if (combineGoodsList.size()>0){
            info[1] = new Gson().toJson(combineGoodsList);
        }
        return info;
    }

}
