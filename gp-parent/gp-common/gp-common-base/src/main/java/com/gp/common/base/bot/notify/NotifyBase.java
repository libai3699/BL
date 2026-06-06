package com.gp.common.base.bot.notify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/20/020 17:29
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyBase implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
}
