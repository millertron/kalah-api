package com.millertronics.kalahapi.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data repository for GameEntity
 */
@Repository
public interface GameRepository extends JpaRepository<GameEntity, Integer> {
}
