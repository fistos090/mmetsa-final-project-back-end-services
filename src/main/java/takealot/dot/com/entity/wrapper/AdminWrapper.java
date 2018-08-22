/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.entity.wrapper;

import takealot.dot.com.entity.Administrator;


/**
 *
 * @author Sifiso
 */
public class AdminWrapper {
    
    private Administrator admin;
    private String  encodedAdminImage;

    public AdminWrapper() {
    }

    public AdminWrapper(Administrator admin) {
        this.admin = admin;
    }

    public AdminWrapper(Administrator admin, String encodedAdminImage) {
        this.admin = admin;
        this.encodedAdminImage = encodedAdminImage;
    }

    public Administrator getAdmin() {
        return admin;
    }

    public void setAdmin(Administrator admin) {
        this.admin = admin;
    }

    public String getEncodedAdminImage() {
        return this.encodedAdminImage;
    }

    public void setEncodedAdminImage(String adminImage) {
        this.encodedAdminImage = adminImage;
    }

    @Override
    public String toString() {
        return "AdminWrapper{" + "admin=" + admin + ", adminImage=" + encodedAdminImage + '}';
    }
    
    
}
