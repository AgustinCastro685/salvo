package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")

  private long id;

  private Date joinDate;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "player_id")
  private Player player;

  @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
  private Set<Ship> ships;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "game_id")
  private Game game;

  @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
  private List<Salvo> salvoes;

  private int missed = 5;

  public GamePlayer(Date date, Player player, Game game) {
    this.joinDate = new Date();
    this.player = player;
    this.game = game;
  }

  public GamePlayer() {
    this.joinDate = new Date();
  }

  public GamePlayer(Date date, Game g1, Player p1) {
  }

  public Set<Ship> getShips() {
    return ships;
  }

  public Map<String, Object> makeGamePlayerDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", this.getId());
    dto.put("player", this.getPlayer().makePlayerDTO());
    return dto;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Date getJoinDate() {
    return joinDate;
  }

  public void setJoinDate(Date joinDate) {
    this.joinDate = joinDate;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public void setShips(Set<Ship> ships) {
    this.ships = ships;
  }

  public List<Salvo> getSalvoes() {
    return salvoes;
  }

  public void setSalvoes(List<Salvo> salvoes) {
    this.salvoes = salvoes;
  }

  public Score getScore() {
    return this.player.getScore(this.getGame());
  }

  public GamePlayer(Player player, Game game) {
    this.player = player;
    this.game = game;
  }

  public GamePlayer getOpponent() {
    return this.getGame().getGamePlayers().stream()
            .filter(opponent -> this.getId() != opponent.getId())
            .findFirst()
            .orElse(null);
  }

  public List<Map<String, Object>> makeHitsDTO(GamePlayer gamePlayer) {

    List<Map<String, Object>> dtoLista = new ArrayList<>();
    Map<String, Object> dto = new LinkedHashMap<>();

    for (Salvo s : gamePlayer.getOpponent().getSalvoes()) {
      dto.put("turn", s.getTurn());
      dto.put("hitLocations", this.getHitLocations(gamePlayer, s));
      dto.put("damages", this.getShipByTipe(gamePlayer, s));
      dto.put("missed", missed);
      dtoLista.add(dto);
    }
    return dtoLista;
  }

  public int carrier = 0;
  public int battleship = 0;
  public int submarine = 0;
  public int destroyer = 0;
  public int patrolboat = 0;

  public List<String> getHitLocations(GamePlayer gamePlayer, Salvo salvoOpp) {
    return gamePlayer.getShips()
            .stream()
            .flatMap(ship -> ship.getShipLocations()
                    .stream()
                    .flatMap(shiploc -> salvoOpp
                            .getSalvoLocations()
                            .stream()
                            .filter(salvoLoc -> shiploc.contains(salvoLoc))))
            .collect(Collectors.toList());
  }


  public Map<String, Object> getShipByTipe(GamePlayer gamePlayer, Salvo s) {
    Map<String, Object> dto1 = new LinkedHashMap<>();

    int carrierHits = this.countHits(gamePlayer.getShips().stream().filter(ship -> ship.getType() == "carrier").findFirst().orElse(new Ship()), gamePlayer, s);
    int battleshipHits = this.countHits(gamePlayer.getShips().stream().filter(ship -> ship.getType() == "battleship").findFirst().orElse(new Ship()), gamePlayer, s);
    int submarineHits = this.countHits(gamePlayer.getShips().stream().filter(ship -> ship.getType() == "submarine").findFirst().orElse(new Ship()), gamePlayer, s);
    int destroyerHits = this.countHits(gamePlayer.getShips().stream().filter(ship -> ship.getType() == "destroyer").findFirst().orElse(new Ship()), gamePlayer, s);
    int patrolboatHits = this.countHits(gamePlayer.getShips().stream().filter(ship -> ship.getType() == "patrolboat").findFirst().orElse(new Ship()), gamePlayer, s);

    dto1.put("carrierHits", carrierHits);
    dto1.put("battleshipHits", battleshipHits);
    dto1.put("submarineHits", submarineHits);
    dto1.put("destroyerHits", destroyerHits);
    dto1.put("patrolboatsHits", patrolboatHits);

    dto1.put("carrier", carrier += carrierHits);
    dto1.put("battleship", battleship += battleshipHits);
    dto1.put("submarine", submarine += submarineHits);
    dto1.put("destroyer", destroyer += destroyerHits);
    dto1.put("patrolboat", patrolboat += patrolboatHits);

    int x = (carrierHits + battleshipHits + submarineHits + destroyerHits + patrolboatHits);
    this.missed = 5;
    this.missed -= x;
    return dto1;
  }

  public int countHits(Ship ship, GamePlayer gamePlayer, Salvo s) {

    int totalHits = gamePlayer.getHitLocations(gamePlayer, s).size();
    int contador = 0;

    if (ship.getType() != null && totalHits != 0) {

      for (String locationShip : ship.getShipLocations()) {
        if (gamePlayer.getHitLocations(gamePlayer, s).contains(locationShip)) {
          contador++;
        }
      }
    }
    return contador;
  }


}