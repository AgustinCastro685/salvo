package com.codeoftheweb.salvo.com.codeoftheweb.salvo.models;

import com.codeoftheweb.salvo.com.codeoftheweb.salvo.repository.PlayerRepository;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;

@Entity
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private long id;

  private String userName;

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserName() {
    return userName;
  }

  public Player() {
  }

  public Player(String userName) {
    this.userName = userName;
  }
}
