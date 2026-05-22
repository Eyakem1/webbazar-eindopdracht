package com.webbazar.service;

import com.webbazar.entity.Order;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    public byte[] renderInvoice(Order order) {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;

                // Null-safe velden
                Long orderId = order.getId();
                String orderIdStr = orderId != null ? String.valueOf(orderId) : "-";

                Instant createdAt = order.getCreatedAt();
                String createdStr = createdAt != null ? createdAt.toString() : "-";

                BigDecimal total = order.getTotal() != null ? order.getTotal() : BigDecimal.ZERO;
                String totalStr = total.toPlainString();

                String customerName = (order.getUser() != null && order.getUser().getName() != null)
                        ? order.getUser().getName()
                        : "Onbekend";

                String customerEmail = (order.getUser() != null && order.getUser().getEmail() != null)
                        ? order.getUser().getEmail()
                        : "-";

                //  conceptfactuur,
                text(cs, margin, y, 18, safe("Factuur "));
                y -= 30;

                text(cs, margin, y, 12, safe("Bestelnummer: " + orderIdStr));
                y -= 16;

                text(cs, margin, y, 12, safe("Datum: " + createdStr));
                y -= 16;

                text(cs, margin, y, 12, safe("Klant: " + customerName + " (" + customerEmail + ")"));
                y -= 24;

                text(cs, margin, y, 12, safe("Totaalbedrag: " + totalStr));
                y -= 24;

                text(cs, margin, y, 10, safe("Let op: betaling is gelukt)."));
            }

            doc.save(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Kon factuur niet genereren", e);
        }
    }

    private void text(PDPageContentStream cs, float x, float y, int size, String value) throws Exception {
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, size);
        cs.newLineAtOffset(x, y);
        cs.showText(value);
        cs.endText();
    }


// verwijder niet-ASCII tekens.

    private String safe(String s) {
        if (s == null) return "";

        //
        s = s.replace("€", "EUR");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            //
            if (c >= 32 && c <= 126) {
                sb.append(c);
            } else {
                //
                sb.append(' ');
            }
        }
        return sb.toString();
    }
}
