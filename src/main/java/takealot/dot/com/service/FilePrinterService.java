/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import takealot.dot.com.entity.Administrator;
import takealot.dot.com.entity.Customer;
import takealot.dot.com.entity.CustomerOrder;
import takealot.dot.com.entity.OrderAddress;
import takealot.dot.com.entity.wrapper.ProductWrapper;

/**
 *
 * @author ABSMBNW
 */
@Service
public class FilePrinterService {

    private PDDocument document;
    private Document doc;

    public Document createPdfReport(OutputStream stream, Administrator admin, String title) throws DocumentException, BadElementException, IOException {
        doc = new Document();
        PdfWriter.getInstance(doc, stream);
        doc.open();
        addReportHead(title, admin);

        return doc;
    }

    public void addReportHead(String reportTitle, Administrator admin) throws DocumentException, BadElementException, IOException {

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);

        PdfPCell cell = new PdfPCell();
        Image image = Image.getInstance("src/main/resources/static/images/bakery-logo.JPG");
        cell.setImage(image);
        table.addCell(cell);

        table.addCell(getEmptyCellWithNoBorders(2));

        Font font = new Font();
        font.setSize(8);

        cell = new PdfPCell();
        cell.setPadding(10f);
        cell.addElement(new Paragraph(admin.getFirstname() + " " + admin.getLastname(), font));
        Date date = new Date();
        cell.addElement(new Paragraph(formatDate(date) + ", " + formatTime(date), font));
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(Rectangle.TOP);
        cell.setColspan(4);
        cell.setMinimumHeight(30f);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(BaseColor.BLACK);
        cell.setBorderWidth(2f);

        cell.setMinimumHeight(40f);
        cell.setColspan(4);

        cell.addElement(getTitleParagraph(reportTitle));

