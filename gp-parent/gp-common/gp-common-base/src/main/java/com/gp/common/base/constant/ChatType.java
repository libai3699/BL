package com.gp.common.base.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/13/013 12:26
 */
public class ChatType {

    public static String USERCHATTYPE = "private";
    public static String GROUPCHATTYPE = "group";
    public static String CHANNELCHATTYPE = "channel";
    public static String SUPERGROUPCHATTYPE  = "supergroup";


    public static Map<String, Integer> chatLocation = new HashMap<>();
    public static Map<Integer, String> chatLocation2 = new HashMap<>();
    static {
        chatLocation.put(SUPERGROUPCHATTYPE, 0);
        chatLocation.put(CHANNELCHATTYPE, 1);


        chatLocation2.put(0, "公开群");
        chatLocation2.put(1, "频道");
    }


    public static boolean isPrivate(String chatType){
        return USERCHATTYPE.equals(chatType);
    }
}
