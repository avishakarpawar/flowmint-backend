 
package com.flowmint.flowmint_backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.flowmint.flowmint_backend.entity.Invoice;
import com.flowmint.flowmint_backend.repository.InvoiceRepository;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:5173")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;

    public InvoiceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    // Get all invoices
    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // Get invoice by ID
    @GetMapping("/{id}")
    public Invoice getInvoiceById(@PathVariable Long id) {
        return invoiceRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    // Create invoice
    @PostMapping
    public Invoice createInvoice(@RequestBody Invoice invoice) {

        invoice.setInvoiceNumber(
            "INV-" + UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase()
        );

        invoice.setInvoiceDate(LocalDate.now());
        invoice.setStatus("Pending");

        return invoiceRepository.save(invoice);
    }

    // Update invoice
    @PutMapping("/{id}")
    public Invoice updateInvoice(
            @PathVariable Long id,
            @RequestBody Invoice updatedInvoice) {

        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Invoice not found"));

        invoice.setCustomer(updatedInvoice.getCustomer());
        invoice.setAmount(updatedInvoice.getAmount());

        return invoiceRepository.save(invoice);
    }

    // Update payment status
    @PatchMapping("/{id}/status")
    public Invoice updateStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request) {

        Invoice invoice = invoiceRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Invoice not found"));

        if (!"Paid".equalsIgnoreCase(request.getStatus())
                && !"Pending".equalsIgnoreCase(request.getStatus())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid invoice status");
        }

        invoice.setStatus(
            request.getStatus().equalsIgnoreCase("Paid") ? "Paid" : "Pending"
        );

        return invoiceRepository.save(invoice);
    }

    // Delete invoice
    @DeleteMapping("/{id}")
    public String deleteInvoice(@PathVariable Long id) {

        if (!invoiceRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Invoice not found");
        }

        invoiceRepository.deleteById(id);

        return "Invoice deleted successfully";
    }

    // Request body for status update
    public static class StatusRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
