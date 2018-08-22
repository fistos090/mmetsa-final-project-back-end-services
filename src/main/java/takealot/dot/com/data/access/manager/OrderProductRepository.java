/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.data.access.manager;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import takealot.dot.com.entity.OrderProduct;

/**
 *
 * @author Mmetsa
 */
public interface OrderProductRepository extends CrudRepository<OrderProduct, Long>{
    public List<OrderProduct> findByOrderID(Long orderID);
}
