/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import takealot.dot.com.data.access.manager.AdminRepository;
import takealot.dot.com.data.access.manager.ProductRepository;
import takealot.dot.com.entity.Administrator;
import takealot.dot.com.entity.Product;
import takealot.dot.com.entity.wrapper.ProductWrapper;
import takealot.dot.com.service.message.ProductNotifyMessage;

/**
 *
 * @author Sifiso
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private ImageManager imageManager;
    @Autowired
    private SimpMessagingTemplate template;

    private final ProductWrapper[] homePageProducts = new ProductWrapper[15];

    public HashMap getHomePageProducts() {

        HashMap response = new HashMap();

        response.put("homeProducts", this.homePageProducts);

        return response;
    }

    public HashMap AddHomePageProduct(Long productId) throws UnsupportedEncodingException {

        HashMap response = new HashMap();
        Product product = productRepository.findOne(productId);

        if (product != null) {
            ProductWrapper productWrapper = new ProductWrapper(product, imageManager.createEncodedImage(product.getImageAdditonalInfo(), product.getProductImage()));

            ProductWrapper tempProduct = null;
            if (this.homePageProducts.length > 0) {
                tempProduct = this.homePageProducts[this.homePageProducts.length - 1];
            }

            if (tempProduct == null) {
                this.homePageProducts[this.homePageProducts.length - 1] = productWrapper;
            }
            response.put("status", "PRODUCT_ADDED");
        } else {
            response.put("status", "LIST_FULL");
        }

        return response;
    }

    public HashMap removeHomePageProduct(Long productId) {
        HashMap response = new HashMap();
        response.put("status", "NOT_FOUND");
        for (int s = 0; s < this.homePageProducts.length; s++) {
            if (this.homePageProducts[s].getProduct().getId().equals(productId)) {
                this.homePageProducts[s] = null;
                response.put("status", "REMOVED");
            }
        }
        return response;
    }

    public HashMap getAllShopProduct() throws UnsupportedEncodingException {

        HashMap productsDetails = new HashMap();
        ArrayList<Product> products = getAllProducts();//convert Iterable into ArrayList
        ArrayList<ProductWrapper> wrappedProducts = new ArrayList<>();

        products = getOnstockProduct(products);//Remove product with negative quantity

        for (Product product : products) {

            ProductWrapper productWrapper = new ProductWrapper(product, imageManager.createEncodedImage(product.getImageAdditonalInfo(), product.getProductImage()));
            wrappedProducts.add(productWrapper);
        }

        productsDetails.put("products", wrappedProducts);

        return productsDetails;
    }

    public HashMap getByCategory(String category) throws UnsupportedEncodingException {
        ArrayList<Product> products = productRepository.findByCategory(category);

        HashMap productsDetails = new HashMap();
        ArrayList<ProductWrapper> wrappedProducts = new ArrayList<>();

        products = getOnstockProduct(products);//Remove product with negative quantity

        for (Product product : products) {

            ProductWrapper productWrapper = new ProductWrapper(product, imageManager.createEncodedImage(product.getImageAdditonalInfo(), product.getProductImage()));
            wrappedProducts.add(productWrapper);
        }

        productsDetails.put("products", wrappedProducts);

        return productsDetails;
    }

    /*
    Method used to convert Iterable to ArrayList
     */
    public ArrayList<Product> getAllProducts() {

        Iterable<Product> allProducts = productRepository.findAll();

        ArrayList<Product> products = new ArrayList<>();

        allProducts.forEach(products::add);

        return products;
    }

    /*
    1)This method check product quantity 
    2)remove offstock products and 
    3)return products that have a positive quantity only [quantity > 0]
     */
    private ArrayList<Product> getOnstockProduct(ArrayList<Product> products) throws UnsupportedEncodingException {

        ArrayList<Product> offStockProducts = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);

            if (product.getQuantity() <= 0) {
                //Notify admin about out off stock products
                notifySystemAdmin(product);
                offStockProducts.add(product);
            }
        }

        if (!offStockProducts.isEmpty()) {
            notifySystemAdmin((Product[]) offStockProducts.toArray());
        }

        products.removeAll(offStockProducts);

        return products;
    }

    public int loadProducts() {
        int i = 1;
        for (; i <= 15; i++) {

            Product p = new Product("black forest", "ddd", 13.9 * i, "cakes", 23 * i, "egdfg".getBytes(), "imageAdditonalInfo");
            productRepository.save(p);
        }

        return i;
    }

    public HashMap storeProduct(String data) throws JSONException, IOException {

        HashMap response = new HashMap();

        JSONObject jsonData = new JSONObject(data);

        String sessionID = jsonData.getString("sessionID");
        Long adminID = jsonData.getLong("adminID");
        Administrator admin = adminRepository.findOne(adminID);
        String message = "Please sign in";
        String status = "FAILED";

        if (adminService.adminHasLogin(sessionID, admin.getEmail())) {
            JSONObject productData = (JSONObject) jsonData.get("product");

//            Long prodId = productData.isNull("id");
            String productName = productData.getString("productName");
            String productDesc = productData.getString("productDesc");
            double price = productData.getDouble("price");
            String category = productData.getString("category");
            int quantity = productData.getInt("quantity");
            String img = productData.getString("productImage");

            String[] tokens = img.split(",");

            //BASE64Decoder decoder = new BASE64Decoder();
            byte[] productImage = imageManager.createDecodedImage(tokens[1]); //Base64.base64Decode(tokens[1].getBytes());
            Product product;
            System.out.println("productData.isNull(\"id\") "+productData.isNull("id"));
            if (productData.isNull("id")) {
                product = new Product(productName, productDesc, price, category, quantity, productImage, tokens[0]);
            } else {
                product = new Product(productData.getLong("id"), productName, productDesc, price, category, quantity, productImage, tokens[0]);
                
            }

            Product newProd = productRepository.save(product);

            //ArrayList<Product> categoryProducts = productRepository.findByCategory(category);
            //int newProductID = categoryProducts.get(categoryProducts.size() - 1).getId().intValue();
            if (newProd.getId() != null) {
                if (productData.isNull("id")) {
                    message = "New product record has been added to database with product ID " + newProd.getId();
                } else {
                    message = "Product record is updated successfully";
                }
                
                response.put("product_id", newProd.getId());
            } else {
                message = "Product record could not be added or updated";
            }
            status = "CREATED";
        }

        response.put("message", message);
        response.put("status", status);

        return response;
    }

    //Send RT messages to all admins to notify them of product out off stock status
    private void notifySystemAdmin(Product... products) throws UnsupportedEncodingException {

        //Product product = products.length > 0 ? products[0] : null;
        String message = "<h3>The following products are out stock. They won't be display to the customers<h3> <br><br>";
        ProductNotifyMessage productNotifyMessage = new ProductNotifyMessage();
        ArrayList<ProductWrapper> wrappedProducts = new ArrayList<>();

        productNotifyMessage.setUserType("Server");
        productNotifyMessage.setMessageContent(message);

        for (Product product : products) {
            ProductWrapper productWrapper = new ProductWrapper(product, imageManager.createEncodedImage(product.getImageAdditonalInfo(), product.getProductImage()));
            wrappedProducts.add(productWrapper);
        }

        productNotifyMessage.setOnlineAdmins(wrappedProducts);

        template.convertAndSend("/alertBroadcast/adminsGroup/", productNotifyMessage);

    }

}
