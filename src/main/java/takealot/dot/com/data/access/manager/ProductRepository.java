/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.data.access.manager;

import java.io.Serializable;
import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;
import takealot.dot.com.entity.Product;

/**
 *
 * @author Mmetsa
 */
public interface ProductRepository extends CrudRepository<Product, Long>{
    public ArrayList<Product> findByCategory(String category);
}
