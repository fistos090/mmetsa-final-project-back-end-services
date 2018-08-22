/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service.message.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 *
 * @author ABSMBNW
 */
@Service
public class EmailEventEmitter {
   
    @Autowired
    private ApplicationEventPublisher publisher;
    
    public void emitEmailEvent(Email email){
        System.out.println("***************************");
        EmailEvent emailEvent = new EmailEvent(this, email);
        this.publisher.publishEvent(emailEvent);
        System.out.println("**************888888888*************");
    }
}
