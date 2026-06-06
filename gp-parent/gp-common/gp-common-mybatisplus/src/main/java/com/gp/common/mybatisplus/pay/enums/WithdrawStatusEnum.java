package com.gp.common.mybatisplus.pay.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum WithdrawStatusEnum{

    status0(0, "待审核"), //用户广告购买
    status1(1, "风险审核通过"), //用户广告购买
    status2(2, "财务审核通过"), //用户广告购买
    status3(3, "审核拒绝"), //用户广告购买
    status4(4, "提现成功"), //用户广告购买
    status5(5, "提现失败"), //用户广告购买
    status6(6, "上游体现下单成功"), //用户广告购买
    status7(7, "上游体现下单失败"), //用户广告购买


    ;

   public static Map<List<Integer>, String> userTypeMsg = MapUtil.ofEntries(
            MapUtil.entry(CollUtil.newArrayList(0, 1, 2), "审核中"),
            MapUtil.entry(CollUtil.newArrayList(6), "审核通过"),
            MapUtil.entry(CollUtil.newArrayList(3), "审核拒绝"),
            MapUtil.entry(CollUtil.newArrayList(4), "提现成功"),
            MapUtil.entry(CollUtil.newArrayList(5, 7), "提现失败")
    );

    private Integer type;
    private String typeName;


    public static String getTypeName(Integer type) {
        WithdrawStatusEnum[] values = values();
        for (WithdrawStatusEnum withdrawStatusEnum: values) {
            if (withdrawStatusEnum.getType().equals(type)) {
                return withdrawStatusEnum.getTypeName();
            }
        }
        return null;
    }

    public static String getUserTypeName(Integer type) {
        String msg = "";
        for (Map.Entry<List<Integer>, String> listStringEntry : userTypeMsg.entrySet()) {
            if (listStringEntry.getKey().contains(type)) {
                msg = listStringEntry.getValue();
                break;
            }
        }
        return msg;
    }

    public static void main(String[] args) {
        System.out.println(getTypeName(7));
    }

}
