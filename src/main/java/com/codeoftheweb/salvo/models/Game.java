package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Game {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")

  private long id;

  private Date creationDate;

  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  private Set<GamePlayer> gamePlayers;

  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  private Set<Score> scores;


  public Game() {
    this.creationDate = new Date();
    this.scores = scores;
  }

  public Game(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Map<String, Object> makeGameDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", this.getId());
    dto.put("created", this.getCreationDate());
    dto.put("gameState","PLACESHIPS");
    dto.put("gamePlayers", this.getGamePlayers()
            .stream()
            .map(gamePlayer -> gamePlayer.makeGamePlayerDTO())
            .collect(Collectors.toList()));
    dto.put("scores",this.getScores()
            .stream()
            .map(score -> score.makeScoreDTO())
            .collect(Collectors.toList()));
    return dto;
  }

  public Set<Score> getScores() {
    return scores;
  }

  public void setScores(Set<Score> scores) {
    this.scores = scores;
  }

  public Set<GamePlayer> getGamePlayers() {
    return gamePlayers;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void getGamePlayers(Set<GamePlayer> gamePlayers) {
    this.gamePlayers = gamePlayers;
  }
}

