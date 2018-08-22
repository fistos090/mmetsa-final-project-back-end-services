/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.webmaincontroller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import takealot.dot.com.data.access.manager.NotifyMessageRepository;
import takealot.dot.com.entity.Administrator;
import takealot.dot.com.entity.wrapper.AdminWrapper;
import takealot.dot.com.service.ImageManager;
import takealot.dot.com.entity.NotifyMessage;
import takealot.dot.com.service.message.helpers.OnlineAdminstrators;

/**
 *
 * @author Sifiso
 */
@Controller
@RequestMapping("/BAKERY")
public class RTNotificationWebSocketController {

    public final SimpMessagingTemplate template;
    @Autowired
    public OnlineAdminstrators onlineAdminstrators;
    @Autowired
    private ImageManager imageManager;
    @Autowired
    private NotifyMessageRepository notifyMessageRepository;

    @Autowired
    RTNotificationWebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/send/message")
    //@SendTo("/notify")//
    public String forwardMessages(@Payload NotifyMessage message) {

        boolean isOnLine = false;

        for (AdminWrapper adminWrapper : this.onlineAdminstrators.getOnlineAdmins()) {
            if (adminWrapper.getAdmin().getId() == message.getGotoUserID()) {
                isOnLine = true;
            }
        }

        //Send message to online user only
        if (isOnLine) 
            
            //send to particular user with this
            this.template.convertAndSend("/notify/adminID/" + message.getGotoUserID(), message);
        else{
            //Encrypt the message content before persisting it.
            
            this.notifyMessageRepository.save(message);
            
        }
            
        

        System.out.println("message received ==> " + message.getMessageContent());

        return "Is received......";
    }

    @MessageMapping("/joinChat/id")
    @SendTo("/adminOnline")
    public void goOnline(@Payload Administrator admin) {

        //try {
        AdminWrapper adminWrapper = new AdminWrapper(admin/*, imageManager.createEncodedImage(admin.getImageAdditonalInfo(), admin.getAdminImage())*/);

        this.onlineAdminstrators.markAsOnline(adminWrapper);
        HashMap hashMap = new HashMap();
        hashMap.put("status", "Online");

        //Notify other online users of new online user
        this.template.convertAndSend("/newAdminOnline", hashMap);
        
        hashMap.put("onlineAdmins", this.onlineAdminstrators.getOnlineAdmins());
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(RTNotificationWebSocketController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @MessageMapping("/leaveChat/id")
    public void goOffLine(@Payload Administrator admin) {

        //try {
        AdminWrapper adminWrapper = new AdminWrapper(admin/*, imageManager.createEncodedImage(admin.getImageAdditonalInfo(), admin.getAdminImage())*/);

        this.onlineAdminstrators.markAsOffLine(adminWrapper);
        HashMap hashMap = new HashMap();
        hashMap.put("status", "Online");

        //Notify other online users of new online user
        this.template.convertAndSend("/adminOnline/" + admin.getId(), hashMap);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(RTNotificationWebSocketController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    @MessageMapping("/acknowledgeMessage")
    public void acknowledgeMessageReception(@Payload Administrator admin) {

        AdminWrapper adminWrapper = new AdminWrapper(admin/*, imageManager.createEncodedImage(admin.getImageAdditonalInfo(), admin.getAdminImage())*/);

        this.onlineAdminstrators.markAsOffLine(adminWrapper);
        HashMap hashMap = new HashMap();
        hashMap.put("status", "Online");

        this.template.convertAndSend("/adminOnline/" + admin.getId(), hashMap);

    }

}
