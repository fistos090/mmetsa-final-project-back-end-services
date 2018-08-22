/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service;

import java.io.UnsupportedEncodingException;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.springframework.stereotype.Service;

/**
 *
 * @author rss
 */
@Service
public class ImageManager {

    public String createEncodedImage(String imageAdditonalInfo, byte[] imageData) throws UnsupportedEncodingException {

        byte[] base64Encode = Base64.base64Encode(imageData);
        String imageD = new String(base64Encode, "UTF-8");

        return imageAdditonalInfo + ',' + imageD;
    }
    
    public byte[] createDecodedImage(String imageData) throws UnsupportedEncodingException {

        return Base64.base64Decode(imageData.getBytes());
    }
    

}
