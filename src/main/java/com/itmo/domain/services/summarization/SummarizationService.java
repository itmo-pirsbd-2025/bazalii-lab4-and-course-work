package com.itmo.domain.services.summarization;

import com.itmo.domain.models.core.Message;
import com.itmo.domain.models.core.Summary;
import com.itmo.domain.models.core.SummaryCreationModel;
import com.itmo.domain.repositories.summarization.SummarizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SummarizationService {
    private final static String urlRegex = "(https?://\\S+)";
    private final SummarizationRepository summarizationRepository;

    public SummarizationService(SummarizationRepository summarizationRepository) {

        this.summarizationRepository = summarizationRepository;
    }

    public Mono<Summary> summarize(SummaryCreationModel model) {
        var prompt = buildPrompt(model.messages());

        return summarizationRepository
                .summarize(prompt)
                .map(generated -> {
                    var id = UUID.randomUUID();

                    return new Summary(id, generated);
                })
                .delayUntil(summarizationRepository::saveSummary);
    }

    public Mono<Summary> findById(UUID id) {
        return summarizationRepository.findById(id);
    }

    private static String buildPrompt(Message[] messages) {
        var sb = new StringBuilder();

        sb.append(
                """
                        Ты — помощник для постмортема/сводки обсуждений.
                        Сделай краткое, но подробное содержание ветки сообщений ТОЛЬКО по предоставленному треду. Ничего не выдумывай и не добавляй фактов от себя.
                        
                        Правила:
                        - Пиши строго по сообщениям; если данных нет — так и скажи.
                        - Если итогового решения нет — явно напиши "Итогового решения нет".
                        - Укажи, кто какие предложения выдвигал (с привязкой к авторам).
                        - Включи все ссылки/референсы (URL) в отдельный список.
                        - Если есть конкретные числа/метрики/даты/времена — обязательно отрази их без искажений.
                        - Ответ верни СТРОГО в формате JSON, без markdown, без пояснений и без лишнего текста.
                        
                        Схема JSON (верни все поля; если нет данных — пустые строки/массивы):
                        {
                          "initial_problem": "…",
                          "discussion_summary": "…",
                          "timeline": ["…", "…"],
                          "proposals_by_author": [{"author":"…","proposal":"…"}],
                          "decisions": ["…"],
                          "action_items": [{"owner":"…","task":"…","due":"…|unknown"}],
                          "open_questions": ["…"],
                          "links": ["…"]
                        }
                        
                        Ниже тред сообщений:
                        --- MESSAGES START ---
                        """);

        var preparedMessages = preprocessMessages(messages);

        var index = 1;

        for (var message : preparedMessages) {
            sb.append("[")
                    .append(index)
                    .append("] ")
                    .append(message.author())
                    .append(":\n")
                    .append(message.content())
                    .append("\n\n");

            index++;
        }

        return sb.toString().trim();
    }

    private static Message[] preprocessMessages(Message[] messages) {
        if (messages == null || messages.length == 0) {
            return new Message[0];
        }

        var out = new ArrayList<Message>(messages.length);

        String currentAuthor = null;
        var currentContent = new StringBuilder();

        for (var message : messages) {
            if (message == null) {
                continue;
            }

            var author = normalizeWhitespace(message.author());
            var content = normalizeWhitespace(message.content());

            content = truncatePreservingUrls(content);

            if (currentAuthor == null) {
                currentAuthor = author;
                currentContent.append(content);
                continue;
            }

            if (currentAuthor.equals(author)) {
                currentContent.append("\n").append(content);
            } else {
                out.add(new Message(currentAuthor, currentContent.toString().trim()));
                currentAuthor = author;
                currentContent.setLength(0);
                currentContent.append(content);
            }
        }

        if (currentAuthor != null && StringUtils.hasText(currentContent.toString())) {
            out.add(new Message(currentAuthor, currentContent.toString().trim()));
        }

        return out.toArray(new Message[0]);
    }

    private static String normalizeWhitespace(String text) {
        var normalized = text.replace("\r\n", "\n").replace("\r", "\n");
        normalized = normalized.replaceAll("[\\t\\x0B\\f ]+", " ");
        normalized = normalized.replaceAll("\\n{3,}", "\n\n");

        return normalized.trim();
    }

    private static String truncatePreservingUrls(String text) {
        final int maximalMessageTextLength = 2000;
        final int stringForUrlMarkersLength = 200;

        if (text.length() <= maximalMessageTextLength) {
            return text;
        }

        var urlPattern = Pattern.compile(urlRegex);

        var urlToMarker = new LinkedHashMap<String, String>();
        var linksMatcherForFullText = urlPattern.matcher(text);

        while (linksMatcherForFullText.find()) {
            var url = linksMatcherForFullText.group(1);

            if (!urlToMarker.containsKey(url)) {
                var marker = "[URL_" + (urlToMarker.size() + 1) + "]";
                urlToMarker.put(url, marker);
            }
        }

        var head = text.substring(0, maximalMessageTextLength - stringForUrlMarkersLength).trim();

        var lastHttpIndex = Math.max(head.lastIndexOf("http://"), head.lastIndexOf("https://"));

        if (lastHttpIndex != -1) {
            var tail = head.substring(lastHttpIndex);

            if (!tail.matches("https?://\\S+")) {
                head = head.substring(0, lastHttpIndex).trim();
            }
        }
        var headMatcher = urlPattern.matcher(head);

        var stringBuilder = new StringBuilder();

        while (headMatcher.find()) {
            var url = headMatcher.group(1);
            var marker = urlToMarker.get(url);

            headMatcher.appendReplacement(stringBuilder, Matcher.quoteReplacement(marker));
        }

        headMatcher.appendTail(stringBuilder);

        var sb = new StringBuilder();
        sb.append(stringBuilder).append("\n\n(truncated)\n");

        if (!urlToMarker.isEmpty()) {
            sb.append("URLs:\n");

            for (var entry : urlToMarker.entrySet()) {
                sb.append("- ")
                        .append(entry.getValue())
                        .append(" ")
                        .append(entry.getKey())
                        .append("\n");
            }
        }

        return sb.toString().trim();
    }
}
