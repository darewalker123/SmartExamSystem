package com.smartexam.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static Map<String, String> readFormOrJson(HttpServletRequest request) throws IOException {
        Map<String, String> values = new LinkedHashMap<>();
        request.getParameterMap().forEach((key, value) -> values.put(key, value.length == 0 ? "" : value[0]));
        if (!values.isEmpty()) {
            return values;
        }
        String body;
        try (BufferedReader reader = request.getReader()) {
            body = reader.lines().collect(Collectors.joining());
        }
        if (body == null || body.isBlank()) {
            return values;
        }
        String trimmed = body.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
            for (String entry : trimmed.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")) {
                String[] pair = entry.split(":", 2);
                if (pair.length == 2) {
                    values.put(unquote(pair[0]), unquote(pair[1]));
                }
            }
        }
        return values;
    }

    public static void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    public static String object(String... pairs) {
        StringBuilder json = new StringBuilder("{");
        for (int i = 0; i < pairs.length; i += 2) {
            if (i > 0) {
                json.append(',');
            }
            json.append(quote(pairs[i])).append(':').append(pairs[i + 1]);
        }
        return json.append('}').toString();
    }

    public static String quote(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "") + "\"";
    }

    public static String bool(boolean value) {
        return value ? "true" : "false";
    }

    private static String unquote(String value) {
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed.replace("\\\"", "\"").replace("\\n", "\n").replace("\\\\", "\\");
    }
}
