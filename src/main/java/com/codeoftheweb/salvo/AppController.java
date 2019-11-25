package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Salvo;
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
    Map<String,Object> dto= new LinkedHashMap<>();
    if(isGuest(authentication)){
      dto.put("player","Guest");
    }else{
    dto.put("player",playerRepository.findByUserName(authentication.getName())
            .makePlayerDTO());
    }
    dto.put("games",gameRepository.findAll()
            .stream()
            .map(game -> game.makeGameDTO())
            .collect(Collectors.toList()));
    return  dto;

    // GamePlayer gamePlayer;
    //return gameRepository.findAll()
    //        .stream()
    //        .map(game -> game.makeGameDTO())
    //        .collect(Collectors.toList());


  }

  @RequestMapping(path = "/players", method = RequestMethod.POST)
  public ResponseEntity<Object> register(
          @RequestParam String email, @RequestParam String password) {

    if (email.isEmpty() || password.isEmpty()) {
      return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
    }

    if (playerRepository.findByUserName(email) !=  null) {
      return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
    }

    playerRepository.save(new Player(email, passwordEncoder.encode(password)));
    return new ResponseEntity<>(HttpStatus.CREATED);
  }


  @RequestMapping("/game_view/{gamePlayer_id}")
  public Map<String, Object> getGamePlayerAll(@PathVariable Long gamePlayer_id) {

    GamePlayer gamePlayer;
    Game game2;
    Salvo salvo1;
    gamePlayer = gamePlayerRepository.findById(gamePlayer_id).get();
    game2 = gamePlayer.getGame();
    Map<String, Object> gameData = game2.makeGameDTO();
    gameData.put("ships", gamePlayer.getShips()
            .stream()
            .map(ship -> ship.makeShipDTO())
            .collect(Collectors.toList()));
    gameData.put("salvoes", gamePlayer.getGame().getGamePlayers()
            .stream()
            .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes().stream().map(salvo -> salvo.makeSalvoDTO()))
            .collect(Collectors.toList()));

    return gameData;
  }

 // private Player getPlayerAuth(Authentication authentication){
   // return playerRepository.findByUserName(authentication.getName());
  //}
  private boolean isGuest(Authentication authentication) {
    return authentication == null || authentication instanceof AnonymousAuthenticationToken;
  }
}

