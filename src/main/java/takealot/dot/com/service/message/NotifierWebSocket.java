/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service.message;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;



/**
 *
 * @author Sifiso
 */

public class NotifierWebSocket extends TextWebSocketHandler{

    WebSocketSession session;
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //super.afterConnectionClosed(session, status); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Connected to server ");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //super.handleTextMessage(session, message); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Message received "+message.getPayload());
    
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //super.afterConnectionEstablished(session); //To change body of generated methods, choose Tools | Templates.
        this.session = session;
        System.out.println("Connected through websocket");
    }

    
}
