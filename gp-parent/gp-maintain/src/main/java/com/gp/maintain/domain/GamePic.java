package com.gp.maintain.domain;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2024/11/21/021 13:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamePic implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String plateCode;
    private String gameNameZh;
    private String gameNameEN;


    public static void main(String[] args) {
        double similarity = StrUtil.similar("你好呀美", "你好呀靓");
        System.out.println(similarity);
    }
}
