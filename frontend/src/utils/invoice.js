import {jsPDF} from "jspdf";

//  prijs  bepalen
function resolveItemUnitPrice(item) {
    //
    if (typeof item.priceAtPurchase === "number" && item.priceAtPurchase > 0) {
        return item.priceAtPurchase;
    }

    if (typeof item.price === "number" && item.price > 0) {
        return item.price;
    }

    if (typeof item.unitPrice === "number" && item.unitPrice > 0) {
        return item.unitPrice;
    }

    if (item.product && typeof item.product.price === "number" && item.product.price > 0) {
        return item.product.price;
    }

    if (
        item.product &&
        typeof item.product.rentalPrice === "number" &&
        item.product.rentalPrice > 0
    ) {
        return item.product.rentalPrice;
    }

    if (typeof item.total === "number" && item.total > 0 && item.quantity) {
        return item.total / item.quantity;
    }

    return 0;
}

export function makeInvoicePdf(order) {
    const doc = new jsPDF();

    const createdAtDate = order.createdAt
        ? new Date(order.createdAt)
        : new Date();
    const datumTekst = createdAtDate.toLocaleString("nl-NL");

    doc.setFontSize(16);
    doc.text("WebBazar - Factuur", 20, 20);

    doc.setFontSize(12);
    doc.text(`Order ID: ${order.id}`, 20, 30);
    doc.text(`Datum: ${datumTekst}`, 20, 38);

    doc.text("Status: Betaald", 20, 46);

    const customerName =
        order.customerName ||
        order.customer?.name ||
        order.user?.name ||
        order.name;

    if (customerName) {
        doc.text(`Klant: ${customerName}`, 20, 54);
    }

    let y = customerName ? 64 : 60;
    doc.text("Items:", 20, y);
    y += 8;

    (order.items || []).forEach((item, index) => {
        const title =
            item.productTitle ||
            item.product?.title ||
            "Onbekend product";

        const quantity = item.quantity || 1;
        const unitPrice = resolveItemUnitPrice(item);
        const lineTotal = unitPrice * quantity;

        const typeLabel = item.type || "";

        // Toon het regeltotaal (aantal × prijs)
        const line = `${index + 1}. ${title} x${quantity} (${typeLabel}) - €${lineTotal.toFixed(
            2
        )}`;
        doc.text(line, 20, y);
        y += 8;
    });

    y += 4;

    // Totaal berekenen
    const totalValue =
        typeof order.total === "number" && order.total > 0
            ? order.total
            : (order.items || []).reduce((sum, item) => {
                const quantity = item.quantity || 1;
                const unitPrice = resolveItemUnitPrice(item);
                return sum + unitPrice * quantity;
            }, 0);

    doc.text(`Totaal: €${totalValue.toFixed(2)}`, 20, y);

    return doc;
}

export function makeOrderXML(order) {
    const esc = (value) =>
        String(value).replace(/[<>&'"]/g, (c) =>
            ({"<": "&lt;", ">": "&gt;", "&": "&amp;", '"': "&quot;", "'": "&apos;"}[
                c
                ])
        );

    const createdAtValue = order.createdAt || new Date().toISOString();

    const itemsXml = (order.items || [])
        .map((item) => {
            const quantity = item.quantity || 1;
            const unitPrice = resolveItemUnitPrice(item);

            return `
    <item>
      <productId>${esc(item.productId ?? item.product?.id ?? "")}</productId>
      <title>${esc(
                item.productTitle ?? item.product?.title ?? "Onbekend product"
            )}</title>
      <type>${esc(item.type ?? "")}</type>
      <quantity>${quantity}</quantity>
      <price>${unitPrice.toFixed(2)}</price>
    </item>`;
        })
        .join("");

    const totalValue =
        typeof order.total === "number" && order.total > 0
            ? order.total
            : (order.items || []).reduce((sum, item) => {
                const quantity = item.quantity || 1;
                const unitPrice = resolveItemUnitPrice(item);
                return sum + unitPrice * quantity;
            }, 0);

    return `<?xml version="1.0" encoding="UTF-8"?>
<order id="${order.id}" total="${totalValue.toFixed(
        2
    )}" createdAt="${esc(createdAtValue)}">
  <status>Betaald</status>
  <items>${itemsXml}
  </items>
</order>`;
}
