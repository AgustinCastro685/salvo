package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

  @Autowired
  private GamePlayerRepository gamePlayerRepository;

  @Autowired
  private GameRepository gameRepository;

  @RequestMapping("/games")
  public List<Map<String, Object>> getGameAll() {

    return gameRepository.findAll()
            .stream()
            .map(game -> game.makeGameDTO())
            .collect(Collectors.toList());

  }

  @RequestMapping("/game_view/{gamePlayer_id}")
  public Map<String, Object> getGamePlayerAll(@PathVariable Long gamePlayer_id) {

    GamePlayer gamePlayer;
    Game game2;
    gamePlayer = gamePlayerRepository.findById(gamePlayer_id).get();
    game2 = gamePlayer.getGame();
    Map<String, Object> gameData = game2.makeGameDTO();
    gameData.put("ships", gamePlayer.getShips()
            .stream()
            .map(ship -> ship.makeShipDTO())
            .collect(Collectors.toList()));
    return gameData;
  }


}

