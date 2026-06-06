package com.gp.common.base.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2024/1/12/012 21:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleBindParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "谷歌秘钥")
    private String googleKey;
    @ApiModelProperty(value = "谷歌秘钥二维码")
    private String qrBarcodeBase;
}
