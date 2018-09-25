/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.restcontroller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import takealot.dot.com.service.ProductService;

/**
 *
 * @author Sifiso
 */
@RestController
@RequestMapping("/BAKERY")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @RequestMapping(method = RequestMethod.GET, value = "/displayAllProducts")
    //@CrossOrigin(origins = "http://localhost:4200")
    public HashMap displayAllShopProduct() {

        try {
            HashMap products = productService.getAllShopProduct();

            return products;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ProductRestController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/category/{category}")
    public HashMap displayByCategory(@PathVariable("category") String category) {

        try {
            HashMap products = productService.getByCategory(category);

            return products;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ProductRestController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    //Dummy method - To be removed at the later stage
    @RequestMapping(method = RequestMethod.GET, value = "/load")
    public HashMap loadProducts() {

        int num = productService.loadProducts();
        HashMap h = new HashMap();
        h.put("s", "OK - " + num + " are loaded");

        return h;
    }

    @GetMapping(value = "/getHomeProducts")
    public HashMap getHomePageProducts() {
        
        try {
            return productService.getHomePageProducts();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ProductRestController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/addHomePageProduct")
    public HashMap AddHomePageProduct(@RequestBody Long productId) {
        HashMap hashMap = new HashMap();

        try {
            return productService.AddHomePageProduct(productId);
        } catch (UnsupportedEncodingException ex) {
            hashMap.put("status", "EXCEPTION");
            Logger.getLogger(ProductRestController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return hashMap;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/removeHomePageProduct")
    public HashMap removeHomePageProduct(@RequestBody Long productId) {
        return productService.removeHomePageProduct(productId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/removeProduct")
    public HashMap removeProduct(@RequestBody String requestData) {
        return productService.removeProduct(requestData);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/loadNewProduct")
    public HashMap loadProduct(@RequestBody String data) {

        HashMap outcome = null;
        try {

            outcome = productService.storeProduct(data);

        } catch (JSONException | IOException ex) {
            Logger.getLogger(ProductRestController.class.getName()).log(Level.SEVERE, null, ex);

            outcome = new HashMap();
            if (ex.getMessage().contains("sessionID")) {
                outcome.put("message", "Invalid session ID, Please log in to obtain valid session ID.");
                outcome.put("status", "FAILED");
            } else if (ex.getMessage().contains("adminID")) {
                outcome.put("message", "Invalid admin ID, Log in again to bypass this problem");
                outcome.put("status", "FAILED");
            }

        }

        return outcome;
    }

}
