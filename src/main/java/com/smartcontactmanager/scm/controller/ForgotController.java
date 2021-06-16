package com.smartcontactmanager.scm.controller;

import com.smartcontactmanager.scm.dao.UserRepository;
import com.smartcontactmanager.scm.entities.User;
import com.smartcontactmanager.scm.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class ForgotController {

    Random random = new Random(1000);

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //Email id form open handler
    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email")String email , HttpSession session){
        System.out.println("EMAIL "+email);

        //Generating otp of 4 digit

        int otp = random.nextInt(999999);
        System.out.println("OTP "+otp);

        //Write code for OTP to send email...
        String subject = "OTP from SCM";
        String message = "" +
                "<div style= 'border:1px solid #e2e2e2; padding:20px'>" +
                "<h1>" +
                "OTP is " +
                "<b> " +otp+
                "</n>" +
                "</h1>" +
                "</div>";
        String to = email;


        boolean flag = this.emailService.sendEmail(subject, message, to);
        if(flag){
            session.setAttribute("myOtp", otp);
            session.setAttribute("email", email);
            return "verify_otp";

        }else {

            session.setAttribute("message", "Check your email id !!");
            return "forgot_email_form";

        }

    }


    //verify_otp
    @PostMapping("/verify_otp")
    public String verifyOtp(@RequestParam("otp")int otp, HttpSession session){

        int myOtp =(int) session.getAttribute("myOtp");
        String email = (String) session.getAttribute("email");
        if(myOtp==otp){

            //password change form
            User user = this.userRepository.getUserByUserName(email);

            if(user==null)
            {
                //send error message
                session.setAttribute("message", "User does not exit with this email !!");
                return "forgot_email_form";


            }else {

                //send change password form


            }
            return "password_change_form";

        }else {

            session.setAttribute("message", "You have entered wrong otp");
            return "verify_otp";
        }
    }
    //Change password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword")String newPassword, HttpSession session) {

        String email = (String) session.getAttribute("email");
        User user = this.userRepository.getUserByUserName(email);
        user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
        this.userRepository.save(user);

        return "redirect:/signin?change=password changed successfully..";





    }

}
