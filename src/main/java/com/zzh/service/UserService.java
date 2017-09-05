package com.zzh.service;

import com.zzh.domain.User;
import com.zzh.domain.UserRepository;
import com.zzh.exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import java.util.List;

import static com.zzh.util.Util.UUID;

/**
 * Created by zzh on 2017/4/12.
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public void register(User user) throws MyException, Exception{
            User u = userRepository.findByAccountOrEmail(user.getAccount(), user.getEmail());
            if (u == null) {
                user.setAuthCode(DigestUtils.md5DigestAsHex(user.getAuthCode().getBytes()));
                user.setId(UUID());
                user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
                userRepository.save(user);
            } else {
                throw new MyException("Account or E-mail has already been registered");
            }
    }

    public User login(String account)  throws Exception{
        User user = userRepository.findByAccountOrEmail(account, account);
        return user;
    }

    public void active(String id, String authcode) throws Exception{//邮箱激活账号
        User user = userRepository.findByIdAndAuthCode(id,authcode);
        user.setState(true);
        userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
