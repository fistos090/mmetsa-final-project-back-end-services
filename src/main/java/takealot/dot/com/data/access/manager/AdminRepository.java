/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.data.access.manager;

import org.springframework.data.repository.CrudRepository;
import takealot.dot.com.entity.Administrator;

/**
 *
 * @author Sifiso
 */
public interface AdminRepository extends CrudRepository<Administrator, Long>{
    
}
