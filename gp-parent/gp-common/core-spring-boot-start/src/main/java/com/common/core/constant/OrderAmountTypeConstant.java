//package com.common.core.constant;
//
//
///**
// * 转盘奖励类型
// *
// * @author ruoyi
// */
//public interface OrderAmountTypeConstant {
//    public static final int Direct = 0;
//    public static final int Mpay = 1;
//    public static final int Pix = 2;
//    public static final int PixYND = 3;
//    public static final int UPay = 4;
//    public static final int Pay1818 = 5;
//    public static final int BASE_PC = 6;
//    public static final int BB_PC = 7;
//    public static final int HW_PC = 8;
//    public static final int USDT_PC = 9;
//    public static final int TRUST_CN = 10;
//    public static final int JC_PAY = 11;
//    public static final int HUIONE_PAY = 12;
//    public static final int DXTX = 13;
//    public static final int other = 14;
//
//    public static Integer getTypeConstant(PayEnum payType) {
//        switch (payType) {
//            case PIX_PAY:
//                return Pix;
//            case PIX_YND_PAY:
//                return PixYND;
//            case UFB:
//                return UPay;
//            case Pay1818:
//                return Pay1818;
//            default:
//                return Mpay; // 默认处理
//        }
//    }
//
//
//    public static PayEnum getTypePayEnum(Integer typeConstant) {
//        switch (typeConstant) {
//            case Pix:
//                return PayEnum.PIX_PAY;
//            case PixYND:
//                return PayEnum.PIX_YND_PAY;
//            case UPay:
//                return PayEnum.UFB;
//            case Pay1818:
//                return PayEnum.Pay1818;
//            default:
//                return PayEnum.WALLET_PAY; // 默认处理
//        }
//    }
//}
