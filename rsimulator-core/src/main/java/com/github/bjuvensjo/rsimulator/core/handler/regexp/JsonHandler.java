package com.github.bjuvensjo.rsimulator.core.handler.regexp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * JsonHandler is a regular expression handler for json (.json).
 *
 * @see AbstractHandler
 */
@Singleton
public class JsonHandler extends AbstractHandler {
    private final Logger log = LoggerFactory.getLogger(JsonHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();

    private String escape(JsonNode node) {
        if (node.isArray()) {
            return StreamSupport.stream(node.spliterator(), false)
                    .map(this::escape)
                    .collect(Collectors.joining(",", "\\[", "\\]"));
        }
        if (node.isObject()) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(node.fields(), 0), false)
                    .map(field -> "\"" + field.getKey() + "\":" + escape(field.getValue()))
                    .collect(Collectors.joining(",", "\\{", "\\}"));
        }
        return node.toString();
    }

    @Override
    protected String getExtension() {
        return "json";
    }

    @Override
    protected String format(String request) {
        return Optional.ofNullable(request)
                .filter(r -> !r.isBlank())
                .map(r -> {
                    try {
                        return mapper.writeValueAsString(mapper.readValue(request, JsonNode.class)).trim();
                    } catch (Exception e) {
                        log.error(null, e);
                        return request;
                    }
                }).orElse(request);
    }

    @Override
    protected String escape(String request, boolean isCandidate) {
        return Optional.ofNullable(request)
                .filter(r -> isCandidate && !r.isBlank())
                .map(r -> {
                    try {
                        return escape(mapper.readValue(r, JsonNode.class));
                    } catch (Exception e) {
                        log.error(null, e);
                        return request;
                    }
                })
                .orElse(request);
    }
}
