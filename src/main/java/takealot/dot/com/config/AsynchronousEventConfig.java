/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 *
 * @author ABSMBNW
 */
@Configuration
public class AsynchronousEventConfig {
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticast(){
       SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
       eventMulticaster.setTaskExecutor( new SimpleAsyncTaskExecutor());
       
       return eventMulticaster;    
    }
}
