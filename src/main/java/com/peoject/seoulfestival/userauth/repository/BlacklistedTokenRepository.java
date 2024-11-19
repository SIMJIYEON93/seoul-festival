package com.peoject.seoulfestival.userauth.repository;

import com.peoject.seoulfestival.userauth.entity.BlacklistedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedTokenRepository extends CrudRepository<BlacklistedToken, String> {
}
