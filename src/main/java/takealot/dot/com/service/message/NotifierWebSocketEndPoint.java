/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service.message;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 *
 * @author rss
 */
@Configuration
@EnableWebSocketMessageBroker
public class NotifierWebSocketEndPoint extends AbstractWebSocketMessageBrokerConfigurer{

    @Override
    public void registerStompEndpoints(StompEndpointRegistry ser) {
      
        ser.addEndpoint("/TAKEALOT/administratorClientCP").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
      
        registry.setApplicationDestinationPrefixes("/TAKEALOT").enableSimpleBroker("/notify" ,"/adminOnline","/onMessageDelivered","/newAdminOnline","/alertBroadcast");
        //registry.setUserDestinationPrefix("/admin");
    }
    //
    
}
