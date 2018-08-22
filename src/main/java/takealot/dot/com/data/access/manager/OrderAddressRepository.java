/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.data.access.manager;

import org.springframework.data.repository.CrudRepository;
import takealot.dot.com.entity.OrderAddress;

/**
 *
 * @author Mmetsa
 */
public interface OrderAddressRepository extends CrudRepository<OrderAddress, Long>{
    
}
