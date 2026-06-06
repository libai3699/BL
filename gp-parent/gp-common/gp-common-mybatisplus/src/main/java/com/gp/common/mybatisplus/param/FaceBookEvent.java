package com.gp.common.mybatisplus.param;

import com.common.core.service.BigDecimalSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaceBookEvent {
    private String event_name;
    private Long event_time;
    private String action_source;
    private String event_source_url;
    private OriginalEventData original_event_data;
    private UserData user_data;
    private CustomData custom_data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OriginalEventData {
        private String event_name;
        private long event_time;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserData {
        private List<String> external_id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomData {
        private String currency;
        private BigDecimal value;
    }
}


