package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
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
  PasswordEncoder passwordEncoder;

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

  @RequestMapping(path = "/players", method = RequestMethod.POST)
  public ResponseEntity<Object> register(@RequestParam String email, @RequestParam String password) {

    if (email.isEmpty() || password.isEmpty()) {
      return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
    }

    if (playerRepository.findByUserName(email) != null) {
      return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
    }

    playerRepository.save(new Player(email, passwordEncoder.encode(password)));
    return new ResponseEntity<>(HttpStatus.CREATED);
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


  @RequestMapping("/game_view/{gamePlayer_id}")
  public ResponseEntity<Map<String,Object>> getGamePlayerAll(@PathVariable Long gamePlayer_id,Authentication authentication) {

    if(Util.isGuest(authentication)){
      return new ResponseEntity<>(Util.makeMap("error","paso algo"), HttpStatus.UNAUTHORIZED);    }

    Player playerLogued= playerRepository.findByUserName(authentication.getName());
    GamePlayer gamePlayer2=gamePlayerRepository.findById(gamePlayer_id).get();

    if(playerLogued==null){
      return new ResponseEntity<>(Util.makeMap("error","paso algo"), HttpStatus.UNAUTHORIZED);
    }

    if(gamePlayer2==null){
      return new ResponseEntity<>(Util.makeMap("error","paso algo"), HttpStatus.UNAUTHORIZED);
    }

    if(gamePlayer2.getPlayer().getId()!=playerLogued.getId()){
      return new ResponseEntity<>(Util.makeMap("error","paso algo"), HttpStatus.UNAUTHORIZED);
    }

    GamePlayer gamePlayer;
    Game game2;
    Salvo salvo1;
    gamePlayer = gamePlayerRepository.findById(gamePlayer_id).get();
    game2 = gamePlayer.getGame();

    Map <String,Object> hits= new LinkedHashMap<>();
    Map<String, Object> gameData = game2.makeGameDTO();
    gameData.put("ships", gamePlayer.getShips()
            .stream()
            .map(ship -> ship.makeShipDTO())
            .collect(Collectors.toList()));
    gameData.put("salvoes", gamePlayer.getGame().getGamePlayers()
            .stream()
            .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes().stream().map(salvo -> salvo.makeSalvoDTO()))
            .collect(Collectors.toList()));
    hits.put("self",new ArrayList<>());
    hits.put("opponent",new ArrayList<>());
    gameData.put("hits",hits);

    return new ResponseEntity<>(gameData, HttpStatus.OK);
  }
  // private Player getPlayerAuth(Authentication authentication){
  // return playerRepository.findByUserName(authentication.getName());
  //}
}
