package com.codeoftheweb.salvo.Controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameController {

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private GamePlayerRepository gamePlayerRepository;

  @Autowired
  private ShipRepository shipRepository;

  @Autowired
  private SalvoRepository salvoRepository;

  @RequestMapping("/games")
  public Map<String, Object> getGameAll(Authentication authentication) {
    GamePlayer gamePlayer1;
    Map<String, Object> dto = new LinkedHashMap<>();
    if (Util.isGuest(authentication)) {
      dto.put("player", "Guest");
    } else {
      dto.put("player", playerRepository.findByUserName(authentication.getName())
              .makePlayerDTO());
    }
    dto.put("games", gameRepository.findAll()
            .stream()
            .map(game -> game.makeGameDTO())
            .collect(Collectors.toList()));
    return dto;
    // GamePlayer gamePlayer;
    //return gameRepository.findAll()
    //        .stream()
    //        .map(game -> game.makeGameDTO())
    //        .collect(Collectors.toList());
  }

  @RequestMapping(path = "/games", method = RequestMethod.POST)
  public ResponseEntity<Object> createGame(Authentication authentication) {
    if (Util.isGuest(authentication)) {
      return new ResponseEntity<>("No autorizado", HttpStatus.UNAUTHORIZED);
    }

    Player player = playerRepository.findByUserName(authentication.getName());
    if (player == null) {
      return new ResponseEntity<>("No autorizado", HttpStatus.UNAUTHORIZED);
    }
    Game game = gameRepository.save(new Game());

    GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(player, game));

    return new ResponseEntity<>(Util.makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
  }

  @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
  public ResponseEntity<Object> joinGame(@PathVariable long gameId, Authentication authentication) {
    Game game = gameRepository.findById(gameId).get();

    if (Util.isGuest(authentication)) {
      return new ResponseEntity<>("No autorizado", HttpStatus.UNAUTHORIZED);
    }

    Player player = playerRepository.findByUserName(authentication.getName());
    if (player == null) {
      return new ResponseEntity<>("No autorizado", HttpStatus.UNAUTHORIZED);
    }
    if (game == null) {
      return new ResponseEntity<>("No such game", HttpStatus.UNAUTHORIZED);
    }

    if (game.getGamePlayers().size()==2) {
      return new ResponseEntity<>("Game is full", HttpStatus.FORBIDDEN);
    }

    GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(player, game));

    return new ResponseEntity<>(Util.makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
  }

  @RequestMapping(path = "/games/players/{gpId}/ships", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> addShip(@PathVariable long gpId, @RequestBody List<Ship> ships, Authentication authentication) {

    GamePlayer gamePlayer = gamePlayerRepository.findById(gpId).get();
    if (Util.isGuest(authentication)) {
      return new ResponseEntity<>(Util.makeMap("error", "there is no current user logged in"), HttpStatus.UNAUTHORIZED);
    }

    if (gamePlayer == null) {
      return new ResponseEntity<>(Util.makeMap("error", "there is no game player with the given ID"), HttpStatus.UNAUTHORIZED);
    }

    if (authentication.getName() != gamePlayer.getPlayer().getUserName()) {
      return new ResponseEntity<>(Util.makeMap("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
    }

    if (gamePlayer.getShips().stream().count() >= 5) {
      return new ResponseEntity<>(Util.makeMap("error", "Already ships has placed"), HttpStatus.FORBIDDEN);
    }
    ships.stream().forEach(ship -> ship.setGamePlayer(gamePlayer));
    shipRepository.saveAll(ships);
    return new ResponseEntity<>(Util.makeMap("ships", "added ok"), HttpStatus.CREATED);
  }

  @RequestMapping(path = "/games/players/{gpId}/salvoes", method = RequestMethod.POST)
  public ResponseEntity<Map<String,Object>> addSalvoes(@PathVariable long gpId, @RequestBody Salvo salvo, Authentication authentication) {

    GamePlayer gamePlayer = gamePlayerRepository.findById(gpId).get();
    Player player = playerRepository.findByUserName(authentication.getName());

    if (Util.isGuest(authentication)) {
      return new ResponseEntity<>(Util.makeMap("error","there is no current user logged in"), HttpStatus.UNAUTHORIZED);
    }

    if (gamePlayer==null) {
      return new ResponseEntity<>(Util.makeMap("Error","there is no game player with the given ID"), HttpStatus.UNAUTHORIZED);
    }
    if (gamePlayer.getPlayer().getId()!=player.getId()) {
      return new ResponseEntity<>(Util.makeMap("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
    }


    if (gamePlayer.getSalvoes().stream().filter(salvo1 -> salvo1.getTurn() == salvo.getTurn()).count() > 0 ) {
      return new ResponseEntity<>(Util.makeMap("Error", "Ya colocaste tus salvos en este turno"), HttpStatus.FORBIDDEN);
    }

    salvo.setGamePlayer(gamePlayer);
    salvoRepository.save(salvo);
    return new ResponseEntity<>(Util.makeMap("Salvos","Salvos added"), HttpStatus.CREATED);
  }

}
