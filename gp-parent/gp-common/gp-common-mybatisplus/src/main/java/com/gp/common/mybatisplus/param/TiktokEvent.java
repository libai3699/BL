package com.gp.common.mybatisplus.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TiktokEvent {
    private String event_source;
    private String event_source_id;
    private List<EventData> data;

    // Getter and Setter
    // ...
   @Data
   public static  class EventData {
        private String event;
        private long event_time; // 使用 long 来表示时间戳
        private User user;
        private Properties properties;

        // Getter and Setter
        // ...
    }
    @Data
    public  static  class User {
        private String external_id;

        // Getter and Setter
        // ...
    }
    @Data
    public static class Properties {
        private String currency;
        private String value;

        // Getter and Setter
        // ...
    }
}


