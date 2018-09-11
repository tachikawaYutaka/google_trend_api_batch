package com.wakabatimes.google_trend_api_batch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "settings")
@Data
class BatchConst {
    private String url;
}
