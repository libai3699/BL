package com.gp.common.base.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * google翻译工具类
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Slf4j
public class GoogleTranslate {

    /**
     * en 英文
     * <p>
     * zh-CN 中文
     *
     * @param langFrom 翻译前的语言类型 eg: en
     * @param langTo   翻译后的语言类型 eg: zh-CN
     * @param word     翻译的内容
     * @return
     */
    public static Map<String,String> translate(String langFrom, String langTo, String word) {
        Map<String,String> result = new HashMap<>();
        result.put("en",word);
        result.put("cn",word);
        result.put("translate","no");
        try {
            String url = "https://translate.googleapis.com/translate_a/single?" +
                    "client=gtx&" +
                    "sl=" + langFrom +
                    "&tl=" + langTo +
                    "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            JSONArray objects = JSON.parseArray(response.toString());
            JSONArray objects2 = (JSONArray) objects.get(0);
            JSONArray objects3 = (JSONArray) objects2.get(0);
            String en = objects3.get(1).toString();
            String cn = objects3.get(0).toString();
            result.put("en",objects3.get(1).toString());
            result.put("cn",objects3.get(0).toString());
            result.put("translate",!en.equals(cn)?"yes":"no");
            return result;
        } catch (Exception e) {
            log.error("谷歌翻译异常", e);
            return result;
        }

    }

    public static void main(String[] args) {

        try {
            System.out.println(GoogleTranslate.translate("en", "zh-CN", "FF Baia Mare (W)"));
            System.out.println(GoogleTranslate.translate("en", "zh-CN", "CSS Targoviste (W)"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