        table.addCell(cell);
        this.doc.add(table);
    }

    private PdfPCell getEmptyCellWithNoBorders(int colSpan) {
        PdfPCell cell = new PdfPCell();
        cell.setColspan(colSpan);
        cell.setPadding(5f);
        cell.setMinimumHeight(15f);
        cell.setBorder(Rectangle.NO_BORDER);

        return cell;
    }

    private String formatDate(Date date) {
        String formattedDate = "No date";
        if (date != null) {

            String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            formattedDate = cal.get(Calendar.DAY_OF_MONTH) + " " + months[Calendar.MONTH] + " " + cal.get(Calendar.YEAR);

        }

        return formattedDate;
    }

    private String formatTime(Date date) {

        String time = "No time";
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            String min = cal.get(Calendar.MINUTE) < 10 ? "0" + cal.get(Calendar.MINUTE) : "" + cal.get(Calendar.MINUTE);
            String sec = cal.get(Calendar.SECOND) < 10 ? "0" + cal.get(Calendar.SECOND) : "" + cal.get(Calendar.SECOND);

            time = cal.get(Calendar.HOUR_OF_DAY) + ":" + min + ":" + sec;
        }
        
        return time;
    }

    private Paragraph getTitleParagraph(String title) {
        Font font = new Font();
        font.setStyle(Font.BOLD);
        font.setSize(15);

        Paragraph paragraph = new Paragraph(title, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);

        return paragraph;
    }

    private PdfPCell getSubTitle(String title, int colSpan) {
        Font font = new Font();
        font.setSize(10);
        font.setStyle(Font.BOLD);

        PdfPCell cell = new PdfPCell();
        cell.setColspan(colSpan);
        cell.setPaddingBottom(10);
        cell.setMinimumHeight(10);
        cell.addElement(new Paragraph(title, font));

        return cell;
    }

    public double calculateTotalCost(List<ProductWrapper> orderProducts) {
        double totalCost = 0;
        for (ProductWrapper pw : orderProducts) {

            totalCost += pw.getProduct().getQuantity() * pw.getProduct().getPrice();
        }

        return totalCost;
    }

    private PdfPTable addCustomerOrderInfoToReport(CustomerOrder cusOrder, List<ProductWrapper> orderProducts, PdfPTable table) throws BadElementException, IOException {

        Font font = new Font();
        font.setSize(11);
        font.setStyle(Font.BOLD);

        table.addCell(new Paragraph("#", font));
        table.addCell(new Paragraph("DATE & TIME", font));
        table.addCell(new Paragraph("STATUS", font));
        table.addCell(new Paragraph("ORDER TOTAL COST", font));
        table.setHeaderRows(1);

        table.addCell(getEmptyCellWithNoBorders(4));

        font = new Font();
        font.setSize(8);
        font.setStyle(Font.NORMAL);

        table.addCell(new Paragraph(cusOrder.getId().toString(), font));

        String dateAndTime = formatDate(cusOrder.getCustOrderDate()) + ", " + formatTime(cusOrder.getCustOrderTime());
        table.addCell(new Paragraph(dateAndTime, font));

        table.addCell(new Paragraph(cusOrder.getOrderStatus(), font));
        table.addCell(new Paragraph("R " + calculateTotalCost(orderProducts), font));

        table.addCell(getEmptyCellWithNoBorders(4));

        for (ProductWrapper pw : orderProducts) {
            table.addCell(Image.getInstance(pw.getProduct().getProductImage()));
            table.addCell(addOrderProducts(pw));
        }

        return table;
    }

    private PdfPCell addOrderProducts(ProductWrapper pw) throws BadElementException, IOException {
        PdfPCell cell = new PdfPCell();

        Font tFont = new Font();
        tFont.setSize(9);
        tFont.setStyle(Font.BOLD);

        Font vFont = new Font();
        vFont.setSize(8);
        vFont.setStyle(Font.NORMAL);

        cell.addElement(new Paragraph("PRODUCT NAME", tFont));
        cell.addElement(new Paragraph(pw.getProduct().getProductName(), vFont));

        cell.addElement(new Paragraph("PRICE PER EACH", tFont));
        cell.addElement(new Paragraph("R" + pw.getProduct().getPrice(), vFont));

        cell.addElement(new Paragraph("QUANTITY", tFont));
        cell.addElement(new Paragraph(pw.getProduct().getQuantity() + "", vFont));

        cell.addElement(new Paragraph("SUB TOTAL", tFont));
        cell.addElement(new Paragraph("R " + (pw.getProduct().getQuantity() * pw.getProduct().getPrice()), vFont));

        return cell;
    }

    private PdfPTable addOrderAddressInfoToReport(OrderAddress address, PdfPTable table) {

        PdfPCell cell = new PdfPCell();
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);

        Font font = new Font();
        font.setSize(8);

        cell.addElement(new Paragraph(address.getId() + " " + address.getHouseNumber() + " " + address.getStreetName(), font));
        cell.addElement(new Paragraph(address.getSurburb() + " " + address.getCity(), font));
        cell.addElement(new Paragraph(address.getProvince(), font));
        cell.addElement(new Paragraph(address.getHouseNumber(), font));

        table.addCell(cell);

        return table;
    }

    private PdfPTable addCustomerInfoToReport(Customer customer, PdfPTable table) {

        Font font = new Font();
        font.setSize(8);

        PdfPCell cell = new PdfPCell();
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);

        cell.addElement(new Paragraph(customer.getId() + " " + customer.getLastname() + " " + customer.getFirstname(), font));
        cell.addElement(new Paragraph(customer.getCellphonNumber(), font));
        cell.addElement(new Paragraph(customer.getEmail(), font));
        cell.addElement(new Paragraph(customer.getGender(), font));

        table.addCell(cell);

        return table;
    }

    public void addAllOrderInfoToReport(Customer customer, OrderAddress address, CustomerOrder cusOrder, List<ProductWrapper> orderProducts) throws DocumentException, BadElementException, IOException {

        PdfPTable table = new PdfPTable(new float[]{25f, 25f, 25f, 25f});
        table.setWidthPercentage(100f);

        table.addCell(getEmptyCellWithNoBorders(4));

        /**
         * ***************************************************************************************************************************
         * Add order owner (customer) and shipping address.
         * ***************************************************************************************************************************
         */
        table.addCell(getSubTitle("Customer details", 2));
        table.addCell(getSubTitle("Shipping address details", 2));

        table = addCustomerInfoToReport(customer, table);
        table = addOrderAddressInfoToReport(address, table);

        table.addCell(getEmptyCellWithNoBorders(4));

        /**
         * ***************************************************************************************************************************
         * Add order info.
         * ***************************************************************************************************************************
         */
        table.addCell(getSubTitle("Order products details", 4));
        table.addCell(getEmptyCellWithNoBorders(4));
        table = addCustomerOrderInfoToReport(cusOrder, orderProducts, table);
        table.addCell(getEmptyCellWithNoBorders(4));
        this.doc.add(table);
    }

    public void printCustomerLoginReport(List<Customer> customers) throws DocumentException {

        PdfPTable table = new PdfPTable(new float[]{15f, 30f, 10f, 20f, 25f});
        table.setWidthPercentage(100f);

        table.addCell(getEmptyCellWithNoBorders(5));

        Font font = new Font();
        font.setSize(9);
        font.setStyle(Font.BOLD);

        table.addCell(new Paragraph("#", font));
        table.addCell(new Paragraph("FIRSTNAME & LASTNAME", font));
        table.addCell(new Paragraph("GENDER", font));
        table.addCell(new Paragraph("DATE OF BIRTH", font));
        table.addCell(new Paragraph("LAST LOGIN", font));
        table.setHeaderRows(1);

//        table.addCell(getEmptyCellWithNoBorders(4));
        for (Customer cus : customers) {

            font = new Font();
            font.setSize(9);
            font.setStyle(Font.NORMAL);

            table.addCell(new Paragraph(cus.getId() + "", font));
            table.addCell(new Paragraph(cus.getFirstname() + " " + cus.getLastname(), font));
            table.addCell(new Paragraph(cus.getGender(), font));
            table.addCell(new Paragraph(cus.getDateOfBirth(), font));
            table.addCell(new Paragraph(formatDate(cus.getLastLoginDate()) + ", " + formatTime(cus.getLastLoginDate()), font));

            table.addCell(getSubTitle(cus.getFirstname() + " " + cus.getLastname()+"' contact details", 5));

            Font font1 = new Font();
            font1.setSize(8);
            font1.setStyle(Font.BOLD);

            PdfPCell cell = new PdfPCell();
            cell.addElement(new Paragraph("EMAIL", font1));
            cell.addElement(new Paragraph("C.P. NUMBER", font1));
            table.addCell(cell);

            cell = new PdfPCell();
            cell.addElement(new Paragraph(cus.getEmail(), font));
            cell.addElement(new Paragraph(cus.getCellphonNumber(), font));
            cell.setColspan(4);

            table.addCell(cell);
            table.addCell(getEmptyCellWithNoBorders(5));
        }

        this.doc.add(table);
    }
}
