package com.emart.invoice.service;

import com.emart.invoice.entity.Order;
import com.emart.invoice.entity.OrderItem;
import com.emart.invoice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// iText PDF imports
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Optional;




@Service
public class InvoiceService {

    @Autowired
    private OrderRepository orderRepository;

    public String generateInvoicePdf(Integer orderId) {
        // Step 1: Get order from database
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return "Error: Order not found with ID: " + orderId;
        }

        Order order = orderOpt.get();

        // Step 2: For now, let's just return order info as text
        return "Invoice for Order #" + order.getId() +
               "\nCustomer: " + order.getCustomer().getName() +
               "\nTotal: $" + order.getTotalAmount();
    }

    public byte[] generateInvoicePdfBytes(Integer orderId) throws Exception {
        // Step 1: Get order from database
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        Order order = orderOpt.get();

        // Step 2: Create PDF in memory
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Step 3: Add invoice header
        document.add(new Paragraph("INVOICE")
            .setFontSize(24)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\n"));

        // Step 4: Add company info
        document.add(new Paragraph("eMart Invoice Generator")
            .setFontSize(16)
            .setBold());
        document.add(new Paragraph("123 Business Street"));
        document.add(new Paragraph("City, State 12345"));
        document.add(new Paragraph("Phone: (555) 123-4567"));
        document.add(new Paragraph("\n"));

        // Step 5: Add invoice details
        document.add(new Paragraph("Invoice #: INV-" + order.getId())
            .setFontSize(12)
            .setBold());
        document.add(new Paragraph("Date: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
        document.add(new Paragraph("Status: " + order.getStatus()));
        document.add(new Paragraph("\n"));

        // Step 6: Add customer info
        document.add(new Paragraph("Bill To:")
            .setFontSize(12)
            .setBold());
        document.add(new Paragraph(order.getCustomer().getName()));
        document.add(new Paragraph(order.getCustomer().getEmail()));
        document.add(new Paragraph(order.getCustomer().getPhone()));
        document.add(new Paragraph(order.getCustomer().getAddress()));
        document.add(new Paragraph("\n"));

        // Step 7: Add items table
        Table table = new Table(4);
        table.setWidth(500);

        // Table headers
        table.addHeaderCell(new Cell().add(new Paragraph("Item").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Price").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));

        // Add order items
        for (OrderItem item : order.getOrderItems()) {
            table.addCell(new Cell().add(new Paragraph(item.getProductName())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
            table.addCell(new Cell().add(new Paragraph("$" + item.getPrice().toString())));
            table.addCell(new Cell().add(new Paragraph("$" + item.getSubtotal().toString())));
        }

        document.add(table);
        document.add(new Paragraph("\n"));

        // Step 8: Add total
        document.add(new Paragraph("Total Amount: $" + order.getTotalAmount().toString())
            .setFontSize(14)
            .setBold()
            .setTextAlignment(TextAlignment.RIGHT));

        // Step 9: Close document and return bytes
        document.close();

        return baos.toByteArray();
    }
}