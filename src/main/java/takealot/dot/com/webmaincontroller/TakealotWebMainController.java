/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.webmaincontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Sifiso
 */

//This controller is used by Angularjs UI interface for page mapping
@Controller
public class TakealotWebMainController {
    
    /**
     *
     * @return
     */
    @RequestMapping(value = "/")
    public String loadHomePage(){
       return "home";
    }
    
    /**
     *
     * @param pageName
     * @return
     */
    @RequestMapping(value = "/{pageName}")
    public String loadPage(@PathVariable("pageName") String pageName){
        return pageName;
    }
}
