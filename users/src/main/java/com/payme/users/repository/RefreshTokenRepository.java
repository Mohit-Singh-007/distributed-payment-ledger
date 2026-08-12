package com.payme.users.repository;

import com.payme.users.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,String> {

   Optional<RefreshToken> findByHashedToken(String tokenHash);
   void deleteByUserId(String userId);
}
