package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository repository, GamePlayerRepository repositoryPG,GameRepository repositoryG) {
		return (args) -> {
			// save a couple of customers
			Player player1=new Player("malena@gmail.com");
			Player player2=new Player("lucas@gmail.com");
			Player player3=new Player("paola@gmail.com");
			Player player4=new Player("aaa.com");

			Game game1=new Game();
			Game game2=new Game();

			Date date= new Date();
			Date date1=new Date();

			GamePlayer gamePlayer1=new GamePlayer(player3,game1);
			GamePlayer gamePlayer2=new GamePlayer(player2,game1);
			GamePlayer gameplayer3=new GamePlayer(player3,game2);
			GamePlayer gameplayer4=new GamePlayer(player4,game2);

			repository.save(player1);
			repository.save(player2);
			repository.save(player3);
			repository.save(player4);

			repositoryG.save(game1);
			repositoryG.save(game2);

			repositoryPG.save(gamePlayer1);
      repositoryPG.save(gamePlayer2);

      repositoryPG.save(gameplayer3);
      repositoryPG.save(gameplayer4);

		};
	}
}
