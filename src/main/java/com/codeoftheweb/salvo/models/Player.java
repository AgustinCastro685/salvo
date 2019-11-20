package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")

  private long id;

  @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
  private Set<GamePlayer> gamePlayers;
  private String userName;

  @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
  private Set<Score>scores ;

  public Player()
  {
    //this.scores = new Set<Score>scores();
   //     setScores();
  }

  public Player(String userName, Set<Score> scores) {
    this.userName = userName;
    this.scores = scores;
  }

  public Player(String userName) {
    this.userName = userName;

  }

  public Map<String, Object> makePlayerDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", this.getId());
    dto.put("email", this.getUserName());
    return dto;

  }

  public Set<Score> getScores() {
    return scores;
  }

  public void setScores(Set<Score> scores) {
    this.scores = scores;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Set<GamePlayer> getGamePlayers() {
    return gamePlayers;
  }

  public void setGamePlayers(Set<GamePlayer> gamePlayers) {
    this.gamePlayers = gamePlayers;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Score getScore(Game game){
    return this.getScores()
            .stream().filter(score -> score.getGame().getId()== game.getId())
            .findFirst().orElse(null);
  }

}
