package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

  public static void main(String[] args) {
    SpringApplication.run(SalvoApplication.class, args);
  }

  @Bean
  public CommandLineRunner initData(PlayerRepository repository, GamePlayerRepository repositoryPG, GameRepository repositoryG, ShipRepository repositoryS, SalvoRepository repositorySalvo,ScoreRepository repositoryScore) {
    return (args) -> {
      // save a couple of customers
      Player player1 = new Player("malena@gmail.com");
      Player player2 = new Player("lucas@gmail.com");
      Player player3 = new Player("paola@gmail.com");
      Player player4 = new Player("aaa.com");

      repository.save(player1);
      repository.save(player2);
      repository.save(player3);
      repository.save(player4);


      Game game1 = new Game();
      Game game2 = new Game();

      repositoryG.save(game1);
      repositoryG.save(game2);


      Date date = new Date();
      Date date1 = new Date();

      GamePlayer gamePlayer1 = new GamePlayer(date,player1, game1);
      GamePlayer gamePlayer2 = new GamePlayer(date1,player2, game1);
      GamePlayer gameplayer3 = new GamePlayer(date ,player3, game2);
      GamePlayer gameplayer4 = new GamePlayer(date1, player4, game2);

      repositoryPG.save(gamePlayer1);
      repositoryPG.save(gamePlayer2);
      repositoryPG.save(gameplayer3);
      repositoryPG.save(gameplayer4);


      List<String> shipLocation1 = new LinkedList<>();
      shipLocation1.add("B1");
      shipLocation1.add("B2");
      shipLocation1.add("B3");

      List<String> shipLocation2 = new LinkedList<>();
      shipLocation2.add("D7");
      shipLocation2.add("D8");
      shipLocation2.add("D9");


      Ship ship1 = new Ship("Acorazado", gamePlayer1, shipLocation1);
      Ship ship2 = new Ship("Buque", gamePlayer2, shipLocation2);

      repositoryS.save(ship1);
      repositoryS.save(ship2);


      List<String> salvoLocation1 = new LinkedList<>();
      salvoLocation1.add("B3");
      salvoLocation1.add("E5");
      salvoLocation1.add("D7");


      List<String> salvoLocation2 = new LinkedList<>();
      salvoLocation2.add("E3");
      salvoLocation2.add("G5");
      salvoLocation2.add("C4");
      Salvo salvo1 = new Salvo();
      Salvo salvo2 = new Salvo();

      salvo1.setSalvoLocations(salvoLocation1);
      salvo1.setTurn(1);
      salvo1.setGamePlayer(gamePlayer1);

      salvo2.setSalvoLocations(salvoLocation2);
      salvo2.setTurn(2);
      salvo2.setGamePlayer(gamePlayer2);

      repositorySalvo.save(salvo1);
      repositorySalvo.save(salvo2);

      Date date3= new Date();
      Date date4= new Date();

      Score score1= new Score(game1,player1,1.0,date3);
      Score score2= new Score(game1,player2,0.5,date4);
      Score score3= new Score(game2,player3,0.0,date3);
      Score score4= new Score(game2,player4,1.0,date4);

      repositoryScore.save(score1);
      repositoryScore.save(score2);
      repositoryScore.save(score3);
      repositoryScore.save(score4);







    };
  }
}
