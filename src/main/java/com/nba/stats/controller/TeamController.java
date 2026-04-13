package com.nba.stats.controller;

import com.nba.stats.dto.PlayerDTO;
import com.nba.stats.model.Team;
import com.nba.stats.service.PlayerService;
import com.nba.stats.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final PlayerService playerService;

    public TeamController(TeamService teamService, PlayerService playerService) {
        this.teamService = teamService;
        this.playerService = playerService;
    }

    @GetMapping
    public List<Team> getAllTeams(
            @RequestParam(required = false) String conference,
            @RequestParam(required = false) String division) {
        if (conference != null) return teamService.getTeamsByConference(conference);
        if (division != null) return teamService.getTeamsByDivision(division);
        return teamService.getAllTeams();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeam(@PathVariable Long id) {
        return teamService.getTeamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/abbreviation/{abbr}")
    public ResponseEntity<Team> getTeamByAbbreviation(@PathVariable String abbr) {
        return teamService.getTeamByAbbreviation(abbr)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/players")
    public ResponseEntity<List<PlayerDTO>> getTeamPlayers(@PathVariable Long id) {
        return teamService.getTeamById(id)
                .map(team -> ResponseEntity.ok(playerService.getPlayersByTeam(id)))
                .orElse(ResponseEntity.notFound().build());
    }
}
