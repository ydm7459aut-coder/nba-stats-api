package com.nba.stats.controller;

import com.nba.stats.dto.PlayerDTO;
import com.nba.stats.dto.PlayerStatsDTO;
import com.nba.stats.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerDTO> getAllPlayers(@RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return playerService.searchPlayers(search);
        }
        return playerService.getAllPlayers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable Long id) {
        return playerService.getPlayerDTOById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<List<PlayerStatsDTO>> getPlayerStats(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(player -> ResponseEntity.ok(playerService.getPlayerStats(id)))
                .orElse(ResponseEntity.notFound().build());
    }
}
