/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

/**
 *
 * @author rss
 */
@Component
public class NotifierEventHandler {
    
    @Autowired
    private SimpMessagingTemplate template;
    
//    @Scheduled(fixedRate = 60000)
//    public  void keepSend(){
//        template.convertAndSend("/notify","Server: I have your message");
//        System.out.println("got your message");
// 
//    }
    
    @EventListener
    public void onWebSocketConnected(SessionConnectedEvent event){
        System.out.println(event.getMessage()+" message");
        
        System.out.println("User logged in "+event.getUser());
    }
    
    @EventListener
    public void onWebSocketDisonnected(SessionConnectedEvent event){
        
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        
        System.out.println("&&&&&&&&&&&&");
    }
    
}
//
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package takealot.dot.com.service;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import org.eclipse.persistence.internal.oxm.conversion.Base64;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import sun.misc.BASE64Decoder;
//import takealot.dot.com.data.access.manager.AdminRepository;
//import takealot.dot.com.data.access.manager.ProductRepository;
//import takealot.dot.com.entity.Administrator;
//import takealot.dot.com.entity.Product;
//import takealot.dot.com.entity.wrapper.ProductWrapper;
//
///**
// *
// * @author Sifiso
// */
//@Service
//public class ProductService {
//
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private AdminService adminService;
//    @Autowired
//    private AdminRepository adminRepository;
//    @Autowired
//    private ImageManager imageManager;
//
//    public HashMap getAllShopProduct() throws UnsupportedEncodingException {
//
//        HashMap productsDetails = new HashMap();
//
//        ArrayList<Product> products = getAllProducts();//convert Iterable into ArrayList
//        //ArrayList<String> productsImages = new ArrayList<>();
//
//        ArrayList<ProductWrapper> wrappedProducts = new ArrayList<>();
//        
//        products = getOnstockProduct(products);//Remove product with negative quantity
//
//        //products.forEach(prod -> prod.setProductImage(Base64.base64Encode(prod.getProductImage())));
////        for (int i = 0; i < products.size(); i++) {
////            
////            Product prod = products.get(i);
////            productsImages.add(recreateImage(prod.getProductImage(), prod.getImageAdditonalInfo()));
////        }
//        
//        for(Product product : products){
//            
//            ProductWrapper productWrapper = new ProductWrapper(product, imageManager.createEncededImage(product.getImageAdditonalInfo(), product.getProductImage()));
//            wrappedProducts.add(productWrapper);
//        }
//        
//
//        //productsDetails.put("productsImages", wrappedProducts);
//        productsDetails.put("products", wrappedProducts);
//
//        return productsDetails;
//    }
//
//    public HashMap getByCategory(String category) throws UnsupportedEncodingException {
//        ArrayList<Product> products = productRepository.findByCategory(category);
//
//        HashMap productsDetails = new HashMap();
//        //ArrayList<String> productsImages = new ArrayList<>();
//        ArrayList<ProductWrapper> wrappedProducts = new ArrayList<>();
//
//        products = getOnstockProduct(products);//Remove product with negative quantity
//
////        for (int i = 0; i < products.size(); i++) {
////
////            Product prod = products.get(i);
////            productsImages.add(recreateImage(prod.getProductImage(), prod.getImageAdditonalInfo()));
////        }
//        
//        for(Product product : products){
//            
//            ProductWrapper productWrapper = new ProductWrapper(product, imageManager.createEncodedImage(product.getImageAdditonalInfo(), product.getProductImage()));
//            wrappedProducts.add(productWrapper);
//        }
//
//        //productsDetails.put("productsImages", productsImages);
//        productsDetails.put("products", wrappedProducts);
//
//        return productsDetails;
//    }
//
////    private String recreateImage(byte[] imageData, String imageExtraInfo) throws UnsupportedEncodingException {
////
////        byte[] base64Encode = Base64.base64Encode(imageData);
////        String imageD = new String(base64Encode, "UTF-8");
////
////        return imageExtraInfo + ',' + imageD;
////    }
//
//    /*
//    Method used to convert Iterable into ArrayList
//     */
//    public ArrayList<Product> getAllProducts() {
//
//        Iterable<Product> allProducts = productRepository.findAll();
//
//        ArrayList<Product> products = new ArrayList<>();
//
//        allProducts.forEach(products::add);
//
//        return products;
//    }
//
//    /*
//    1)This method check product quantity 
//    2)remove offstock products and 
//    3)return products that have a positive quantity only [quantity > 0]
//     */
//    private ArrayList<Product> getOnstockProduct(ArrayList<Product> products) {
//
//        ArrayList<Product> offStockProducts = new ArrayList<>();
//
//        for (int i = 0; i < products.size(); i++) {
//            Product product = products.get(i);
//
//            if (product.getQuantity() <= 0) {
//                //Notify admin about out off stock products
//                notifySystemAdmin(product);
//                offStockProducts.add(product);
//            }
//        }
//
//        if (!offStockProducts.isEmpty()) {
//            notifySystemAdmin((Product[]) offStockProducts.toArray());
//        }
//
//        products.removeAll(offStockProducts);
//
//        return products;
//    }
//
//    public int loadProducts() {
//        int i = 1;
//        for (; i <= 15; i++) {
//            Product p = new Product("1st item", 25.2 * i, "Books", 500);
//            productRepository.save(p);
//        }
//
//        return i;
//    }
//
//    public HashMap storeProduct(String data) throws JSONException, IOException {
//
//        HashMap response = new HashMap();
//
//        JSONObject jsonData = new JSONObject(data);
//
//        String sessionID = jsonData.getString("sessionID");
//        Long adminID = jsonData.getLong("adminID");
//        Administrator admin = adminRepository.findOne(adminID);
//        String message = "Please sign in";
//        String status = "FAILED";
//
//        if (adminService.adminHasLogin(sessionID, admin.getEmail())) {
//            JSONObject productData = (JSONObject) jsonData.get("product");
//
//            String productDesc = productData.getString("DESC");
//            double price = productData.getDouble("PRICE");
//            String category = productData.getString("CATEGORY");
//            int quantity = productData.getInt("QUANTITY");
//            String img = productData.getString("prodImage");
//
//            String[] tokens = img.split(",");
//
//            //BASE64Decoder decoder = new BASE64Decoder();
//            byte[] productImage = imageManager.createDecodedImage(tokens[1]); //Base64.base64Decode(tokens[1].getBytes());
//
//            Product product = new Product(productDesc, price, category, quantity, productImage, tokens[0]);
//
//            Product newProd = productRepository.save(product);
//
//            //ArrayList<Product> categoryProducts = productRepository.findByCategory(category);
//            //int newProductID = categoryProducts.get(categoryProducts.size() - 1).getId().intValue();
//            if (newProd.getId() != null) {
//                message = "A new product has been added to database with product ID " + newProd.getId();
//            }else{
//                message = "A new product could not be added";
//            }
//            status = "CREATED";
//        }
//
//        response.put("message", message);
//        response.put("status", status);
//
//        return response;
//    }
//
//    //Send RT messages to all admins to notify them of product out off stock status
//    private void notifySystemAdmin(Product... products) {
//
//        //Product product = products.length > 0 ? products[0] : null;
//        //
//    }
//
//}
