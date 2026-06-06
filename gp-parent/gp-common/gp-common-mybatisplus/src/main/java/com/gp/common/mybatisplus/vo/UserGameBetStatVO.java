package com.gp.common.mybatisplus.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gp.common.mybatisplus.aspect.Localized;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@ApiModel(description = "用户详情/游戏/游戏厂商报表")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGameBetStatVO {

    private Long gameId;

    private String gameIcon;

    @Localized("gameName")
    private String gameName;
    @JsonIgnore
    private String gameNameZh;
    @JsonIgnore
    private String gameNameEn;
    @JsonIgnore
    private String gameNameKo;
    @JsonIgnore
    private String gameNamePt;
    @JsonIgnore
    private String gameNameVi;
    @JsonIgnore
    private String gameNameTr;
    @JsonIgnore
    private String gameNameTw;
    @JsonIgnore
    private String gameNameJa;
    @JsonIgnore
    private String gameNameHi;
    @JsonIgnore
    private String gameNameTh;
    @JsonIgnore
    private String gameNameRu;
    @JsonIgnore
    private String gameNameAr;

    private String gameTypeCode;

    @Localized("gameTypeName")
    private String gameTypeName;
    @JsonIgnore
    private String gameTypeNameZh;
    @JsonIgnore
    private String gameTypeNameEn;
    @JsonIgnore
    private String gameTypeNameKo;
    @JsonIgnore
    private String gameTypeNamePt;
    @JsonIgnore
    private String gameTypeNameVi;
    @JsonIgnore
    private String gameTypeNameTr;
    @JsonIgnore
    private String gameTypeNameTw;
    @JsonIgnore
    private String gameTypeNameJa;
    @JsonIgnore
    private String gameTypeNameHi;
    @JsonIgnore
    private String gameTypeNameTh;
    @JsonIgnore
    private String gameTypeNameRu;
    @JsonIgnore
    private String gameTypeNameAr;

    private String plateCode;

    private String plateIcon;

    private String plateBlackIcon;

    @Localized("plateName")
    private String plateName;
    @JsonIgnore
    private String plateNameZh;
    @JsonIgnore
    private String plateNameEn;
    @JsonIgnore
    private String plateNameKo;
    @JsonIgnore
    private String plateNamePt;
    @JsonIgnore
    private String plateNameVi;
    @JsonIgnore
    private String plateNameTr;
    @JsonIgnore
    private String plateNameTw;
    @JsonIgnore
    private String plateNameJa;
    @JsonIgnore
    private String plateNameHi;
    @JsonIgnore
    private String plateNameTh;
    @JsonIgnore
    private String plateNameRu;
    @JsonIgnore
    private String plateNameAr;

    /**
     * 下注金额
     */
    private BigDecimal betAmount;

    /**
     * 返奖
     */
    private BigDecimal win;

    /**
     * 游戏盈亏(返奖 - 投注金额)
     */
    private BigDecimal winLoss;

    /**
     * 打码量
     */
    private BigDecimal codeAmount;

    private BigDecimal payoutRate;
}
