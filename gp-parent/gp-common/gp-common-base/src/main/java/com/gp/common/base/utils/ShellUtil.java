package com.gp.common.base.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HtmlUtil;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author axing
 * @version 1.0
 * @date 2022/12/29 22:11
 */
public class ShellUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String shellRegex = "(?i)\\b(bash|sh|nc|netcat|bashbug|cmd|curl|wget|socat|telnet|python|python3|perl|ruby|php|nmap)\\b(?=\\s|;|&&|\\||>|<|\\(|\\)|$)";
//    public static String javaRegex = "(\\$\\{.*?\\}|#\\{.*?\\}|T\\(.*?\\)|Runtime\\s*\\(.*?\\)|System\\s*\\(.*?\\)|exec\\s*\\(.*?\\)|\\$\\(.*?\\))";
    public static String javaRegex = "(\\$\\{.*?\\}|#\\{.*?\\}|T\\(.*?\\)|\\$\\(.*?\\)|Runtime\\.getRuntime\\s*\\(\\)\\.exec\\s*\\(.*?\\)|System\\.exit\\s*\\(.*?\\)|eval\\s*\\(.*?\\)|ScriptEngineManager\\s*\\(.*?\\)\\.getEngineByName\\s*\\(.*?\\)\\.eval\\s*\\(.*?\\)|ExpressionParser\\s*\\(.*?\\)\\.parseExpression\\s*\\(.*?\\)|ObjectInputStream\\s*\\(.*?\\)\\.readObject\\s*\\(\\))";


    public static boolean matches(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        // 检查文本中的所有匹配项
//        while (matcher.find()) {
//            // 打印匹配到的部分
//            System.out.println("Found match: " + matcher.group());
//        }
        return matcher.find();
    }

    /**
     * 校验命令注入
     * @param input
     * @return
     */
    public static boolean checkCommandInjection(String input) {
        // 使用正则表达式进行过滤
        return checkShell(input) || checkJava(input) || checkUnicode(input) || isHexEncoded(input) || isHtmlEntityEncoded(input) || isUrlEncoded(input) || isBase64Encoded(input);
    }

    public static boolean checkShell(String input) {
        // 使用正则表达式进行过滤
        return matches(input, shellRegex);
    }


    public static boolean checkJava(String input) {
        // 使用正则表达式进行过滤
        return matches(input, javaRegex);
    }

    //校验 Unicode
    public static boolean checkUnicode(String str) {
        return str.matches(".*\\\\u[0-9a-fA-F]{4}.*");
    }
    //校验十六进制编码
    public static boolean isHexEncoded(String str) {
        // 使用正则表达式检查字符串是否仅包含十六进制允许的字符
        return str.matches("^[0-9a-fA-F]+$");
    }
    //校验HTML编码
    public static boolean isHtmlEntityEncoded(String str) {
        // 使用 Hutool 的 HtmlUtil 解码 HTML 实体
        String decoded = HtmlUtil.unescape(str);
        // 如果解码后的字符串与原字符串不同，那么原字符串可能包含 HTML 实体编码
        return !decoded.equals(str);
    }
    //检查 URL 编码
    @SneakyThrows
    public static boolean isUrlEncoded(String str) {
//        String decoded = URLUtil.decode(str);
        // 编码字符串
//        String decoded = URLEncoder.encode(str, "UTF-8");
//        System.out.println(decoded);
        // 如果解码后的字符串与原字符串不同，则可能是URL编码
//        return !decoded.equals(str);
        // 正则表达式来检查 % 后跟两位十六进制数
        return str.matches(".*%[0-9a-fA-F]{2}.*");
    }
    //校验base64编码
    public static boolean isBase64Encoded(String str) {
        try {
            // 尝试解码字符串
            String decoded = Base64.decodeStr(str);
            // 尝试编码解码后的字符串
            String encoded = Base64.encode(decoded);
            // 如果编码后的字符串与原字符串匹配，且原字符串不包含非Base64字符，则可能是Base64编码
            return encoded.equals(str.replaceAll("=", "")) && str.matches("^[A-Za-z0-9+/=]*$");
        } catch (Exception e) {
            // 解码失败，不是有效的 Base64 编码
            return false;
        }
    }

    //校验十六进制编码
    public static void main(String[] args) {

//        String demo = " https://walletfrontapi.c2vm25eyh6.xyz/favicon.ico {\"sec-fetch-mode\":\"no-cors\",\"referer\":\"https://walletfrontapi.c2vm25eyh6.xyz/swagger-ui.html\",\"sec-fetch-site\":\"same-origin\",\"accept-language\":\"zh-CN,zh;q=0.9\",\"X-Forwarded-Proto\":\"https\",\"Host\":\"walletfrontapi.c2vm25eyh6.xyz\",\"X-Forwarded-Port\":\"443\",\"X-Amzn-Trace-Id\":\"Root=1-6619f5b3-2bc659212a97da096343a288\",\"accept\":\"image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8\",\"sec-ch-ua\":\"\\\"Google Chrome\\\";v=\\\"123\\\", \\\"Not:A-Brand\\\";v=\\\"8\\\", \\\"Chromium\\\";v=\\\"123\\\"\",\"sec-ch-ua-mobile\":\"?0\",\"sec-ch-ua-platform\":\"\\\"macOS\\\"\",\"X-Forwarded-For\":\"43.239.85.49\",\"accept-encoding\":\"gzip, deflate, br, zstd\",\"user-agent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36\",\"sec-fetch-dest\":\"image\"} {}  ";
//        String demo01 = " https://walletfrontapi.c2vm25eyh6.xyz/favicon.ico {\"sec-fetch-mode\":\"no-cors\",\"referer\":\"https://walletfrontapi.c2vm25eyh6.xyz/swagger-ui.html\",\"sec-fetch-site\":\"same-origin\",\"accept-language\":\"zh-CN,zh;q=0.9\",\"X-Forwarded-Proto\":\"https\",\"Host\":\"walletfrontapi.c2vm25eyh6.xyz\",\"X-Forwarded-Port\":\"443\",\"X-Amzn-Trace-Id\":\"Root=1-6619f5b3-2bc659212a97da096343a288\",\"accept\":\"image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8\",\"sec-ch-ua\":\"\\\"Google Chrome\\\";v=\\\"123\\\", \\\"Not:A-Brand\\\";v=\\\"8\\\", \\\"Chromium\\\";v=\\\"123\\\"\",\"sec-ch-ua-mobile\":\"?0\",\"sec-ch-ua-platform\":\"\\\"macOS\\\"\",\"X-Forwarded-For\":\"43.239.85.49\",\"accept-encoding\":\"gzip, deflate, br, zstd\",\"user-agent\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36\",\"sec-fetch-dest\":\"image\"} {}  ";
        String demo = "$(meinv)";
        System.out.println("checkShell: "+checkShell(demo)); // 输出 "SELECT  FROM users   rf /tmp"
        System.out.println("checkJava: "+checkJava(demo)); // 输出 "SELECT  FROM users   rf /tmp"
        System.out.println("checkUnicode: "+checkUnicode(demo)); // 输出 "SELECT  FROM users   rf /tmp"
        System.out.println("isHexEncoded: "+isHexEncoded(demo)); // 输出 "SELECT  FROM users   rf /tmp"
        System.out.println("isHtmlEntityEncoded: "+isHtmlEntityEncoded(demo)); // 输出 "SELECT  FROM users   rf /tmp"
        System.out.println("isUrlEncoded: "+isUrlEncoded(demo)); // 输出 "SELECT  FROM users   rf /tmp"
        System.out.println("isBase64Encoded: "+isBase64Encoded(demo)); // 输出 "SELECT  FROM users   rf /tmp"
        System.out.println("checkCommandInjection: "+checkCommandInjection(demo)); // 输出 "SELECT  FROM users   rf /tmp"



    }

}
