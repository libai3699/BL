//package com.common.core.constant;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * @author axing
// */
//@NoArgsConstructor
//@Getter
//public enum PayEnum {
//
//    WALLET_PAY("wallet_pay", "MAPY支付"),
////    PIX_PAY("pix", "西班牙支付"),
////    PIX_YND_PAY("pix_ynd", "越南盾支付"),
////    UFB("UFB", "U付宝"),
////    Pay1818("pay1818", "pay1818"),
////    HJC_BASE_PC("BASE_PC", "恒聚财-印度卢比"),
////    HJC_BB_PC("BB_PC", "恒聚财-波币支付"),
////    HJC_HW_PC("HW_PC", "恒聚财-汇旺支付"),
////    HJC_USDT_PC("USDT_PC", "恒聚财-U支付"),
////    HJC_TRUST_CN("TRUST_CN", "恒聚财-支付宝支付"),
////    HUIONE_TRANSFER("huione", "汇旺转账"),
////    JC_PAY("JCPAY", "聚财-支付"),
////    HUIONE_PAY("huione_pay", "汇旺官方支付USDT"),
////    HUIONE_PAY2("huione_pay2", "汇旺官方支付USD"),
////    ZH_PAY_8005("zh_pay_8005", "综合支付-越南网银"),
////    ZH_PAY_8006("zh_pay_8006", "综合支付-越南ZALO"),
////    ZH_PAY_8007("zh_pay_8007", "综合支付-越南MOMO"),
////    ZH_PAY_4("zh_pay_4", "综合支付-越南Newbayar"),
////    DXTX("DXTX", "鼎信天下"),
////    Jpai_WX("Jpai_WX", "Jpai_WX"),
////    Jpai_ZFB("Jpai_ZFB", "Jpai_ZFB"),
////    Jpai_BANK("Jpai_BANK", "Jpai_BANK"),
////    Jpai_USDT("Jpai_USDT", "Jpai_USDT"),
////    Jpai_wallet("Jpai-wallet", "Jpai-wallet"),
////    DL_PAY("DL_PAY", "DL_PAY"),
////    CX_PAY("CX_PAY", "CX_PAY"),
////    HH_PAY("HH_PAY", "HH_PAY"),
////    WB_PAY("WB_PAY", "WB_PAY"),
////    bobi_PAY("bobi_PAY", "bobi_PAY"),
////    Alipay_PAY("Alipay_PAY", "Alipay_PAY"),
////    CB_PAY("CB_PAY", "CB_PAY"),
////    KBY_PAY("KBY_PAY", "KBY_PAY"),
////    HENGXIN_PAY("HENGXIN_PAY", "HENGXIN_PAY"),
//    ;
//    /**
//     * 场景对应的编码
//     */
//    private String code;
//    /**
//     * 业务场景描述
//     */
//    private String desc;
//
//    PayEnum(String code, String desc) {
//        this.code = code;
//        this.desc = desc;
//    }
//
//    /**
//     * 获取支付类型
//     */
//    public static Set<Integer> getType(String code) {
//        Set<Integer> typeSet = new HashSet<>();
//        PayEnum payEnum = PayEnum.valueOfForCode(code);
//        if (payEnum != null) {
//            switch (payEnum.code) {
//                case "wallet_pay":
//                    typeSet.add(0);
//                    typeSet.add(1);
//                    break;
//                case "pix":
//                    typeSet.add(2);
//                    break;
//                case "pix_ynd":
//                    typeSet.add(3);
//                    break;
//                case "UFB":
//                    typeSet.add(4);
//                    break;
//                case "pay1818":
//                    typeSet.add(5);
//                    break;
//                case "BASE_PC":
//                    typeSet.add(6);
//                    break;
//                case "BB_PC":
//                    typeSet.add(7);
//                    break;
//                case "HW_PC":
//                    typeSet.add(8);
//                    break;
//                case "USDT_PC":
//                    typeSet.add(9);
//                    break;
//                case "TRUST_CN":
//                    typeSet.add(10);
//                    break;
//            }
//        }
//        return typeSet;
//    }
//
//    /**
//     * 根据code获取相应的枚举
//     *
//     * @param code
//     * @return
//     */
//    public static PayEnum valueOfForCode(String code) {
//        for (PayEnum expCompanyEnum : PayEnum.values()) {
//            if (expCompanyEnum.code.equals(code)) {
//                return expCompanyEnum;
//            }
//        }
//        return null;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getDesc() {
//        return desc;
//    }
//
//    public void setDesc(String desc) {
//        this.desc = desc;
//    }
//
//
//}
