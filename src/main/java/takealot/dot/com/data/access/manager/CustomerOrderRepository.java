/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.data.access.manager;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import takealot.dot.com.entity.CustomerOrder;

/**
 *
 * @author Sifiso
 */
public interface CustomerOrderRepository extends CrudRepository<CustomerOrder, Long>{
    public List<CustomerOrder> findByCustID(Long id);
    
}
