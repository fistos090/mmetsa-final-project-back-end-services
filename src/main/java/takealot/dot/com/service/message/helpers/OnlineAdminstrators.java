/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service.message.helpers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takealot.dot.com.entity.wrapper.AdminWrapper;

/**
 *
 * @author Sifiso
 */

@Service
public class OnlineAdminstrators {
    
    private List<AdminWrapper> onlineAdmins = new ArrayList<>();

    public OnlineAdminstrators() {
    }

    public OnlineAdminstrators(List<AdminWrapper> onlineAdmins) {
        this.onlineAdmins = onlineAdmins;
    }

    public List<AdminWrapper> getOnlineAdmins() {
        return onlineAdmins;
    }

    public void setOnlineAdmins(List<AdminWrapper> onlineAdmins) {
        this.onlineAdmins = onlineAdmins;
    }
    
    public void markAsOnline(AdminWrapper admin){
        System.out.println("adding");
        if(onlineAdmins != null)
            this.onlineAdmins.add(admin);
        System.out.println(this.onlineAdmins.size());
    }
    
    public void markAsOffLine(AdminWrapper admin){
        if(onlineAdmins != null)
            this.onlineAdmins.remove(admin);
    }
    
}
