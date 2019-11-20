package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class Ship {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")

  private long id;

  private String type;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "gamePlayer_id")
  private GamePlayer gamePlayer;

  @ElementCollection
  @Column(name="shipLocations")
  private List<String> shipLocations;

  public Ship(String type, GamePlayer gamePlayer, List<String> shipLocations) {
    this.type = type;
    this.gamePlayer = gamePlayer;
    this.shipLocations = shipLocations;
  }

  public Ship() {
  }

  public List<String> getShipLocations() {
    return shipLocations;
  }

  public Map<String, Object> makeShipDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("type", this.getType());
    dto.put("locations", this.getShipLocations());

    return dto;

  }

  public void setGamePlayer(GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
  }

  public GamePlayer getGamePlayer() {
    return gamePlayer;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


}