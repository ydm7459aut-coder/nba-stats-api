package com.nba.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nba.stats.repository.PlayerSeasonStatsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Value("${anthropic.api.key:}")
    private String apiKey;

    private final PlayerSeasonStatsRepository statsRepo;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();

    public AiController(PlayerSeasonStatsRepository statsRepo) {
        this.statsRepo = statsRepo;
    }

    @PostMapping("/ask")
    @Transactional(readOnly = true)
    public Map<String, String> ask(@RequestBody Map<String, String> body) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            return Map.of("error", "No Anthropic API key configured. Set ANTHROPIC_API_KEY environment variable.");
        }

        String question = body.getOrDefault("question", "").strip();
        if (question.isBlank()) return Map.of("error", "Empty question.");

        // Build stats context
        var sb = new StringBuilder("2023-24 NBA Season Player Stats:\n");
        statsRepo.findBySeason("2023-24").forEach(s -> {
            var p = s.getPlayer();
            var t = p.getTeam();
            sb.append(String.format("%-22s %-4s | %4.1f PPG %4.1f RPG %4.1f APG %3.1f SPG %3.1f BPG | FG %.1f%% 3P %.1f%% FT %.1f%% | %2d GP\n",
                p.getFullName(), t.getAbbreviation(),
                s.getPointsPerGame(), s.getReboundsPerGame(), s.getAssistsPerGame(),
                s.getStealsPerGame(), s.getBlocksPerGame(),
                s.getFieldGoalPct() * 100, s.getThreePointPct() * 100, s.getFreeThrowPct() * 100,
                s.getGamesPlayed()));
        });

        String systemPrompt = "You are a knowledgeable NBA stats analyst. Answer the user's question using the 2023-24 season data below. Be concise and conversational. No markdown headers.\n\n" + sb;

        // Build JSON explicitly to guarantee correct types
        ObjectNode req = mapper.createObjectNode();
        req.put("model", "claude-opus-4-5-20251101");
        req.put("max_tokens", 400);
        req.put("system", systemPrompt);

        ArrayNode messages = req.putArray("messages");
        ObjectNode msg = messages.addObject();
        msg.put("role", "user");
        msg.put("content", question);

        HttpRequest httpReq = HttpRequest.newBuilder()
            .uri(URI.create("https://api.anthropic.com/v1/messages"))
            .header("Content-Type", "application/json")
            .header("x-api-key", apiKey)
            .header("anthropic-version", "2023-06-01")
            .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(req)))
            .build();

        HttpResponse<String> resp = http.send(httpReq, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() == 200) {
            String text = mapper.readTree(resp.body()).path("content").get(0).path("text").asText();
            return Map.of("answer", text);
        }

        // Return actual error detail from Anthropic
        String detail = mapper.readTree(resp.body()).path("error").path("message").asText(resp.body());
        return Map.of("error", detail);
    }
}
