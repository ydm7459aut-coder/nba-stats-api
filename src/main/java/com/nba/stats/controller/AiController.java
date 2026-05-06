package com.nba.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nba.stats.repository.PlayerSeasonStatsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
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

        // Build stats context from DB
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

        String system = "You are a knowledgeable NBA stats analyst. Answer the user's question using the provided 2023-24 season data. Be concise, factual, and conversational. Do not use markdown headers — plain text only.\n\n" + sb;

        String reqBody = mapper.writeValueAsString(Map.of(
            "model", "claude-sonnet-4-6",
            "max_tokens", 400,
            "system", system,
            "messages", List.of(Map.of("role", "user", "content", question))
        ));

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create("https://api.anthropic.com/v1/messages"))
            .header("Content-Type", "application/json")
            .header("x-api-key", apiKey)
            .header("anthropic-version", "2023-06-01")
            .POST(HttpRequest.BodyPublishers.ofString(reqBody))
            .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() == 200) {
            String text = mapper.readTree(resp.body()).path("content").get(0).path("text").asText();
            return Map.of("answer", text);
        }
        return Map.of("error", "AI returned status " + resp.statusCode());
    }
}
