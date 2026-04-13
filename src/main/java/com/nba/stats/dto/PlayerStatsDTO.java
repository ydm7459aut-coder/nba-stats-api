package com.nba.stats.dto;

import com.nba.stats.model.PlayerSeasonStats;

public record PlayerStatsDTO(
        Long id,
        Long playerId,
        String playerName,
        String teamName,
        String teamAbbreviation,
        String season,
        Integer gamesPlayed,
        Double pointsPerGame,
        Double reboundsPerGame,
        Double assistsPerGame,
        Double stealsPerGame,
        Double blocksPerGame,
        Double fieldGoalPct,
        Double threePointPct,
        Double freeThrowPct,
        Double minutesPerGame
) {
    public static PlayerStatsDTO from(PlayerSeasonStats stats) {
        var player = stats.getPlayer();
        var team = player != null ? player.getTeam() : null;
        return new PlayerStatsDTO(
                stats.getId(),
                player != null ? player.getId() : null,
                player != null ? player.getFullName() : null,
                team != null ? team.getName() : null,
                team != null ? team.getAbbreviation() : null,
                stats.getSeason(),
                stats.getGamesPlayed(),
                stats.getPointsPerGame(),
                stats.getReboundsPerGame(),
                stats.getAssistsPerGame(),
                stats.getStealsPerGame(),
                stats.getBlocksPerGame(),
                stats.getFieldGoalPct(),
                stats.getThreePointPct(),
                stats.getFreeThrowPct(),
                stats.getMinutesPerGame()
        );
    }
}
