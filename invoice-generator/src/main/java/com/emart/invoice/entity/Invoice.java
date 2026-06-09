package com.emart.invoice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "generated_date")
    private LocalDateTime generatedDate;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    @Column(name = "email_sent")
    private Boolean emailSent;

    @Column(name = "email_sent_date")
    private LocalDateTime emailSentDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "generated_by_user_id")
    private User generatedByUser;

    public Invoice() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }

    public Boolean getEmailSent() { return emailSent; }
    public void setEmailSent(Boolean emailSent) { this.emailSent = emailSent; }

    public LocalDateTime getEmailSentDate() { return emailSentDate; }
    public void setEmailSentDate(LocalDateTime emailSentDate) { this.emailSentDate = emailSentDate; }

    public User getGeneratedByUser() { return generatedByUser; }
    public void setGeneratedByUser(User generatedByUser) { this.generatedByUser = generatedByUser; }
}
