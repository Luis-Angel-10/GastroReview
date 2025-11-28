package websiters.gastroreview.service;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import websiters.gastroreview.model.ReviewComment;
import websiters.gastroreview.model.ReviewCommentAnalysis;

@Service
@RequiredArgsConstructor
public class TextAnalysisService {

    private final TextAnalyticsClient client;

    public ReviewCommentAnalysis analyze(ReviewComment comment) {

        DocumentSentiment sentiment = client.analyzeSentiment(comment.getContent());

        Iterable<String> phraseIterable = client.extractKeyPhrases(comment.getContent());

        StringBuilder phraseBuilder = new StringBuilder();
        for (String phrase : phraseIterable) {
            if (phraseBuilder.length() > 0) phraseBuilder.append(",");
            phraseBuilder.append(phrase);
        }

        String keyPhrases = phraseBuilder.toString();

        return ReviewCommentAnalysis.builder()
                .comment(comment)
                .sentiment(sentiment.getSentiment().toString())
                .positiveScore(sentiment.getConfidenceScores().getPositive())
                .neutralScore(sentiment.getConfidenceScores().getNeutral())
                .negativeScore(sentiment.getConfidenceScores().getNegative())
                .keyPhrases(keyPhrases)
                .build();
    }
}
