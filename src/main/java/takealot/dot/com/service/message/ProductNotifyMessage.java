/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service.message;
import takealot.dot.com.entity.NotifyMessage;
import java.util.List;
import takealot.dot.com.entity.MessageStatus;
import takealot.dot.com.entity.wrapper.ProductWrapper;

/**
 *
 * @author Sifiso
 */
public class ProductNotifyMessage extends NotifyMessage{
    
    private List<ProductWrapper> onlineAdmins;

    public ProductNotifyMessage() {
    }

    public ProductNotifyMessage(List<ProductWrapper> onlineAdmins, int gotoUserID, int fromUserID, String messageContent, String userType, MessageStatus messageStatus) {
        super(gotoUserID, fromUserID, messageContent, userType, messageStatus);
        this.onlineAdmins = onlineAdmins;
    }

    public List<ProductWrapper> getOnlineAdmins() {
        return onlineAdmins;
    }

    public void setOnlineAdmins(List<ProductWrapper> onlineAdmins) {
        this.onlineAdmins = onlineAdmins;
    }
    
}
