package com.example.hardwarestore.invoice;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/generatePDF/{invoiceId}")
    public ResponseEntity<byte[]> generatePDF(@PathVariable("invoiceId") Long invoiceId) {
        Invoice invoice = invoiceService.getInvoice(invoiceId);
        if (invoice != null) {
            try {
                // Prepare output stream
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Add Invoice ID
                document.add(new Paragraph("Invoice ID: " + invoice.getId()));

                // Create table for invoice details
                Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
                table.addCell("Product Name");
                table.addCell("Price");
                table.addCell("Quantity");
                table.addCell("Total Price");

                for (InvoiceDetail detail : invoice.getDetails()) {
                    table.addCell(detail.getProductName());
                    table.addCell(detail.getPrice().toString());
                    table.addCell(detail.getQuantity().toString());
                    table.addCell(String.valueOf(detail.getPrice() * detail.getQuantity()));
                }

                document.add(table);

                // Add total price
                document.add(new Paragraph("Total Price: " + invoice.getTotalPrice()));

                // Close PDF
                document.close();

                // Return PDF as response
                byte[] pdfBytes = baos.toByteArray();
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header("Content-Disposition", "inline; filename=invoice.pdf")
                        .contentLength(pdfBytes.length)
                        .body(pdfBytes);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
