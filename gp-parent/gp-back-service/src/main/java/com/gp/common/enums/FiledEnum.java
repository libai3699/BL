package com.gp.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum FiledEnum {

    tAd("tAd", "广告ID:adId,用户ID:userId,用户的飞机Id:tgUserId,用户名称:username,广告类型id:adTypeId,广告类型key:adTypeKey,广告类型名称:adTypeName,广告名称:adName,广告标题:adTitle,广告内容(广告简介):adContent,广告封面:adCover,广告地址:adUrl,广告展示数量:adOpenNum,广告总购买量:adTotalOpenNum,广告状态:adStatus,广告开始时间:startTime,广告结束时间:finishTime,广告购买时间:createTime"),

    tAdOpenNumAmount("tAdOpenNumAmount", "ID:openNumId,u套餐:amount,1u展示次数:rate,创建时间:createTime,创建人:creater," +
            "修改时间:updateTime,修改人:updater"),

    tAdType("tAdType", "广告类型id:adTypeId,广告类型Key:adTypeKey,广告类型名称:adTypeName,售价:amount,创建时间:createTime"),

    tAmountChange("tAmountChange", "商户账变id:id,用户ID:userId,tg用户ID:tgUserId,用户名称:username,关联的订单号:orderNo," +
            "类型:accountType,帐变类型:type,变更金额:amount,变更前金额:oldAmount,变更后金额:newAmount," +
            "备注:remark,操作人:operator,变更时间:createTime"),

    tBrand("tBrand", "品牌id:brandId,品牌简介:brandInfo,品牌标题:brandTitle,品牌名称:brandName,品牌链接:brandLink," +
            "品牌封面:brandPic,用户id:userId,飞机用户id:tgUserId,用户名称:username,品牌状态:status," +
            "品牌支付状态:payStatus,支付关联订单:payOrderNo,支付时间:payTime,开始时间:startTime,结束时间:finishTime,创建时间:createTime"),

    tBrandConf("tBrandConf", "id:id,金额:amount,月数(能购买几个月):num,创建时间:createTime,创建人:creater," +
            "修改时间:updateTime,修改人:updater"),

    tChat("tChat", "tg群组id:chatId,群组类型:chatType,群组用户名:chatUsername,群组title:chatTitle,群组描述:chatDescription," +
            "群组人员数量:chatNum,收录人ID:includeUserId,收录人飞机ID:includeTgUserId,用户名称:username,购买的关键词:buyKye," +
            "查询排序:queryOrder,创建时间:createTime,修改时间:updateTime"),

    tContent("tContent", "内容id:cId,唯一值:uniqueId,类型:type,群组频道id:chatId,聊天框类型:chatType," +
            "群组频道用户名:chatUsername,群组频道名称:chatTitle,群组频道简介:chatDescription,群组人员数量:chatNum,购买关键词:buyKey," +
            "消息转发量:msgForwards,文件id:fileId,文件hash:fileAccessHash,创建时间:createTime,修改时间:updateTime"),

    tKeyBuyManage("tKeyBuyManage", "id:id,关键词:key,排序:num,花费金额:amount,飞机用户id:tgUserId," +
            "用户id:userId,用户名称:username,管理员飞机id:adminTgUserId,管理员用户id:adminUserId,管理员用户名称:adminUsername," +
            "标题:title,链接:link,开始时间:startTime,结束时间:finishTime,状态:status,关键词购买订单号:keyOrderNo," +
            "创建时间:createTime"),

    tKeyBuyNotice("tKeyBuyNotice", "id:id,广告购买id:keyBuyId,关键词:key,排序:num,订阅人飞机用户id:tgUserId," +
            "订阅人用户id:userId,用户名称:username,创建时间:createTime"),

    tKeySearchNumAmont("tKeySearchNumAmont", "id:id,最小搜索量:minNum,最大搜索量:maxNum,第1名价格:amount1,第2名价格:amount2," +
            "第3名价格:amount3,第4名价格:amount4,第5名价格:amount5,第6名价格:amount6,第7名价格:amount7," +
            "第8名价格:amount8,第9名价格:amount9,第10名价格:amount10,创建时间:createTime,创建人:creater,修改时间:updateTime," +
            "修改人:updater"),

    tOrderAdOpenNum("tOrderAdOpenNum", "id:orderId,订单单号:orderNo,用户id:userId,tg用户Id:tgUserId,用户名称:username," +
            "广告类型Id:adTypeId,金额:amount,播放数量:openNum,订单状态:orderType,支付类型:payType," +
            "外部订单信息:payOutInfo,支付时间:payTime,下单时间:createTime"),

    tOrderAmount("tOrderAmount", "id:id,充值单号:orderNo,用户id:userId,tg用户Id:tgUserId,用户名称:username," +
            "金额:amount,付款地址:payAddr,订单状态:orderStatus,上游订单号:upOrderNo,上游订单信息:upPayInfo," +
            "支付时间:payTime,下单时间:create_time"),

    tOrderBrand("tOrderBrand", "id:id,订单号:orderNo,品牌id:brandId,品牌名:brandName,花费金额:amount," +
            "飞机用户id:tgUserId,用户id:userId,用户名称:username,创建时间:createTime"),

    tOrderWithdraw("tOrderWithdraw", "id:id,提现单号:orderNo,用户id:userId,tg用户Id:tgUserId,用户名称:username," +
            "金额:amount,手续费:fee,实际到账金额:realAmount,收款地址:receiveAddr,订单状态:orderStatus," +
            "备注:remark,上游订单号:upOrderNo,上游下单信息:upOrderInfo,上游回调信息:upNotifyInfo,支付时间:payTime,下单时间:createTime"),

    tPage("tPage", "页面id:pageId,页面标识:pageKey,页面名称:pageName,页面的文本内容:pageContent,支持广告:supportAd," +
            "内联键盘:inlineKeyBoard,创建时间:createTime"),

    tUser("tUser", "用户id:userId,上级用户id:superUserId,飞机id:tgId,上级飞机id:superTgId,上级用户名称:username," +
            "飞机名称:tgName,飞机用户名:tgUsername,金额(usdt):money,创建时间:createTime"),

    tWhiteIp("tWhiteIp", "白名单id:id,ip地址:ip,备注:remark,创建时间:createTime,修改时间:updateTime," +
            "创建人:createBy,修改人:updateBy"),

    sysConfig("sysConfig", "参数主键:configId,参数名称:configName,参数键名:configKey,参数键值:configValue,系统内置:configType" +
            ",创建时间:createTime,修改时间:updateTime,创建人:createBy,修改人:updateBy,备注:remark"),

    sysDept("sysDept", "部门ID:deptId,父部门ID:parentId,祖级列表:ancestors,部门名称:deptName,显示顺序:orderNum," +
            "负责人:leader,联系电话:phone,邮箱:email,部门状态:status,删除标志:delFlag," +
            "父部门名称:parentName,创建时间:createTime,修改时间:updateTime,创建人:createBy,修改人:updateBy,备注:remark"),

    sysDictData("sysDictData", "字典编码:dictCode,字典排序:dictSort,字典标签:dictLabel,字典键值:dictValue,字典类型:dictType," +
            "样式属性:cssClass,表格字典样式:listClass,是否默认:isDefault,状态:status,创建时间:createTime,修改时间:updateTime," +
            "创建人:createBy,修改人:updateBy,备注:remark"),

    sysDictType("sysDictType", "字典主键:dictId,字典名称:dictName,字典类型:dictType,状态:status" +
            ",创建时间:createTime,修改时间:updateTime,创建人:createBy,修改人:updateBy,备注:remark"),

    sysMenu("sysMenu", "菜单ID:menuId,菜单名称:menuName,父菜单名称:parentName,父菜单ID:parentId,显示顺序:orderNum" +
            ",路由地址:path,组件路径:component,是否为外链:isFrame,类型:menuType,显示状态:visible,菜单状态:status,权限字符串:perms,菜单图标:icon" +
            ",创建时间:createTime,修改时间:updateTime,创建人:createBy,修改人:updateBy,备注:remark"),

    sysNotice("sysNotice", "公告ID:noticeId,公告标题:noticeTitle,公告类型:noticeType,公告内容:noticeContent,公告状态:status" +
            ",创建时间:createTime,修改时间:updateTime,创建人:createBy,修改人:updateBy,备注:remark"),

    sysPost("sysPost", "岗位序号:postId,岗位编码:postCode,岗位名称:postName,岗位排序:postSort,状态:status" +
            ",创建时间:createTime,修改时间:updateTime,创建人:createBy,修改人:updateBy,备注:remark"),

    sysRole("sysRole", "角色ID:roleId,角色名称:roleName,角色权限:roleKey,角色排序:roleSort,数据范围:dataScope," +
            "角色状态:status,删除标志:delFlag,是否默认:isDefault,状态:status,创建时间:createTime,修改时间:updateTime," +
            "创建人:createBy,修改人:updateBy,备注:remark"),

    sysUser("sysUser", "用户ID:userId,部门ID:deptId,用户账号:userName,用户昵称:nickName,用户邮箱:email," +
            "手机号码:phonenumber,用户性别:sex,用户头像:avatar,帐号状态:status,删除标志:delFlag," +
            "最后登陆IP:loginIp,最后登陆时间:loginDate,创建时间:createTime,修改时间:updateTime," +
            " 创建人:createBy,修改人:updateBy,备注:remark"),

    sysLogininfor("sysLogininfor", "ID:infoId,用户账号:userName,登录状态:status,登录IP地址:ipaddr,登录地点:loginLocation," +
            "浏览器类型:browser,操作系统:os,提示消息:msg,访问时间:loginTime"),

    sysOperLog("sysOperLog", "操作序号:operId,操作模块:title,业务类型:businessType,请求方法:method,请求方式:requestMethod," +
            "操作类别:phonenumber,操作类别:operatorType,操作人员:operName,部门名称:deptName,请求url:operUrl," +
            "操作地址:operIp,操作地点:operLocation,请求参数:operParam,返回参数:jsonResult," +
            " 操作状态:status,错误消息:errorMsg,操作时间:operTime"),

    sysUserOnline("sysUserOnline", "会话编号:tokenId,部门名称:deptName,用户名称:userName,登录IP地址:ipaddr,登录地址:loginLocation," +
            "浏览器类型:browser,操作系统:os,登录时间:loginTime"),


    ;

    private String filed;
    private String filedName;


    public static String getFiledName(String filed) {
        FiledEnum[] values = values();
        for (FiledEnum filedEnum : values) {
            if (filedEnum.getFiled().equals(filed)) {
                return filedEnum.getFiledName();
            }
        }
        return null;
    }

}
