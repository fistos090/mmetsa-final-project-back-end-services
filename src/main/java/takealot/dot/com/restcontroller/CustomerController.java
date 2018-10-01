/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.restcontroller;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import takealot.dot.com.entity.Customer;
import takealot.dot.com.service.CustomerService;

/**
 *
 * @author Sifiso
 */
@RestController
@RequestMapping("/BAKERY")
public class CustomerController {

    public static HashMap logonCustomerIds = new HashMap();
    @Autowired
    private CustomerService service;

    @RequestMapping(method = RequestMethod.POST, value = "/customer/register")
    public HashMap registerNewUser(@RequestBody Customer cus, HttpServletRequest request) {

        HashMap responseDetails = null;
        try {
            //        try {
            responseDetails = service.registerCustomer(cus);
        } catch (Exception ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (responseDetails.get("status") == "CREATED") {
            responseDetails.put("auto_logon", signIn(cus, request));
        }

        return responseDetails;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/customer/login")
    public HashMap signIn(@RequestBody Customer cus, HttpServletRequest request) {

        HttpSession session = request.getSession();

        HashMap responseDetails = null;
        try {
            responseDetails = service.login(cus, session);
        } catch (Exception ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return responseDetails;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/customer/logout/{sessionID}/{userID}")
    public HashMap signout(@PathVariable("sessionID") String sessionID, @PathVariable("userID") Long userID) {

        HashMap response = service.logout(sessionID, userID);

        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllRegisteredCustomers/{sessionID}/{userID}")
    public HashMap getAllUsers(@PathVariable("sessionID") String sessionID, @PathVariable("userID") Long userID) {

        HashMap response = service.getAllRegisteredCustomers(sessionID, userID);

        return response;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/customer/updateProfile/{sessionID}")
    public HashMap updateProfile(@RequestBody Customer cus, @PathVariable("sessionID") String sessionID) {

        System.out.println(cus);
        HashMap response = service.updateProfile(cus, sessionID);

        return response;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/customer/forgot")
    public HashMap retrievePassword(@RequestBody String email) {

        HashMap response = service.findCustomer(email);

        return response;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/customer/passAnswer")
    public HashMap completePasswordRetrival(@RequestBody String customerData) {

        HashMap response = null;
        try {
            response = service.completePasswordRetrival(customerData);
        } catch (JSONException | MessagingException ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return response;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/printCustomerLoginTrackReport", produces = "application/pdf")
    public void printReport(HttpServletResponse response, @RequestBody String requestData) {
        int bufferSize = response.getBufferSize();
        response.reset();
        response.setContentType("application/pdf");
        response.setBufferSize(bufferSize);

        try {

            service.printCustomerLoginTrackReport(response.getOutputStream(), requestData);

        } catch (DocumentException | IOException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
