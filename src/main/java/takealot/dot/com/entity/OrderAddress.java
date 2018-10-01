/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Mmetsa
 */
@Entity
public class OrderAddress implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "addressID", length = 10)
    private Long id;
    @Column(length = 10, nullable = false)
    private Long orderID;
    @Column(length = 10, nullable = false)
    private String houseNumber;
    @Column(length = 50, nullable = false)
    private String streetName;
    @Column(length = 50, nullable = false)
    private String surburb;
    @Column(length = 50, nullable = false)
    private String city;
    @Column(length = 10, nullable = false)
    private String postalCode;
    @Column(length = 15, nullable = false)
    private String province;

    public OrderAddress() {
    }

    public OrderAddress(Long orderID, String houseNumber, String streetName, String surburb, String city, String postalCode, String province) {
        this.orderID = orderID;
        this.houseNumber = houseNumber;
        this.streetName = streetName;
        this.surburb = surburb;
        this.city = city;
        this.postalCode = postalCode;
        this.province = province;
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getSurburb() {
        return surburb;
    }

    public void setSurburb(String surburb) {
        this.surburb = surburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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
        if (!(object instanceof OrderAddress)) {
            return false;
        }
        OrderAddress other = (OrderAddress) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderAddress{" + "id=" + id + ", orderID=" + orderID + ", houseNumber=" + houseNumber + ", streetName=" + streetName + ", surburb=" + surburb + ", city=" + city + ", postalCode=" + postalCode + ", province=" + province + '}';
    }

}
