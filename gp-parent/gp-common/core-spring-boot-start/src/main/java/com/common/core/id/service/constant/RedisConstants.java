package com.common.core.id.service.constant;

/**
 * @author lengleng
 * @date 2017/10/29
 */
public interface RedisConstants {
    /**
     * header 中租户ID
     */
    String TENANT_ID = "TENANT-ID";

    /**
     * header 中版本信息
     */
    String VERSION = "VERSION";

    /**
     * header 中平台code
     */
    String CLIENT_CODE = "clientCode";

    /**
     * header 子平台code
     */
    String CLIENT_ITEM = "clientItem";

    /**
     * header 平台skinCode
     */
    String SKIN_CODE = "skinCode";

    /**
     * header 中平台Domain
     */
    String CLIENT_DOMAIN = "clientDomain";

    /**
     * 默认CLIENT_CODE
     */
    String DEFAULT_CLIENT_CODE_1 = "0";

    /**
     * 租户ID
     */
    Integer TENANT_ID_1 = 0;


    /**
     * 禁用
     */
    String STATUS_DEL = "1";
    /**
     * 正常
     */
    String STATUS_NORMAL = "0";

    /**
     * 锁定
     */
    String STATUS_LOCK = "9";

    /**
     * 账户异常
     */
    String STATUS_EXCEPTION = "8";

    /**
     * 菜单
     */
    String MENU = "0";

    /**
     * 菜单树根节点
     */
    Integer MENU_TREE_ROOT_ID = -1;

    /**
     * 编码
     */
    String UTF8 = "UTF-8";

    /**
     * 前端工程名
     */
    String FRONT_END_PROJECT = "pigx-ui";

    /**
     * 后端工程名
     */
    String BACK_END_PROJECT = "pigx";

    /**
     * 验证码前缀
     */
    String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY_";

    /**
     * 公共参数
     */
    String PIG_PUBLIC_PARAM_KEY = "PIG_PUBLIC_PARAM_KEY";

    /**
     * 成功标记
     */
    Integer SUCCESS = 0;
    /**
     * 失败标记
     */
    Integer FAIL = 1;


    /**
     * 默认存储bucket
     */
    String BUCKET_NAME = "mujun";

    /**
     *
     */
    String BUCKET_EXCEL_NAME = "mujun-excel";

    /**
     * 数据逻辑删除标志 已删除
     */
    final static Short DEL_FLAG_YES = 0;

    /**
     * 数据逻辑删除标志 未删除
     */
    final static Short DEL_FLAG_NO = 1;

    final static String SECURITY_ENCODE_KEY = "pigxpigxpigxpigx";

    /**
     * 系统后台注册标记
     */
    Integer ADMIN_TYPE = 1;

    String ADMIN_PHONE = "18888888888";
    // 类型，4管理员,3股东 2总代 1代理 0会员
    Byte MEMBER_TYPE_4 = 4;
    Byte MEMBER_TYPE_3 = 3;
    Byte MEMBER_TYPE_2 = 2;
    Byte MEMBER_TYPE_1 = 1;
    Byte MEMBER_TYPE_0 = 0;


    // 类型，1大股东2股东3总代理4代理
    Integer AGENT_LEVEL_1 = 1;
    Integer AGENT_LEVEL_2 = 2;
    Integer AGENT_LEVEL_3 = 3;
    Integer AGENT_LEVEL_4 = 4;

    //平台状态 0禁用 1正常
    Byte CLIENT_STATUS_ZERO = 0;
    Byte CLIENT_STATUS_ONE = 1;

    // 网站ip访问控制
    final Byte WHITELIST_IP = 1;
    final Byte BLACKLIST_IP = 2;
    String WEBSITE_IP_ENABLE = "website_ip_enable::";
    String WEBSITE_IP_ENABLE_ALL = "website_ip_enable_all::";
    String LIST_WHITELIST_IP = "list_whitelist_ip::";
    String LIST_BLACKLIST_IP = "list_blacklist_ip::";

    //基础厂商数据
    String CLIENT_VENDOR_LIST = "CLIENT_VENDOR_LIST::";

}
