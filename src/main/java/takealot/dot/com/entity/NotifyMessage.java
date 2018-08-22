/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Sifiso
 */
@Entity
public class NotifyMessage implements Serializable{
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int gotoUserID;
    private int fromUserID;
    private String messageContent;
    private String userType;
    private MessageStatus messageStatus;

    public NotifyMessage() {
    }

    public NotifyMessage(int gotoUserID, int fromUserID, String messageContent, String userType, MessageStatus messageStatus) {
        this.gotoUserID = gotoUserID;
        this.fromUserID = fromUserID;
        this.messageContent = messageContent;
        this.userType = userType;
        this.messageStatus = messageStatus;
    }

    public NotifyMessage(Long id, int gotoUserID, int fromUserID, String messageContent, String userType, MessageStatus messageStatus) {
        this.id = id;
        this.gotoUserID = gotoUserID;
        this.fromUserID = fromUserID;
        this.messageContent = messageContent;
        this.userType = userType;
        this.messageStatus = messageStatus;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
   
    public int getGotoUserID() {
        return gotoUserID;
    }

    public void setGotoUserID(int gotoUserID) {
        this.gotoUserID = gotoUserID;
    }

    public int getFromUserID() {
        return fromUserID;
    }

    public void setFromUserID(int fromUserID) {
        this.fromUserID = fromUserID;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public Long getId() {
        return id;
    }
    
}
