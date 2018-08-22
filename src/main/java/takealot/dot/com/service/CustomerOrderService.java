/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.mail.MessagingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import takealot.dot.com.data.access.manager.AdminRepository;
import takealot.dot.com.data.access.manager.CustomerOrderRepository;
import takealot.dot.com.data.access.manager.CustomerRepository;
import takealot.dot.com.data.access.manager.OrderAddressRepository;
import takealot.dot.com.data.access.manager.OrderProductRepository;
import takealot.dot.com.data.access.manager.ProductRepository;
import takealot.dot.com.entity.Administrator;
import takealot.dot.com.entity.Customer;
import takealot.dot.com.entity.CustomerOrder;
import takealot.dot.com.entity.OrderAddress;
import takealot.dot.com.entity.OrderProduct;
import takealot.dot.com.entity.Product;
import takealot.dot.com.entity.wrapper.ProductWrapper;

/**
 *
 * @author Sifiso
 */
@Service
public class CustomerOrderService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerOrderRepository custOrderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private OrderProductRepository orderProdRepository;
    @Autowired
    private OrderAddressRepository addressRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private EmailService emailService;

    public HashMap processOrder(String orderData) throws JSONException, MessagingException {

        HashMap response = new HashMap();

        JSONObject jObj = new JSONObject(orderData);

        JSONArray jArray = (JSONArray) jObj.get("orderItems");
        String sessionID = (String) jObj.get("sessionID");
        JSONObject addressObj = (JSONObject) jObj.get("addressInfo");

        JSONObject customerData = new JSONObject(jObj.getString("user"));
        Long customerID = customerData.getLong("id");

        //Create timestamp for the order
        Date date = new Date(System.currentTimeMillis());

        //TODO fix the shipping cost issue. For now I am using Zero(0) as placeholder
        CustomerOrder custOrder = new CustomerOrder(customerID, 0, date);

        String url = "/";
        String message = "";

        Customer cus = customerRepository.findOne(customerID);

        //Persist customer order
        if (customerService.customerHasLogon(sessionID, cus.getEmail())) {

            List<CustomerOrder> orders = custOrderRepository.findByCustID(customerID);

            //Get number of orders before persisting the current one
            int numOfBefore = orders.size();

            //Persist the client order
            CustomerOrder latestOrder = custOrderRepository.save(custOrder);

            //Pesist order products and order destination address
            if (latestOrder != null) {

                String houseNumber = addressObj.getString("houseNumber");
                String streetName = addressObj.getString("streetName");
                String surburb = addressObj.getString("surburb");
                String city = addressObj.getString("city");
                String postalCode = addressObj.getString("postalCode");
                String province = addressObj.getString("province");

                //Pesist order address
                OrderAddress address = new OrderAddress(latestOrder.getId(), houseNumber, streetName, surburb, city, postalCode, province);
                addressRepository.save(address);

                //Pesist order products
                List<OrderProduct> orderProducts = createLineProduct(jArray, latestOrder.getId());
                orderProdRepository.save(orderProducts);

                //Update the quantity of products left in store
                ArrayList<Product> allProducts = productService.getAllProducts();
                updateQuantity(allProducts, orderProducts);

                //Get number of orders after persisting the current one
                orders = custOrderRepository.findByCustID(customerID);
                int numOfAfter = orders.size();

                //Get all specific customer orders
                //If the is an increase in number of client orders that means the order was persisted successfully
                if (numOfBefore != numOfAfter) {
                    //Get customer's latest order number
                    Long OrderNumber = latestOrder.getId();
                    message = "Your order is successfully placed. You will recieve a confirmation email with your order number";

                    String subject = "TAKE-A-LOT ORDER CONFIRMATION";
                    String emailAddress = customerData.getString("email");

                    String firstname = customerData.getString("firstname");
                    String emailBody = createEmailBody(OrderNumber, firstname, allProducts, orderProducts);
                    System.out.println(emailBody);

                    emailService.sendEmail(subject, emailBody, emailAddress);

                }

            }

            response.put("status", "CREATED");

        } else {

            response.put("status", "CONFLICT");
            message = "It looks like you did not login";
            url = "/login";

        }

        response.put("message", message);
        response.put("url", url);

        return response;
    }

    private List<OrderProduct> createLineProduct(JSONArray jArray, Long orderID) throws JSONException {

        List<OrderProduct> orderProducts = new ArrayList<>();

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject obj = (JSONObject) jArray.get(i);

            int quantity = obj.getInt("QUANTITY");
            int productId = obj.getInt("ID");

            OrderProduct product = new OrderProduct(productId, quantity, orderID);
            orderProducts.add(product);
        }

        return orderProducts;
    }

    public HashMap getAllOrders(String sessionID, Long adminID) {

        HashMap response = new HashMap();

        String message = "";
        String status = "";

        Administrator admin = adminRepository.findOne(adminID);

        if (adminService.adminHasLogin(sessionID, admin.getEmail())) {

            List<CustomerOrder> allOrders = getAllOrders();

            if (allOrders.isEmpty()) {
                message = "There are no olders";
                status = "FAILED";
            } else {
                message = allOrders.size() + " order(s) found";
                status = "OK";
            }

            response.put("allOrders", allOrders);

        }

        response.put("message", message);
        response.put("status", status);

        return response;
    }

    public HashMap getCustomerOrders(Customer cus, String sessionID) {
        HashMap response = new HashMap();
        List<CustomerOrder> customerOrders = new ArrayList<>();
        String message = "It looks like you did not login";
        String status = "FAILED";

        if (this.customerService.customerHasLogon(sessionID, cus.getEmail())) {
            customerOrders = this.custOrderRepository.findByCustID(cus.getId());
            message = "Fetch successfully";
            status = "OK";

        }
        response.put("customerOrders", customerOrders);
        response.put("message", message);
        response.put("status", status);

        return response;
    }

    public HashMap getCustomerOrderProducts(Long orderId) throws UnsupportedEncodingException {
        HashMap response = new HashMap();

        List<OrderProduct> ordersProducts = this.orderProdRepository.findByOrderID(orderId);
        HashMap shopProducts = this.productService.getAllShopProduct();
        List<ProductWrapper> wrappedProducts = (List<ProductWrapper>) shopProducts.get("products");

        List<ProductWrapper> products = new ArrayList();
        for (OrderProduct orderProduct : ordersProducts) {

            for (ProductWrapper productWrapper : wrappedProducts) {
                if (orderProduct.getProductId() == productWrapper.getProduct().getId()) {
                    products.add(productWrapper);
                }
            }

        }

        response.put("orderProducts", products);

        return response;
    }

    private List<CustomerOrder> getAllOrders() {

        Iterable<CustomerOrder> tempAllUser = custOrderRepository.findAll();
        List<CustomerOrder> allOrders = new ArrayList<>();

        tempAllUser.forEach(allOrders::add);

        return allOrders;
    }

    private void updateQuantity(ArrayList<Product> allProducts, List<OrderProduct> orderProducts) {

        for (int i = 0; i < allProducts.size(); i++) {
            Product product = allProducts.get(i);

            for (int j = 0; j < orderProducts.size(); j++) {
                OrderProduct orderProduct = orderProducts.get(j);

                if (product.getId() == orderProduct.getProductId()) {
                    int remainingQuantity = product.getQuantity() - orderProduct.getQuantity();
                    product.setQuantity(remainingQuantity);

                    productRepository.save(product);
                }
            }

        }
    }

    private String createEmailBody(Long OrderNumber, String firstname, ArrayList<Product> allProducts, List<OrderProduct> lineProducts) {

        String rows = "";
        double total = 0.00;
        int totalQuantity = 0;
        int number = 0;

        String emailBody = "<h4>Hi " + firstname + "</h4><br/><br/> Order Number : <b>" + OrderNumber + ""
                + "</b><br/><br/>Your order is successfully placed and it have the following item(s) :<br/><br/>"
                + "<table>"
                + "<thead>"
                + "<tr style=\"width: 450px;height: 35px;border: none;background-color: #1d78cb;\">"
                + "<td>#</td><td>Item Description</td><td>Item Price</td><td>Quantity</td><td>Sub Total</td>"
                + "</tr>"
                + "</thead>"
                + "<tbody>";

        for (int j = 0; j < lineProducts.size(); j++) {
            OrderProduct lineProduct = lineProducts.get(j);

            for (int y = 0; y < allProducts.size(); y++) {

                if (allProducts.get(y).getId() == lineProduct.getProductId()) {

                    Product product = allProducts.get(y);
                    number = number + 1;

                    double subTotal = product.getPrice() * lineProduct.getQuantity();
                    total = total + subTotal;
                    totalQuantity += lineProduct.getQuantity();

                    rows += "<tr style=\"width: 100%;height: 35px;border: none;\">"
                            + "<td>" + number + "</td>"
                            + "<td>" + product.getProductDesc() + "<br/>" + product.getCategory() + "</td>"
                            + "<td> R " + product.getPrice() + "</td>"
                            + "<td>" + lineProduct.getQuantity() + "</td>"
                            + "<td> R " + subTotal + "</td>"
                            + "</tr>";

                    y = allProducts.size() + 1;
                }

            }
        }

        rows += "<tr style=\"width: 100%;height: 35px;border: none;\">"
                + "<td></td><td></td>"
                + "<td> TOTAL :</td>"
                + "<td>" + totalQuantity + "</td>"
                + "<td> R " + total + "</td>"
                + "</tr>"
                + "<tbody>"
                + "</table>";

        emailBody = emailBody + rows;

        return emailBody;
    }

//    public HashMap printInvoice(String sessionID, Long adminID) {
//        HashMap response = new HashMap();
//
//        String message = "";
//        String status = "";
//
//        UserTB dUser = userRepository.findOne(adminID);
//        String loginID = dUser.getLoginID();
//
//        if (sessionID.equals(loginID)) {
//
//            try {
//                ArrayList<Product> allProducts = productService.getAllProducts();
//                List<ClientOrder> allOrders = getAllOrders();
//
//                message = printerService.printInvoice(allOrders, allProducts);
//
//                if (allOrders.isEmpty()) {
//                    status = "FAILED";
//                } else {
//
//                    status = "OK";
//                }
//
//                response.put("allOrders", allOrders);
//
//            } catch (IOException ex) {
//                Logger.getLogger(ClientOrderService.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        }
//
//        response.put("message", message);
//        response.put("status", status);
//
//        return response;
//    }
}
