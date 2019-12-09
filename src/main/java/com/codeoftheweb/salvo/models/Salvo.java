package com.codeoftheweb.salvo.models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
public class Salvo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private long id;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "gameplayer_id")
  private GamePlayer gamePlayer;


  private int turn = 0;

  @ElementCollection
  @Column(name = "Locations")
  private List<String> salvoLocations;


  public Salvo() {
  }

  public Salvo(long id, int numTurno, List<String> salvoLocations, GamePlayer gamePlayer) {
    this.id = id;
    this.turn = numTurno;
    this.salvoLocations = salvoLocations;
    this.gamePlayer = gamePlayer;
  }


  public Map<String, Object> makeSalvoDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("turn", this.getTurn());
    dto.put("player", this.getGamePlayer().getId());
    dto.put("locations", this.getSalvoLocations());
    return dto;

  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getTurn() {
    return turn;
  }

  public void setTurn(int numTurno) {
    this.turn = numTurno;
  }

  public List<String> getSalvoLocations() {
    return salvoLocations;
  }

  public void setSalvoLocations(List<String> salvoLocations) {
    this.salvoLocations = salvoLocations;
  }

  public GamePlayer getGamePlayer() {
    return gamePlayer;
  }

  public void setGamePlayer(GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
  }

  public long countHits(Ship ship) {
    if (ship.getType().equals(null)) {
      return 0;
    } else {
      return this.getSalvoLocations()
              .stream()
              .filter(salvoLocs -> ship.getShipLocations().contains(salvoLocs))
              .count();
    }
  }
}