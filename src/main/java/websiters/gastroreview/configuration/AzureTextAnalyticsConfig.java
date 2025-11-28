package websiters.gastroreview.configuration;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureTextAnalyticsConfig {

    @Bean
    public TextAnalyticsClient textAnalyticsClient() {

        String endpoint = System.getenv("AZURE_ENDPOINT");
        String key = System.getenv("AZURE_KEY");

        if (endpoint == null || key == null) {
            throw new IllegalStateException("AZURE_ENDPOINT or AZURE_KEY is missing");
        }

        return new TextAnalyticsClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(key))
                .buildClient();
    }
}
