/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.restcontroller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import takealot.dot.com.entity.Customer;
import takealot.dot.com.service.CustomerOrderService;

/**
 *
 * @author Sifiso
 */
@RequestMapping("/BAKERY")
@RestController
public class CustomerOrderController {

    @Autowired
    private CustomerOrderService orderService;

    @RequestMapping(method = RequestMethod.POST, value = "/checkout")
    public HashMap checkout(@RequestBody String orderData) {

        System.out.println(orderData);
        //HashMap hashMap = new HashMap();
        HashMap response = null;
        try {
            response = orderService.processOrder(orderData);
        } catch (JSONException | MessagingException ex) {
            Logger.getLogger(CustomerOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return response;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getCustomerOrders/{sessionID}")
    public HashMap getCustomerOrders(@RequestBody Customer cus, @PathVariable("sessionID") String sessionID) {

        return orderService.getCustomerOrders(cus, sessionID);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getCustomerOrderProducts/{orderId}")
    public HashMap getCustomerOrderProducts(@PathVariable("orderId") Long orderId) {

        HashMap response = null;
        try {
            response = orderService.getCustomerOrderProducts(orderId);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CustomerOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return response;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/displayAllOrders/{sessionID}/{adminID}")
    public HashMap displayAllOrders(@PathVariable("sessionID") String sessionID, @PathVariable("adminID") Long adminID) {

        System.out.println(sessionID);

        HashMap response = orderService.getAllOrders(sessionID, adminID);

        System.out.println(response);
        return response;
    }

//    @RequestMapping(method = RequestMethod.GET,value = {"/deleteOrder/{sessionID}/{orderID}",})
//    public HashMap deleteOrder(@PathVariable() Long orderID,@PathVariable String sessionID,HttpServletRequest request){
//
//        HashMap response = orderService.getAllOrders(sessionID,request.getSession());
// 
//        return response;
//    }
//    @RequestMapping(method = RequestMethod.GET, value = "/printInvoice/{sessionID}/{adminID}")
//    public HashMap printInvoice(@PathVariable("sessionID") String sessionID, @PathVariable("adminID") Long adminID) {
//
//        System.out.println(sessionID);
//
////        HashMap response = orderService.printInvoice(sessionID, adminID);
//
//        System.out.println(response);
//        return response;
//    }
}
