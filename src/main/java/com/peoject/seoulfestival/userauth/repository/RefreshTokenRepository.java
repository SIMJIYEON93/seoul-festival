package com.peoject.seoulfestival.userauth.repository;

import com.peoject.seoulfestival.userauth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
