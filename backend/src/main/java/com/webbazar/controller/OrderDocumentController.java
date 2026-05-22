package com.webbazar.controller;

import com.webbazar.entity.Order;
import com.webbazar.service.InvoiceService;
import com.webbazar.service.OrderService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderDocumentController {

    private final OrderService orderService;
    private final InvoiceService invoiceService;

    public OrderDocumentController(OrderService orderService, InvoiceService invoiceService) {
        this.orderService = orderService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{id}/invoice")
    @PreAuthorize("@ownershipGuard.canAccessOrder(#id, authentication) or hasRole('ADMIN')")
    public ResponseEntity<byte[]> invoice(@PathVariable Long id) {
        Order o = orderService.findByIdOrThrow(id);
        byte[] pdf = invoiceService.renderInvoice(o);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("factuur-order-" + id + ".pdf").build());
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
