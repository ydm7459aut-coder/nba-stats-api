package com.nba.stats.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 3)
    private String abbreviation;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String conference;

    @Column(nullable = false)
    private String division;

    @JsonIgnore
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Player> players = new ArrayList<>();

    public Team() {}

    public Team(String name, String abbreviation, String city, String conference, String division) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.city = city;
        this.conference = conference;
        this.division = division;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAbbreviation() { return abbreviation; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getConference() { return conference; }
    public void setConference(String conference) { this.conference = conference; }
    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }
    public List<Player> getPlayers() { return players; }
}
