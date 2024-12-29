package com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.TokenBlacklistEntity;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistEntity, Integer>{
	Optional<TokenBlacklistEntity> findByToken(String token);
}
