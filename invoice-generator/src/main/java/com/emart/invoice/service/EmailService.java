package com.emart.invoice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private InvoiceService invoiceService;

    public void sendInvoiceEmail(Integer orderId, String recipientEmail, String subject, String messageText) throws Exception {
        try {
            // Step 1: Generate PDF invoice
            System.out.println("=== GENERATING PDF FOR ORDER " + orderId + " ===");
            byte[] pdfBytes = invoiceService.generateInvoicePdfBytes(orderId);
            System.out.println("PDF generated successfully. Size: " + pdfBytes.length + " bytes");

            // Step 2: Send real email
            System.out.println("=== SENDING REAL EMAIL ===");
            System.out.println("To: " + recipientEmail);
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + messageText);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("sherinshibu149@gmail.com");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(createEmailBody(orderId, messageText), true);
            helper.addAttachment("invoice-" + orderId + ".pdf", new ByteArrayResource(pdfBytes));

            System.out.println("Attempting to send email...");
            mailSender.send(message);

            System.out.println("=== EMAIL SENT SUCCESSFULLY ===");
        } catch (Exception e) {
            System.err.println("=== EMAIL SENDING FAILED ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
    
    private String createEmailBody(Integer orderId, String customMessage) {
        return "<html><body>" +
               "<h2>Invoice #" + orderId + "</h2>" +
               "<p>Dear Customer,</p>" +
               "<p>" + customMessage + "</p>" +
               "<p>Please find your invoice attached as a PDF.</p>" +
               "<br>" +
               "<p>Thank you for your business!</p>" +
               "<p><strong>eMart Invoice Generator</strong></p>" +
               "<p>123 Business Street<br>" +
               "City, State 12345<br>" +
               "Phone: (555) 123-4567</p>" +
               "</body></html>";
    }
}
