/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.restcontroller;

import com.itextpdf.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import takealot.dot.com.entity.Administrator;
import takealot.dot.com.entity.wrapper.AdminWrapper;
import takealot.dot.com.service.AdminService;

/**
 *
 * @author Sifiso
 */
@RestController
@RequestMapping("/BAKERY")
public class AdminController {

    public static HashMap logonAdminIds = new HashMap();
    @Autowired
    private AdminService service;
    private final String ssacode = "939!1993";

    @RequestMapping(method = RequestMethod.POST, value = "/admin/register/{ssacode}")
    public HashMap registerNewUser(@RequestBody AdminWrapper adminWrapper, @PathVariable("ssacode") String ssacode, HttpServletRequest request) {

        System.out.println(adminWrapper);

        HashMap responseDetails = null;
        if (ssacode != null) {
            if (this.ssacode.equals(ssacode)) {
                try {
                    responseDetails = service.registerAdmin(adminWrapper, request.getSession());
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                responseDetails = new HashMap();
                responseDetails.put("status", "FAILED");
                responseDetails.put("message", "System security access code is incorrect");
            }
        } else {
                responseDetails = new HashMap();
                responseDetails.put("status", "FAILED");
                responseDetails.put("message", "Invalid System security access code provided");
        }

        return responseDetails;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/admin/login")
    public HashMap signIn(@RequestBody Administrator admin, HttpServletRequest request) {

        try {

            HashMap responseDetails = service.login(admin, request.getSession());

            return responseDetails;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/logout/{sessionID}/{userID}")
    public HashMap signout(@PathVariable("sessionID") String sessionID, @PathVariable("userID") Long userID) {

        HashMap response = service.logout(sessionID, userID);

        return response;
    }
    
}
