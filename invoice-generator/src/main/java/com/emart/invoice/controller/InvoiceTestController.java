package com.emart.invoice.controller;

import com.emart.invoice.dto.EmailRequest;
import com.emart.invoice.service.EmailService;
import com.emart.invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/invoices")
public class InvoiceTestController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/hello")
    public String hello() {
        return "Invoice Controller is working!";
    }

    @GetMapping("/test/{orderId}")
    public String testInvoice(@PathVariable Integer orderId) {
        return invoiceService.generateInvoicePdf(orderId);
    }

    @GetMapping("/pdf/{orderId}")
    public ResponseEntity<byte[]> previewPdfInvoice(@PathVariable Integer orderId) {
        try {
            byte[] pdfBytes = invoiceService.generateInvoicePdfBytes(orderId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "invoice-" + orderId + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Order not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // PDF generation error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{orderId}")
    public ResponseEntity<byte[]> downloadPdfInvoice(@PathVariable Integer orderId) {
        try {
            byte[] pdfBytes = invoiceService.generateInvoicePdfBytes(orderId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice-" + orderId + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Order not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // PDF generation error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/email/{orderId}")
    public ResponseEntity<String> emailInvoice(@PathVariable Integer orderId, @RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendInvoiceEmail(
                orderId,
                emailRequest.getRecipientEmail(),
                emailRequest.getSubject(),
                emailRequest.getMessage()
            );

            return new ResponseEntity<>("Email sent successfully!", HttpStatus.OK);
        } catch (RuntimeException e) {
            // Order not found or authentication failed
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Email sending error
            return new ResponseEntity<>("Failed to send email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/email-test/{orderId}")
    public ResponseEntity<String> emailInvoiceTest(@PathVariable Integer orderId) {
        try {
            emailService.sendInvoiceEmail(
                orderId,
                "sherinshibu149@gmail.com", // Send to the same email for testing
                "Test Invoice #" + orderId,
                "This is a test email from the browser!"
            );
            return new ResponseEntity<>("Email sent successfully to sherinshibu149@gmail.com!", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Order not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}