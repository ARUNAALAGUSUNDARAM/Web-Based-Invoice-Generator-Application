package com.example.hardwarestore.invoice;

public interface InvoiceService {
    Invoice saveInvoice(Invoice invoice);

    Invoice getInvoice(Long id);
}
