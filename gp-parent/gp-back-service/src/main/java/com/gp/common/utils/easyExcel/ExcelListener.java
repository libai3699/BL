package com.gp.common.utils.easyExcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mazhitao
 * @Description
 * @create 2019-10-15 11:16
 */
public class ExcelListener extends AnalysisEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelListener.class);

    List<Object> list = new ArrayList<>();

    @Override
    public void invoke(Object data, AnalysisContext context) {
        LOGGER.info("解析到一条数据:{}", JSON.toJSONString(data));
//        Integer curRowNum = context.getCurrentRowNum();
//        if(curRowNum > 0){
//            LOGGER.info("增加数据:{}", JSON.toJSONString(data));
//            list.add(data);
//        }
        list.add(data);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

        LOGGER.info("所有数据解析完成！");
    }

    public List<Object> getDatas(){
        return list;
    }

}
