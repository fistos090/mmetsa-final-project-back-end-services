/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service.message.helpers;

import org.springframework.context.ApplicationEvent;

/**
 *
 * @author ABSMBNW
 */

public class EmailEvent extends ApplicationEvent{
    
    private final Email email;
    public EmailEvent(Object source,Email email) {
        super(source);
        this.email = email;
    }

    public Email getEmail() {
        return email;
    }
    
}
