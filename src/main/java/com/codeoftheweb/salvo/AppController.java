package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController{

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
}