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
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "custID",length = 9)
    private Long id;
    @Column(length = 30)
    private String firstname;
    @Column(length = 30)
    private String lastname;
    @Column(length = 40)
    private String email;
    @Column(length = 50)
    private String password;
    @Column(length = 15)
    private String cellphonNumber;
    @Column(length = 7)
    private String gender;
    @Column(length = 20)
    private String dateOfBirth;
    
    @Column(length = 100)
    private String securityQuestuion;
    @Column(length = 100)
    private String answer;

    public Customer() {
    }

    public Customer(String firstname, String lastname, String email, String password, String cellphonNumber, String gender, String dateOfBirth, String securityQuestuion, String answer) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.cellphonNumber = cellphonNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.securityQuestuion = securityQuestuion;
        this.answer = answer;
    }

    public String getSecurityQuestuion() {
        return securityQuestuion;
    }

    public void setSecurityQuestuion(String securityQuestuion) {
        this.securityQuestuion = securityQuestuion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCellphonNumber() {
        return cellphonNumber;
    }

    public void setCellphonNumber(String cellphonNumber) {
        this.cellphonNumber = cellphonNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "takealot.dot.com.entity.Customer[ id=" + id + " ]";
    }
    
}
