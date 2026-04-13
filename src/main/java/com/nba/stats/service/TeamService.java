package com.nba.stats.service;

import com.nba.stats.model.Team;
import com.nba.stats.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    public Optional<Team> getTeamByAbbreviation(String abbreviation) {
        return teamRepository.findByAbbreviation(abbreviation.toUpperCase());
    }

    public List<Team> getTeamsByConference(String conference) {
        return teamRepository.findByConference(conference);
    }

    public List<Team> getTeamsByDivision(String division) {
        return teamRepository.findByDivision(division);
    }
}
