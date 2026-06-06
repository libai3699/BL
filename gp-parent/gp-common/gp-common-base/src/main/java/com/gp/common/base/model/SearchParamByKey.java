package com.gp.common.base.model;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/16/016 16:18
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchParamByKey
{
    /** 搜索关键字 */
    private String key;
    /** 查询类型 */
    private Integer type;
    /** 当前记录起始索引 */
    private Integer pg = 1;
    /** 每页显示记录数 */
    private Integer sz = 12;


    public String toValueStr(){
        StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("pg_").append(this.getPg()).append(",");
                stringBuilder.append("sz_").append(this.getSz()).append(",");
        if (this.getKey() != null) {
            stringBuilder.append("key_").append(this.getKey()).append(",");
        }
        if (this.getType() != null) {
            stringBuilder.append("type_").append(this.getType()).append(",");
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }

    public static String getJsonStr(String searchKey){
        String[] split = searchKey.split(",");
        JSONObject jsonObject = new JSONObject();
        for (String str : split) {
            String[] value = str.split("_");
            jsonObject.put(value[0], value[1]);
        }
        return jsonObject.toJSONString();
    }


    public static SearchParamByKey getSearchParamByKey(String searchKey){
        String[] split = searchKey.split(",");
        JSONObject jsonObject = new JSONObject();
        for (String str : split) {
            String[] value = str.split("_");
            jsonObject.put(value[0], value[1]);
        }
        return JSON.parseObject(jsonObject.toJSONString(), SearchParamByKey.class);
    }
}
