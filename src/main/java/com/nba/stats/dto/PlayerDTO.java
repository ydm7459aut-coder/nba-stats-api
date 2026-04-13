package com.nba.stats.dto;

import com.nba.stats.model.Player;

public record PlayerDTO(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String position,
        Integer jerseyNumber,
        Long teamId,
        String teamName,
        String teamCity,
        String teamAbbreviation
) {
    public static PlayerDTO from(Player player) {
        var team = player.getTeam();
        return new PlayerDTO(
                player.getId(),
                player.getFirstName(),
                player.getLastName(),
                player.getFullName(),
                player.getPosition(),
                player.getJerseyNumber(),
                team != null ? team.getId() : null,
                team != null ? team.getName() : null,
                team != null ? team.getCity() : null,
                team != null ? team.getAbbreviation() : null
        );
    }
}
