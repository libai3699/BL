package com.gp.common.base.context;

import lombok.Data;
import org.slf4j.Logger;

/**
 * 业务上下文
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/13/013 12:26
 */
@Data
public class EngineContext<P, R> {

    private P inputModel;

    private R outputModel;

    private Logger log;
}
