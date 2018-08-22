/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service.message.helpers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import takealot.dot.com.service.EmailService;

/**
 *
 * @author ABSMBNW
 */
@Component
public class EmailEventHandler implements ApplicationListener<EmailEvent>{
    @Autowired
    private EmailService emailService;
    
    @Override
    public void onApplicationEvent(EmailEvent e) {
        System.out.println("**************handler*************");
        Email email = e.getEmail();
        if(email != null){
            try {
                this.emailService.sendEmail(email.getEmailSubject(), email.getEmailBody(), email.getDestinationAddress());
            } catch (MessagingException ex) {
                Logger.getLogger(EmailEventHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
