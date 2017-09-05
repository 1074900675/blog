package com.zzh.controller;

import com.zzh.domain.User;
import com.zzh.exception.MyException;
import com.zzh.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import static com.zzh.util.Util.UUID;

/**
 * Created by zzh on 2017/4/12.
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    JavaMailSenderImpl javaMailSender;

    Logger logger = Logger.getLogger(UserController.class.getName());


    @PostMapping("/register")
    public String register(@RequestBody User user, HttpSession session) throws MyException {
        if (user.getAuthCode() == null || user.getAuthCode().isEmpty())
            throw new MyException("sessionCode cannot be empty");

        if (user.getUsername() == null || user.getUsername().isEmpty())
            throw new MyException("username cannot be empty");

        if (user.getAccount() == null || user.getAccount().isEmpty())
            throw new MyException("account cannot be empty");

        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new MyException("email cannot be empty");

        if (user.getPassword() == null || user.getPassword().isEmpty())
            throw new MyException("password cannot be empty");
        if (session.getAttribute("KAPTCHA_SESSION_KEY").equals(user.getAuthCode())) {
            try {
                userService.register(user);
                this.email(user.getId(), user.getAuthCode());
            } catch (Exception e) {
                e.printStackTrace();
                throw new MyException("unknown exception");
            }
        } else {
            throw new MyException("sessionCode error");
        }
        return "success";
    }

    @PostMapping("/login")
    public String login(String account, String password, String sessionKey, HttpSession session) throws MyException {
        if (sessionKey == null || sessionKey.isEmpty())
            throw new MyException("sessionCode cannot be empty");
        if (account == null || account.isEmpty())
            throw new MyException("account cannot be empty");
        if (password == null || password.isEmpty())
            throw new MyException("password cannot be empty");
        if (session.getAttribute("KAPTCHA_SESSION_KEY").equals(sessionKey)) {
            try {
                User user = userService.login(account);
                if (user != null && user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
                    session.setAttribute("user", user);
                } else {
                    throw new MyException("Account or password error");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new MyException("unknown exception");
            }
        } else {
            throw new MyException("sessionCode error");
        }
        return "success";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "success";
    }

    @GetMapping("/hello")
    public String hello(HttpSession session) {

        return "hello";
    }

    @GetMapping("/active")
    public String active(String id, String authcode) throws MyException {
        if (id == null || id.isEmpty()) {
            throw new MyException("id cannot be empty");
        }
        if (authcode == null || authcode.isEmpty()) {
            throw new MyException("authcode cannot be empty");
        }
        try {
            userService.active(id, authcode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException("unknown exception");
        }
        return "success";
    }

    @GetMapping("/email")
    public String email(String id, String authcode) throws MyException {
        if (id == null || id.isEmpty()) {
            throw new MyException("id cannot be empty");
        }
        if (authcode == null || authcode.isEmpty()) {
            throw new MyException("authcode cannot be empty");
        }
        try {
            javaMailSender.setHost("smtp.qq.com");
            javaMailSender.setUsername("1074900675@qq.com");
            javaMailSender.setPassword("hvwomhihbmxcgbbi");
            //加认证机制
            Properties javaMailProperties = new Properties();
            javaMailProperties.put("mail.smtp.auth", true);
            javaMailProperties.put("mail.smtp.starttls.enable", true);
            javaMailProperties.put("mail.smtp.timeout", 5000);
            javaMailSender.setJavaMailProperties(javaMailProperties);
            //创建邮件内容
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("1074900675@qq.com");
            message.setTo("1074900675@qq.com");
            message.setSubject("测试邮件");
            message.setText("http://10.3.13.135:8081/active?id=" + id + "&authcode=" + authcode);
            //发送邮件
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException("unknown exception");
        }
        return "success";
    }

    @PostMapping("/upload/icon")
    public String uploadIcon(MultipartFile file) throws MyException {
        if (!file.isEmpty()) {
            if(!file.getContentType().equals("image/png") && !file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/bmp")) {
                throw new MyException("Incorrect type");
            }
            //System.out.print(file.getContentType());
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File("D:/"+UUID()+".jpg")));
                stream.write(bytes);
                stream.close();
                return "success";
            } catch (Exception e) {
                e.printStackTrace();
                throw new MyException("unknown exception");
            }
        } else {
            throw new MyException("file is null");
        }

    }


}
