package com.gp.common.base.feign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author axing
 * @version 1.0
 * @date 2024/1/10/010 15:47
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotifyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userTgId;

    private String title;

    private String content;

    //用户头像
    private String userAvatar;

    //消息文件
    private List<String> files;

    /**
     * 语言
     */
    private String lanKey;
    /**
     * 机器人
     */
    private String botUserName;
    /**
     * mpay提现
     */
    private Integer isMPayWithdraw;
}
