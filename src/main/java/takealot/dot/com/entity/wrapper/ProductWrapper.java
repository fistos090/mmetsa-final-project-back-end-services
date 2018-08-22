/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.entity.wrapper;

import takealot.dot.com.entity.Product;

/**
 *
 * @author rss
 */
public class ProductWrapper {
    private Product product;
    private String  productImage;

    public ProductWrapper() {
    }

    public ProductWrapper(Product product) {
        this.product = product;
    }

    public ProductWrapper(Product product, String productImage) {
        this.product = product;
        this.productImage = productImage;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Product getProduct() {
        return product;
    }

    public String getProductImage() {
        return productImage;
    }
    
    
}
