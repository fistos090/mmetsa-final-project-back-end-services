/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takealot.dot.com.data.access.manager.AdminRepository;
import takealot.dot.com.entity.Administrator;
import takealot.dot.com.entity.wrapper.AdminWrapper;
import takealot.dot.com.entity.wrapper.ProductWrapper;
import takealot.dot.com.restcontroller.AdminController;
import takealot.dot.com.service.message.helpers.Email;
import takealot.dot.com.service.message.helpers.EmailEventEmitter;

/**
 *
 * @author Sifiso
 */
//TODO Centralizaion of methods/function that are common in CustomerService and this(AdminService) service 
@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private ImageManager imageManager;
    @Autowired
    private EmailEventEmitter emailEventEmitter;
    @Autowired
    private PasswordEncDecManager passwordEncDecManager;

    public HashMap registerAdmin(AdminWrapper adminWrapper, HttpSession session) throws UnsupportedEncodingException, Exception {

        Administrator admin = adminWrapper.getAdmin();
        System.out.println(admin);

        List<Administrator> allUsers = getAllUsers();

        HashMap response = new HashMap();
        HashMap loginResponse = null;
        boolean isUnique = true;
        String message = "";
        String url = "";

        for (int i = 0; i < allUsers.size(); i++) {
            Administrator arrayUser = allUsers.get(i);

            if (arrayUser.getEmail().equals(admin.getEmail())) {
                isUnique = false;
            }
        }

        if (isUnique) {
            admin.setPassword(passwordEncDecManager.encryptPassword(admin.getPassword()));
            Administrator savedAdmin = adminRepository.save(admin);

            //On successfully persisted/save
            if (savedAdmin.getId() != null) {

                loginResponse = login(admin, session);

                String emailBody = "Hi " + admin.getFirstname() + "<br/></br> Thank you for creating an account on takealot.com."
                        + " Your registered email address is <b>" + admin.getEmail() + ". </b><br><br>Once again Thank you for using Takealot.com Online Store.";
                String subject = "First Enteprise Online Bakery registration confirmation";

                // emailService.sendEmail(subject, emailBody, admin.getEmail());
                Email email = new Email(emailBody, admin.getEmail(), subject);
                this.emailEventEmitter.emitEmailEvent(email);

                message = "You are successfully registered and a confirmation email was sent to your email";

            }

            //For AngularJS
            url = "/";
            response.put("status", "CREATED");

        } else {
            message = "You are already registered, Please use your details to login.";
            //For AngularJS
            url = "/login";
            response.put("status", "CONFLICT");
        }

        response.put("loginResponse", loginResponse);
        response.put("message", message);
        response.put("url", url);

        return response;
    }

    public HashMap login(Administrator admin, HttpSession session) throws UnsupportedEncodingException, Exception {

        List<Administrator> allUsers = getAllUsers();

        String email = admin.getEmail();
        String password = admin.getPassword();

        String message = "It looks like you don't have an"
                + " account with us. Please register one first and then you can login";

        //AngularJS
        String url = "/login";

        String sessionID = "";
        String status = null;
        AdminWrapper adminWrapper = null;

        HashMap response = new HashMap();

        for (int i = 0; i < allUsers.size(); i++) {
            Administrator arrayUser = allUsers.get(i);

            if (arrayUser.getEmail().equals(email)) {
                String dencryptPassword = passwordEncDecManager.dencryptPassword(arrayUser.getPassword());

                System.out.println("dencryptPassword " + dencryptPassword);

                if (dencryptPassword.equals(password)) {
                    status = "FOUND";
                    message = "You have successfully logged in";

                    //AngularJS
                    url = "/";

                    sessionID = session.getId();

                    markAsSignedIn(session, email);

                    //Attaching admin image to admin logged in 
                    adminWrapper = new AdminWrapper(arrayUser, "ghghg,fddf");
                    System.out.println("adminWrapper " + adminWrapper);
                    //TODO - Check admin stock updates
                    break;
                } else {
                    status = "NOT_FOUND";
                    message = "Enter the correct password";
                }
                //break to outside of the loop if the email exist
                i = allUsers.size() + 1;
            } else {

                message = "Email address entered doesn't exist, Please enter the correct one.";
            }

        }

        response.put("status", status);
        response.put("url", url);
        response.put("message", message);
        response.put("sessionID", sessionID);
        response.put("userIn", adminWrapper);

        return response;
    }

    public HashMap logout(String sessionID, Long userID) {

        HashMap response = new HashMap();
        String status = "FAILED";

        //find user login status
        Administrator admin = adminRepository.findOne(userID);

        if (adminHasLogin(sessionID, admin.getEmail())) {

            boolean logoutStatus = markAsSignedOut(admin.getEmail());

            if (logoutStatus) {
                status = "OK";
            }
        }

        response.put("status", status);

        return response;
    }

    public List<Administrator> getAllUsers() {
        Iterable<Administrator> tempAllAdmin = adminRepository.findAll();
        List<Administrator> allAdmins = new ArrayList<>();

        tempAllAdmin.forEach(allAdmins::add);

        return allAdmins;
    }

    public boolean adminHasLogin(String sessionID, String email) {

        boolean hasLogin = false;
        String storedSessionID = (String) AdminController.logonAdminIds.get(email);

        if (storedSessionID != null) {
            if (storedSessionID.equals(sessionID)) {
                hasLogin = true;
            }
        }

        return hasLogin;
    }

    private String markAsSignedIn(HttpSession session, String email) {
        String sessionID = session.getId();

        //Use email as a key to sessionID of the signed in user
        AdminController.logonAdminIds.put(email, sessionID);

        return sessionID;
    }

    private boolean markAsSignedOut(String email) {

        return AdminController.logonAdminIds.remove(email) != null;
    }

    public HashMap completePasswordRetrival(String customerData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public HashMap findAdmin(String email) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
