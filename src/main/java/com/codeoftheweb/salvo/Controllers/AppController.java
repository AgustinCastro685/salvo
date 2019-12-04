package com.codeoftheweb.salvo.Controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

  @Autowired
  private GamePlayerRepository gamePlayerRepository;

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private PlayerRepository playerRepository;


  @Autowired
  private ShipRepository shipRepository;

  @Autowired
  private SalvoRepository salvoRepository;

  @RequestMapping("/game_view/{gamePlayer_id}")
  public ResponseEntity<Map<String, Object>> getGamePlayerAll(@PathVariable Long gamePlayer_id, Authentication authentication) {

    if (Util.isGuest(authentication)) {
      return new ResponseEntity<>(Util.makeMap("error", "paso algo"), HttpStatus.UNAUTHORIZED);
    }

    Player playerLogued = playerRepository.findByUserName(authentication.getName());
    GamePlayer gamePlayer2 = gamePlayerRepository.findById(gamePlayer_id).get();

    if (playerLogued == null) {
      return new ResponseEntity<>(Util.makeMap("error", "paso algo"), HttpStatus.UNAUTHORIZED);
    }

    if (gamePlayer2 == null) {
      return new ResponseEntity<>(Util.makeMap("error", "paso algo"), HttpStatus.UNAUTHORIZED);
    }

    if (gamePlayer2.getPlayer().getId() != playerLogued.getId()) {
      return new ResponseEntity<>(Util.makeMap("error", "paso algo"), HttpStatus.UNAUTHORIZED);
    }

    GamePlayer gamePlayer;
    Game game2;
    Salvo salvo1;
    gamePlayer = gamePlayerRepository.findById(gamePlayer_id).get();
    game2 = gamePlayer.getGame();

    Map<String, Object> hits = new LinkedHashMap<>();
    Map<String, Object> gameData = game2.makeGameDTO();
    gameData.put("gameState", getState(gamePlayer, gamePlayer.getOpponent()));
    gameData.put("ships", gamePlayer.getShips()
            .stream()
            .map(ship -> ship.makeShipDTO())
            .collect(Collectors.toList()));
    gameData.put("salvoes", gamePlayer.getGame().getGamePlayers()
            .stream()
            .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes().stream().map(salvo -> salvo.makeSalvoDTO()))
            .collect(Collectors.toList()));
    if (Objects.nonNull(gamePlayer.getOpponent())) {
      hits.put("self", gamePlayer.makeHitsDTO(gamePlayer));
      hits.put("opponent", gamePlayer.makeHitsDTO(gamePlayer.getOpponent()));
    } else {
      hits.put("self", new ArrayList<>());
      hits.put("opponent", new ArrayList<>());
    }
    gameData.put("hits", hits);

    return new ResponseEntity<>(gameData, HttpStatus.OK);
  }

  private String getState(GamePlayer gamePlayerSelf, GamePlayer gamePlayerOpponent) {

    if (gamePlayerSelf.getShips().isEmpty()) {
      return "PLACESHIPS";
    }
    if (gamePlayerSelf.getGame().getGamePlayers().size() == 1) {
      return "WAITINGFOROPP";
    }
    if (gamePlayerSelf.getId() < gamePlayerOpponent.getId()) {
      return "PLAY";
    }
    if (gamePlayerSelf.getId() > gamePlayerOpponent.getId()) {
      return "WAIT";
    }if(gamePlayerSelf.countHitsWon()==gamePlayerOpponent.getShips().size()){
       return "WON";
    }
    if(gamePlayerSelf.countHitsWon()==gamePlayerOpponent.countHitsWon())//&& //tengo que fijarme si el turno es el mismo )
     {  return "TIE";}
    return "LOST";
  }
}
