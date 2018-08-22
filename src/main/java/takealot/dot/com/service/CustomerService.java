/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takealot.dot.com.data.access.manager.CustomerRepository;
import takealot.dot.com.entity.Customer;
import takealot.dot.com.restcontroller.CustomerController;
import takealot.dot.com.service.message.helpers.Email;
import takealot.dot.com.service.message.helpers.EmailEventEmitter;

/**
 *
 * @author Sifiso
 */

//TODO Centralizaion of methods/function that are common in CustomerService and this(AdminService) service 
   
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository userRepository;
    @Autowired
    private EmailEventEmitter emailEventEmitter;

    public HashMap registerCustomer(Customer user){

        List<Customer> allUsers = getAllUsers();

        HashMap response = new HashMap();
        boolean isUnique = true;
        String message = "";
        String url = "";

        for (int i = 0; i < allUsers.size(); i++) {
            Customer arrayUser = allUsers.get(i);

            if (arrayUser.getEmail().equals(user.getEmail())) {
                isUnique = false;
            }
        }

        if (isUnique) {
            userRepository.save(user);

            String emailBody = "Hi " + user.getFirstname() + "<br/></br> Thank you for creating an account on takealot.com."
                    + " Your registered email address is <b>" + user.getEmail() + ". </b><br><br>Once again Thank you for using Takealot.com Online Store.";
            String subject = "Mmetsa's bakery Registration Confirmation";

            Email email = new Email(emailBody,user.getEmail(),subject);
            this.emailEventEmitter.emitEmailEvent(email);

            message = "You are successfully registered. Check your email account for confirmation email";
            //For AngularJS
            url = "/";
            response.put("status", "CREATED");

        } else {
            message = "You are already registered, Please use your details to login.";
            //For AngularJS
            url = "/login";
            response.put("status", "CONFLICT");
        }

        response.put("message", message);
        response.put("url", url);

        return response;
    }

//    public List<UserTB> getAllUserByRole(String role) {
//
//        return userRepository.findByUserRole(role);
//    }
    public HashMap login(Customer customer, HttpSession session) {

        List<Customer> allUsers = getAllUsers();

        String email = customer.getEmail();
        String password = customer.getPassword();

        String message = "Please register first";

        //AngularJS
        String url = "/login";

        String sessionID = "";
        String status = null;
        Customer userIn = null;

        HashMap response = new HashMap();

        for (int i = 0; i < allUsers.size(); i++) {
            Customer arrayUser = allUsers.get(i);
            
            if (arrayUser.getEmail().equals(email)) {

                if (arrayUser.getPassword().equals(password)) {
                    status = "FOUND";
                    message = "You have successfully logged in";

                    //AngularJS
                    url = "/";
                    userIn = arrayUser;
                    sessionID = session.getId();

                    markAsSignedIn(session, email);

                    break;
                } else {
                    status = "NOT_FOUND";
                    message = "Email and Password entered doesn't match";
                }
                //break to outside of the loop if the email exist
                i = allUsers.size() + 1;
            } else {
                status = "NOT_FOUND";
                message = "Email and Password entered doesn't match";
            }

        }

        response.put("status", status);
        response.put("url", url);
        response.put("message", message);
        response.put("sessionID", sessionID);
        response.put("userIn", userIn);

        return response;
    }

    public List<Customer> getAllUsers() {
        Iterable<Customer> tempAllUser = userRepository.findAll();
        List<Customer> allCustomers = new ArrayList<>();

        tempAllUser.forEach(allCustomers::add);

        return allCustomers;
    }

    public HashMap updateProfile(Customer cus, String sessionID) {

        HashMap response = new HashMap();

        String message = cus.getFirstname() + " " + cus.getLastname() + " your profile is not updated";
        String status = "NOT_FOUND";

        Customer customer = userRepository.findOne(cus.getId());

        if (customerHasLogon(sessionID, customer.getEmail())) {

            cus = userRepository.save(cus);

            status = "FOUND";
            message = cus.getFirstname() + " " + cus.getLastname() + " your profile is updated";
        }

        response.put("status", status);
        response.put("userIn", cus);
        response.put("message", message);
        response.put("sessionID", sessionID);

        return response;
    }

    public HashMap findCustomer(String email) {

        HashMap response = new HashMap();
        String message = "";
        Customer customer = (Customer) userRepository.findByEmail(email);

        if (customer == null) {
            message = "There is no record for email entered, please enter the correct one.";
            response.put("message", message);
            response.put("status", "FAILED");

        } else {

            response.put("status", "OK");
            response.put("customerQ", customer.getSecurityQuestuion());
        }

        return response;
    }

    public HashMap completePasswordRetrival(String customerData) throws JSONException, MessagingException {

        HashMap response = new HashMap();

        JSONObject obj = new JSONObject(customerData);

        String answer = obj.getString("answer");
        String emailAdddress = obj.getString("email");

        Customer customer = (Customer) userRepository.findByEmail(emailAdddress);

        String message = "You have provided the wrong answer, Please enter the correct one.";
        String status = "FAILED";

        if (answer.equals(customer.getAnswer())) {

            //send an email to customer
            String emailBody = "Hi " + customer.getFirstname() + "Your Password is: <b>" + customer.getPassword() + "</b><br><br>Thank you for using Takealot.com Online Store.";
            String subject = "Email Recovery";

            Email email = new Email(emailBody,emailAdddress,subject);
            this.emailEventEmitter.emitEmailEvent(email);

            status = "OK";
            message = "An email was sent to you with your password";
        }

        response.put("email", emailAdddress);
        response.put("status", status);
        response.put("message", message);

        return response;
    }

    public HashMap getQuestion(String email) {

        HashMap response = new HashMap();

        Customer customer = (Customer) userRepository.findByEmail(email);

        String question = customer.getSecurityQuestuion();

        response.put("question", question);

        return response;
    }

    public HashMap logout(String sessionID, Long userID) {

        HashMap response = new HashMap();

        //find user login status
        Customer customer = userRepository.findOne(userID);

        if (customerHasLogon(sessionID, customer.getEmail())) {

            String status = "FAILED";
            String message = "We are strangling to log you out. Please try again";
            boolean logoutStatus = markAsSignedOut(customer.getEmail());
            
            if (logoutStatus) {
                status = "OK";
                message = "Log out successfully";
            }

            response.put("status", status);

        }

        return response;
    }

    public boolean customerHasLogon(String sessionID, String email) {

        boolean hasLogon = false;
        String storedSessionID = (String) CustomerController.logonCustomerIds.get(email);

        if (storedSessionID != null) {
            if (storedSessionID.equals(sessionID)) {
                hasLogon = true;
            }
        }

        return hasLogon;
    }

    private String markAsSignedIn(HttpSession session, String email) {
        String sessionID = session.getId();

        //Use email as a key to sessionID of the signed in user
        CustomerController.logonCustomerIds.put(email, sessionID);

        return sessionID;
    }

    private boolean markAsSignedOut(String email) {
        
        return  CustomerController.logonCustomerIds.remove(email) != null;
    }

}
