/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service.message.helpers;

/**
 *
 * @author ABSMBNW
 */
public class Email {
    private final String emailBody;
    private final String destinationAddress;
    private final String emailSubject;

    public Email(String emailBody, String destinationAddress, String emailSubject) {
        this.emailBody = emailBody;
        this.destinationAddress = destinationAddress;
        this.emailSubject = emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    @Override
    public String toString() {
        return "Email{" + "emailBody=" + emailBody + ", destinationAddress=" + destinationAddress + ", emailSubject=" + emailSubject + '}';
    }

}
