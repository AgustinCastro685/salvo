package com.codeoftheweb.salvo.com.codeoftheweb.salvo.repository;

import java.util.List;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
  public interface PlayerRepository extends JpaRepository<Player, Long> {
  }


