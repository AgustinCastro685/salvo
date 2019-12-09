package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
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
  private Set<Salvo> salvoes;

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

  public Set<Salvo> getSalvoes() {
    return salvoes;
  }

  public void setSalvoes(Set<Salvo> salvoes) {
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


  public Set<Map<String, Object>> makeHitsDTO() {

    Set<Map<String, Object>> dtoLista = new LinkedHashSet<>();
    List<Salvo> salvoes2 = this.getOpponent().getSalvoes().stream().sorted(Comparator.comparing(Salvo::getTurn)).collect(Collectors.toList());

    for (Salvo s : salvoes2) {
      Map<String, Object> dto = new LinkedHashMap<>();

      dto.put("turn", s.getTurn());
      dto.put("hitLocations", this.getHitLocations(s));
      dto.put("damages", this.getShipByTipe(s));
      dto.put("missed", this.hitMissed(s));
      dtoLista.add(dto);
    }
    return dtoLista;
  }


  public List<String> getHitLocations(Salvo s) {
    return this.getShips()
            .stream()
            .flatMap(ship -> ship.getShipLocations()
                    .stream()
                    .flatMap(shiploc -> s
                            .getSalvoLocations()
                            .stream()
                            .filter(salvoLoc -> shiploc.equals(salvoLoc))))
            .collect(Collectors.toList());
  }


  public Map<String, Object> getShipByTipe(Salvo s) {
    Map<String, Object> dto1 = new LinkedHashMap<>();

    dto1.put("carrierHits", s.countHits(this.getShips().stream().filter(ship -> ship.getType().equals("carrier")).findFirst().orElse(new Ship())));
    dto1.put("battleshipHits", s.countHits(this.getShips().stream().filter(ship -> ship.getType().equals("battleship")).findFirst().orElse(new Ship())));
    dto1.put("submarineHits", s.countHits(this.getShips().stream().filter(ship -> ship.getType().equals("submarine")).findFirst().orElse(new Ship())));
    dto1.put("destroyerHits", s.countHits(this.getShips().stream().filter(ship -> ship.getType().equals("destroyer")).findFirst().orElse(new Ship())));
    dto1.put("patrolboatHits", s.countHits(this.getShips().stream().filter(ship -> ship.getType().equals("patrolboat")).findFirst().orElse(new Ship())));


    List<Salvo> salvoOpponent = new ArrayList<>(this.getOpponent().getSalvoes());

    dto1.put("carrier", salvoOpponent
            .stream().map(salvo -> salvo.countHits(this.getShips().stream().filter(ship -> ship.getType().equals("carrier")).findFirst().orElse(new Ship()))).reduce(Long::sum).get());

    dto1.put("battleship", salvoOpponent
            .stream().map(salvo -> salvo.countHits(this.getShips().stream().filter(ship -> ship.getType().equals("battleship")).findFirst().orElse(new Ship()))).reduce(Long::sum).get());

    dto1.put("submarine", salvoOpponent
            .stream().map(salvo -> salvo.countHits(this.getShips().stream().filter(ship -> ship.getType().equals("submarine")).findFirst().orElse(new Ship()))).reduce(Long::sum).get());

    dto1.put("destroyer", salvoOpponent
            .stream().map(salvo -> salvo.countHits(this.getShips().stream().filter(ship -> ship.getType().equals("destroyer")).findFirst().orElse(new Ship()))).reduce(Long::sum).get());

    dto1.put("patrolboat", salvoOpponent
            .stream().map(salvo -> salvo.countHits(this.getShips().stream().filter(ship -> ship.getType().equals("patrolboat")).findFirst().orElse(new Ship()))).reduce(Long::sum).get());

    return dto1;
  }

  public long hitMissed(Salvo s) {
    long missed = 5 - this.getHitLocations(s).stream().count();
    return missed;
  }

  public long totalHits() {
    List<Long> totalHits = new ArrayList<>();
    List<Salvo> salvoOpponent = new ArrayList<>(this.getOpponent().getSalvoes());

    totalHits.add(salvoOpponent.stream().map(salvo -> salvo.countHits((this.getShips().stream().filter(ship -> ship.getType().equals("carrier")).findFirst().orElse(new Ship())))).mapToLong(x -> x).sum());//reduce(Long::sum).get());
    totalHits.add(salvoOpponent.stream().map(salvo -> salvo.countHits((this.getShips().stream().filter(ship -> ship.getType().equals("battleship")).findFirst().orElse(new Ship())))).mapToLong(x -> x).sum());//reduce(Long::sum).get());
    totalHits.add(salvoOpponent.stream().map(salvo -> salvo.countHits((this.getShips().stream().filter(ship -> ship.getType().equals("submarine")).findFirst().orElse(new Ship())))).mapToLong(x -> x).sum());//reduce(Long::sum).get());
    totalHits.add(salvoOpponent.stream().map(salvo -> salvo.countHits((this.getShips().stream().filter(ship -> ship.getType().equals("destroyer")).findFirst().orElse(new Ship())))).mapToLong(x -> x).sum());//reduce(Long::sum).get());
    totalHits.add(salvoOpponent.stream().map(salvo -> salvo.countHits((this.getShips().stream().filter(ship -> ship.getType().equals("patrolboat")).findFirst().orElse(new Ship())))).mapToLong(x -> x).sum());//reduce(Long::sum).get());

    long resultado = totalHits.stream().reduce(Long::sum).get();
    return resultado;
  }

  public boolean playerLost() {
    boolean ok = false;

    if (this.getShips()
            .stream().mapToLong(ship -> ship.getShipLocations().size())
            .sum() == this.getOpponent().totalHits()) {
      ok = true;
    }
    return ok;
  }

}