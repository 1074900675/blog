package com.zzh.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by zzh on 2017/4/12.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public User findByAccount(String account);

    public User findByEmail(String email);

    public User findByAccountOrEmail(String account, String email);

    public User findByIdAndAuthCode(String id, String auth_code);
}
