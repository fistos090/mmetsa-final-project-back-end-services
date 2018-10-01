/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Mmetsa
 */
@Entity
public class CustomerOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(length = 9, name = "orderID")
    private Long id;
    @Column(length = 9, nullable = false, unique = false)
    private Long custID;
    @Column(length = 10, nullable = false)
    private double shippingCost;
    @Column(length = 10, nullable = false)
    private String orderStatus;
    @Column(length = 30, nullable = false, name = "order_date")
    @Temporal(TemporalType.DATE)
    private Date custOrderDate;
    @Temporal(TemporalType.TIME)
    private Date custOrderTime;

    public CustomerOrder() {
    }

//    public CustomerOrder(Long custID, double shippingCost, Date custOrderDate) {
//        this.custID = custID;
//        this.shippingCost = shippingCost;
//        this.custOrderDate = custOrderDate;
//        this.custOrderTime = custOrderDate;
//    }

    public CustomerOrder(Long custID, double shippingCost, String orderStatus, Date custOrderDate) {
        this.custID = custID;
        this.shippingCost = shippingCost;
        this.orderStatus = orderStatus;
        this.custOrderDate = custOrderDate;
        this.custOrderTime = custOrderDate;
    }
    
    public CustomerOrder(Long id,Long custID, double shippingCost, String orderStatus, Date custOrderDate) {
        this.id = id;
        this.custID = custID;
        this.shippingCost = shippingCost;
        this.orderStatus = orderStatus;
        this.custOrderDate = custOrderDate;
        this.custOrderTime = custOrderDate;
    }
    

    public Long getCustID() {
        return custID;
    }

    public void setCustID(Long custID) {
        this.custID = custID;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Date getCustOrderDate() {
        return custOrderDate;
    }

    public void setCustOrderDate(Date custOrderDate) {
        this.custOrderDate = custOrderDate;
    }

    public Date getCustOrderTime() {
        return custOrderTime;
    }

    public void setCustOrderTime(Date custOrderTime) {
        this.custOrderTime = custOrderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CustomerOrder)) {
            return false;
        }
        CustomerOrder other = (CustomerOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CustomerOrder{" + "id=" + id + ", custID=" + custID + ", shippingCost=" + shippingCost + ", orderStatus=" + orderStatus + ", custOrderDate=" + custOrderDate + ", custOrderTime=" + custOrderTime + '}';
    }

  

}
