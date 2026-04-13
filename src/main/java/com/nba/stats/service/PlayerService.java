package com.nba.stats.service;

import com.nba.stats.dto.PlayerDTO;
import com.nba.stats.dto.PlayerStatsDTO;
import com.nba.stats.model.Player;
import com.nba.stats.repository.PlayerRepository;
import com.nba.stats.repository.PlayerSeasonStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerSeasonStatsRepository statsRepository;

    public PlayerService(PlayerRepository playerRepository, PlayerSeasonStatsRepository statsRepository) {
        this.playerRepository = playerRepository;
        this.statsRepository = statsRepository;
    }

    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream().map(PlayerDTO::from).toList();
    }

    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public Optional<PlayerDTO> getPlayerDTOById(Long id) {
        return playerRepository.findById(id).map(PlayerDTO::from);
    }

    public List<PlayerDTO> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId).stream().map(PlayerDTO::from).toList();
    }

    public List<PlayerDTO> searchPlayers(String name) {
        return playerRepository.searchByName(name).stream().map(PlayerDTO::from).toList();
    }

    public List<PlayerStatsDTO> getPlayerStats(Long playerId) {
        return statsRepository.findByPlayerId(playerId).stream().map(PlayerStatsDTO::from).toList();
    }

    public List<PlayerStatsDTO> getTopScorers(String season, int limit) {
        return statsRepository.findTopScorersBySeason(season)
                .stream().limit(limit).map(PlayerStatsDTO::from).toList();
    }

    public List<PlayerStatsDTO> getTopRebounders(String season, int limit) {
        return statsRepository.findTopReboundersBySeason(season)
                .stream().limit(limit).map(PlayerStatsDTO::from).toList();
    }

    public List<PlayerStatsDTO> getTopAssists(String season, int limit) {
        return statsRepository.findTopAssistsBySeason(season)
                .stream().limit(limit).map(PlayerStatsDTO::from).toList();
    }
}
