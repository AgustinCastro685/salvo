package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import sun.plugin2.message.GetAuthenticationReplyMessage;

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
            .filter(gamePlayer -> gamePlayer.getId() != this.getId())
            .findFirst()
            .orElse(new GamePlayer());
  }

  public Map<String, Object> makeHitsDTO(GamePlayer gamePlayer) {
    Map<String, Object> dto = new LinkedHashMap<>();
    Map<String, Object> dto1 = new LinkedHashMap<>();
    //List<Object> dto1= new ArrayList<>(gamePlayer.getSalvoes());

    for (Salvo s : salvoes) {
      dto.put("turn", s.getTurn());
      dto.put("hitLocation",this.getHitLocations(gamePlayer));


      dto.put("Damages", dto1);
    }

    return dto;
  }


  public List<Object> getHitLocations(GamePlayer gamePlayer){
    public List<Object> getHitsLocation(GamePlayer gamePlayer){
      return gamePlayer.getShips()
              .stream()
              .flatMap(ship -> ship.getShipLocations()
                      .stream()
                      .flatMap(shipLocation -> gamePlayer
                              .getOpponent()
                              .getSalvoes()
                              .stream()
                              .flatMap(salvo -> salvo
                                      .getSalvoLocations()
                                      .stream()
                                      .filter(salvoLoc-> shipLocation.contains(salvoLoc)))))
              .collect(Collectors.toList());
    }
  }

   public Map <String,Object> getShipByTipe(GamePlayer gamePlayer){
    Map <String,Object> dto1= new LinkedHashMap<>();

     dto1.put("carrierHits", gamePlayer.getShips().stream().filter(ship -> ship.getType().equals("carrier").getHits(gamePlayer)));
     dto1.put("battleshipHits", this.getHits("battleship", gamePlayer));
     dto1.put("submarineHits", this.getHits("submarine", gamePlayer));
     dto1.put("destroyerHits", this.getHits("destroyer", gamePlayer));
     dto1.put("patrolboatsHits", this.getHits("patrolboat", gamePlayer));
     //dto1.put("carrier", this.getHits("carrier", gamePlayer));
     //dto1.put("battleship", this.getHits("carrier", gamePlayer));
     //dto1.put("submarine", this.getHits("carrier", gamePlayer));
     //dto1.put("destroyer", this.getHits("carrier", gamePlayer));
     //dto1.put("patrolboat", this.getHits("carrier", gamePlayer));
   }

   public int countHits(GamePlayer gamePlayer){
    int count=0;


    return count;
   }

   public Ship getShipByType(String type){
    Ship ship;
    ship=
   }

  public int getHits(String type, GamePlayer gamePlayer) {
    int damages = 0;
    Ship shipEnco=null;
    List<Object> aaa = new ArrayList<>();
    //if(gamePlayer.getShips().contains(gamePlayer.getOpponent().getSalvoes())){
    for(Ship s:ships){
      if(s.getType().equals(type)){
        shipEnco=s;
      }
    }
    damages= gamePlayer.getOpponent().getSalvoes()
            .stream()
            .

    return damages;
  }
}
