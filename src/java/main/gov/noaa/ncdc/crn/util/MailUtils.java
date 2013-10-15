package gov.noaa.ncdc.crn.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Scraped from http://old.nabble.com/how-to-create-mail-from-jav-or-jsp-application-td11023135s19691.html
 */
public class MailUtils {
    /**
     * Send an email to a list of recipients
     * @param recipients Array of email addresses
     * @param subject email subject
     * @param message email message
     * @param from sender's email address
     * @throws MessagingException if can't connect to email server. NOTE that as of 11/9/11 sending unauthenticated
     * email is unsupported from all Windows desktops but should run on all Linux servers
     */
    public static void sendMail(String recipients[], String subject, String message, String from)
            throws MessagingException {
        boolean debug = false;

        // Set the host smtp address
        Properties props = new Properties();
        // localhost is the default mail.host and therefore mail.smtp.host
        // props.put("mail.smtp.host", "localhost");
        // 1/5/12 you can use this configuration, but only if you're sending
        // email *to* noaa.gov email addresses (from doesn't matter and doesn't
        // need to be a real email account
        // props.put("mail.smtp.host", "mta.nems.noaa.gov");

        // create some properties and get the default Session
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(debug);

        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Optional : You can also set your custom headers in the Email if you Want
        // msg.addHeader("MyHeaderName", "myHeaderValue");

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }

}
