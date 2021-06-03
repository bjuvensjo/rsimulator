package com.github.bjuvensjo.rsimulator.core.handler.regexp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Core {
    public static final UnaryOperator<String> txtFormat = UnaryOperator.identity();
    public static final BiFunction<String, Boolean, String> txtEscape = (r, c) -> r;
    private static final Logger log = LoggerFactory.getLogger(Core.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public final static Function<JsonNode, String> escape = node -> {
        if (node.isArray()) {
            return StreamSupport.stream(node.spliterator(), false)
                    .map(Core.escape)
                    .collect(Collectors.joining(",", "\\[", "\\]"));
        }
        if (node.isObject()) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(node.fields(), 0), false)
                    .map(field -> "\"" + field.getKey() + "\":" + Core.escape.apply(field.getValue()))
                    .collect(Collectors.joining(",", "\\{", "\\}"));
        }
        return node.toString();
    };

    public static final UnaryOperator<String> jsonFormat = request -> Optional.ofNullable(request)
            .filter(r -> !r.isBlank())
            .map(r -> {
                try {
                    return mapper.writeValueAsString(mapper.readValue(request, JsonNode.class)).trim();
                } catch (Exception e) {
                    log.error(null, e);
                    return request;
                }
            }).orElse(request);

    public static final BiFunction<String, Boolean, String> jsonEscape = (request, isCandidate) -> Optional.ofNullable(request)
            .filter(r -> isCandidate && !r.isBlank())
            .map(r -> {
                try {
                    return Core.escape.apply(mapper.readValue(r, JsonNode.class));
                } catch (Exception e) {
                    log.error(null, e);
                    return request;
                }
            })
            .orElse(request);
}
