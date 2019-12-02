package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.ArrayList;

@SpringBootApplication
public class SalvoApplication {

  public static void main(String[] args) {
    SpringApplication.run(SalvoApplication.class, args);
  }

  @Bean
  public PasswordEncoder passwordEncoder() { return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
  @Bean
  public CommandLineRunner initData(PlayerRepository repository, GamePlayerRepository repositoryPG, GameRepository repositoryG, ShipRepository repositoryS, SalvoRepository repositorySalvo,ScoreRepository repositoryScore) {
    return (args) -> {
      // save a couple of customers
      Player player1 = new Player("malena@gmail.com",passwordEncoder().encode("12345"));
      Player player2 = new Player("lucas@gmail.com",passwordEncoder().encode("11111"));
      Player player3 = new Player("paola@gmail.com",passwordEncoder().encode("22222"));
      Player player4 = new Player("aaa@baba.com",passwordEncoder().encode("33333"));
      Player player5= new Player("agustin@mail.com", passwordEncoder().encode("55555"));
      repository.save(player1);
      repository.save(player2);
      repository.save(player3);
      repository.save(player4);
      repository.save(player5);

      Game game1 = new Game();
      Game game2 = new Game();
      Game game3= new Game();
      repositoryG.save(game1);
      repositoryG.save(game2);
      repositoryG.save(game3);

      Date date = new Date();
      Date date1 = new Date();
      Date date2= new Date();

      GamePlayer gamePlayer1 = new GamePlayer(date,player1, game1);
      GamePlayer gamePlayer2 = new GamePlayer(date1,player2, game1);
      GamePlayer gameplayer3 = new GamePlayer(date ,player3, game2);
      GamePlayer gameplayer4 = new GamePlayer(date1, player4, game2);
      GamePlayer gamePlayer5= new GamePlayer(date2,player5,game3);

      repositoryPG.save(gamePlayer1);
      repositoryPG.save(gamePlayer2);
      repositoryPG.save(gameplayer3);
      repositoryPG.save(gameplayer4);
      repositoryPG.save(gamePlayer5);

      List<String> shipLocation1 = new LinkedList<>();
      shipLocation1.add("B1");
      shipLocation1.add("B2");
      shipLocation1.add("B3");

      List<String> shipLocation2 = new LinkedList<>();
      shipLocation2.add("D7");
      shipLocation2.add("D8");
      shipLocation2.add("D9");


      Ship ship1 = new Ship("destroyer", gamePlayer1, shipLocation1);
      Ship ship2 = new Ship("battleship", gamePlayer2, shipLocation2);

      repositoryS.save(ship1);
      repositoryS.save(ship2);


      List<String> salvoLocation1 = new LinkedList<>();
      salvoLocation1.add("B3");
      salvoLocation1.add("E5");
      salvoLocation1.add("D7");
      salvoLocation1.add("D5");
      salvoLocation1.add("D6");


      List<String> salvoLocation2 = new LinkedList<>();
      salvoLocation2.add("E3");
      salvoLocation2.add("G5");
      salvoLocation2.add("C4");
      salvoLocation2.add("C7");
      salvoLocation2.add("C5");
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
  @Configuration
  class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(inputName-> {
        Player player = playerRepository.findByUserName(inputName);
        if (player != null) {
          return new User(player.getUserName(),player.getPassword(),
                  AuthorityUtils.createAuthorityList("USER"));
        } else {
          throw new UsernameNotFoundException("Unknown user: " + inputName);
        }
      });
    }
  }

  @EnableWebSecurity
  @Configuration
  class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
            .antMatchers("/api/game_view/*","/web/game.html?gp=*").hasAuthority("USER")
            .antMatchers("/web/**").permitAll()
            .antMatchers("/api/**").permitAll()
            .antMatchers("/rest").denyAll()
            .anyRequest().denyAll();
    http.formLogin()
            .usernameParameter("name")
            .passwordParameter("pwd")
            .loginPage("/api/login");
    http.logout()
            .logoutUrl("/api/logout");
    // turn off checking for CSRF tokens
    http.csrf().disable();

    // if user is not authenticated, just send an authentication failure response
    http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

    // if login is successful, just clear the flags asking for authentication
    http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

    // if login fails, just send an authentication failure response
    http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

    // if logout is successful, just send a success response
    http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
  }
    private void clearAuthenticationAttributes(HttpServletRequest request) {
      HttpSession session = request.getSession(false);
      if (session != null) {
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
      }
  }
  }
