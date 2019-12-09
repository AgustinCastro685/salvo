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

  @Autowired
  private ScoreRepository scoreRepository;

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
    String state = getState(gamePlayer, gamePlayer.getOpponent());
    gameData.put("gameState", state);
    gameData.put("ships", gamePlayer.getShips()
            .stream()
            .map(ship -> ship.makeShipDTO())
            .collect(Collectors.toList()));
    gameData.put("salvoes", gamePlayer.getGame().getGamePlayers()
            .stream()
            .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes().stream().map(salvo -> salvo.makeSalvoDTO()))
            .collect(Collectors.toList()));
    if (Objects.nonNull(gamePlayer.getOpponent())) {
      GamePlayer opponent = gamePlayer.getOpponent();
      if (gamePlayer.getOpponent().getSalvoes().size() == 0) {
        hits.put("self", new ArrayList<>());
      } else {
        hits.put("self", gamePlayer.makeHitsDTO());
      }
      hits.put("opponent", opponent.makeHitsDTO());
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

    if (gamePlayerSelf.getSalvoes().size() == gamePlayerOpponent.getSalvoes().size()
            && gamePlayerSelf.getSalvoes().size() != 0
            && gamePlayerSelf.playerLost() == true
            && gamePlayerOpponent.playerLost() != true) {

      Date date = new Date();
      if (this.hayScore(gamePlayerSelf.getGame()) == true) {
        Score score = new Score(gamePlayerSelf.getGame(), gamePlayerSelf.getPlayer(), 1.0, date);
        Score score1= new Score(gamePlayerOpponent.getGame(),gamePlayerOpponent.getPlayer(),0.0,date);
        scoreRepository.save(score);
        scoreRepository.save(score1);

      }
      return "WON";
    }
    if (gamePlayerSelf.getSalvoes().size() == gamePlayerOpponent.getSalvoes().size()
            && gamePlayerSelf.playerLost() == true && gamePlayerOpponent.playerLost() == true
            && gamePlayerOpponent.getSalvoes().size() != 0 && gamePlayerSelf.getSalvoes().size() != 0) {

      Date date = new Date();

      if (this.hayScore(gamePlayerSelf.getGame()) == true) {
        Score score = new Score(gamePlayerSelf.getGame(), gamePlayerSelf.getPlayer(), 0.5, date);
        Score score1= new Score(gamePlayerOpponent.getGame(),gamePlayerOpponent.getPlayer(),0.5,date);
        scoreRepository.save(score);
        scoreRepository.save(score1);
      }
      return "TIE";
    }
    if (gamePlayerSelf.playerLost() == false && gamePlayerOpponent.playerLost() == true
            && gamePlayerSelf.getSalvoes().size() == gamePlayerOpponent.getSalvoes().size() && gamePlayerOpponent.getSalvoes().size() != 0 && gamePlayerSelf.getSalvoes().size() != 0) {
      //Date date = new Date();
      //if (this.hayScore(gamePlayerSelf.getGame()) == true){
      //  Score score = new Score(gamePlayerSelf.getGame(), gamePlayerSelf.getPlayer(), 0.0, date);
      //  Score score1= new Score(gamePlayerOpponent.getGame(),gamePlayerOpponent.getPlayer(),1.0,date);
      //  scoreRepository.save(score);
      //  scoreRepository.save(score1);
      //}
      return "LOST";
    }

    if (gamePlayerSelf.getId() < gamePlayerOpponent.getId()) {
      if (gamePlayerSelf.getSalvoes().size() > gamePlayerOpponent.getSalvoes().size()) {
        return "WAIT";
      } else if (gamePlayerSelf.getSalvoes().size() == gamePlayerOpponent.getSalvoes().size()) {
        return "PLAY";
      } else {
        return "WAIT";
      }
    }

    if (gamePlayerSelf.getId() > gamePlayerOpponent.getId()) {
      if (gamePlayerSelf.getSalvoes().size() < gamePlayerOpponent.getSalvoes().size()) {
        return "PLAY";
      } else if (gamePlayerSelf.getSalvoes().size() == gamePlayerOpponent.getSalvoes().size()) {
        if (gamePlayerSelf.getId() > gamePlayerOpponent.getId()) {
          return "WAIT";
        } else {
          return "PLAY";
        }
      }
    }
    return "LOST";
  }

  public boolean hayScore(Game game) {
    if (game.getScores().isEmpty()) {
      return true;
    } else {
      return false;
    }
  }


}
