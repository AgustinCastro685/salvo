package com.codeoftheweb.salvo.models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private long id;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "gameplayer_id")
  private GamePlayer gamePlayer;


  private int numTurno = 0;

  @ElementCollection
  @Column(name = "Locations")
  private List<String> salvoLocations;


  public Salvo() {
  }

  public Salvo(long id, int numTurno, List<String> salvoLocations, GamePlayer gamePlayer) {
    this.id = id;
    this.numTurno = numTurno;
    this.salvoLocations = salvoLocations;
    this.gamePlayer = gamePlayer;
  }


  public Map<String, Object> makeSalvoDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("turno", this.getNumTurno());
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

  public int getNumTurno() {
    return numTurno;
  }

  public void setNumTurno(int numTurno) {
    this.numTurno = numTurno;
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
}
