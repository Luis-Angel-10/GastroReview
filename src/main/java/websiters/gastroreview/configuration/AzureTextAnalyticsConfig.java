package websiters.gastroreview.configuration;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureTextAnalyticsConfig {

    @Value("${azure.text.endpoint}")
    private String endpoint;

    @Value("${azure.text.key}")
    private String key;

    @Bean
    public TextAnalyticsClient textAnalyticsClient() {
        return new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(key))
                .endpoint(endpoint)
                .buildClient();
    }
}
