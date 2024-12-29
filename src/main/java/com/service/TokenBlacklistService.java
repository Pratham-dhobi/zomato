package com.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.TokenBlacklistEntity;
import com.repository.TokenBlacklistRepository;

@Service
public class TokenBlacklistService {

	@Autowired
	TokenBlacklistRepository tokenBlacklistRepository;
	
    public void addTokenToBlacklist(String token) {
        TokenBlacklistEntity blacklistedToken = new TokenBlacklistEntity();
        blacklistedToken.setToken(token);
        blacklistedToken.setDate(LocalDate.now());
        tokenBlacklistRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.findByToken(token).isPresent();
    }
}
