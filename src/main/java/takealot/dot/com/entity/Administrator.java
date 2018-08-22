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
import javax.persistence.Lob;

/**
 *
 * @author Mmetsa
 */
@Entity
public class Administrator implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(length = 30)
    private String firstname;
    @Column(length = 30)
    private String lastname;
    @Column(length = 40)
    private String email;
    @Column(length = 50)
    private String password;
    @Lob
    private byte[] adminImage;
    @Column(nullable=false, length=25)
    private String imageAdditonalInfo;

    public Administrator() {
    }

    public Administrator(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public Administrator(String firstname, String lastname, String email, String password, byte[] adminImage, String imageAdditonalInfo) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.adminImage = adminImage;
        this.imageAdditonalInfo = imageAdditonalInfo;
    }
    
    public Administrator(Long id, String firstname, String lastname, String email, String password) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public byte[] getAdminImage() {
        return adminImage;
    }

    public void setAdminImage(byte[] adminImage) {
        this.adminImage = adminImage;
    }

    public String getImageAdditonalInfo() {
        return imageAdditonalInfo;
    }

    public void setImageAdditonalInfo(String imageAdditonalInfo) {
        this.imageAdditonalInfo = imageAdditonalInfo;
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
        if (!(object instanceof Administrator)) {
            return false;
        }
        Administrator other = (Administrator) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Administrator{" + "id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + ", email=" + email + ", password=" + password + ", adminImage=" + adminImage + ", imageAdditonalInfo=" + imageAdditonalInfo + '}';
    }

    
    
}
