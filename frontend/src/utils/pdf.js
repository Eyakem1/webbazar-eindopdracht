import jsPDF from "jspdf";
export function saveInvoicePdf(order){
  const doc = new jsPDF();
  doc.setFontSize(18);
  doc.text("Factuur — WebBazar", 14, 20);
  doc.setFontSize(12);
  doc.text(`Order: #${order.id}`, 14, 30);
  doc.text(`Datum: ${(order.createdAt||'').toString().slice(0,10)}`, 14, 38);
  let y=50; doc.text("Items:",14,y); y+=8;
  (order.items||[]).forEach(it=>{ doc.text(`- ${it.title} — €${Number(it.price||0).toFixed(2)}`,20,y); y+=7; });
  y+=2; doc.text(`Totaal: €${Number(order.total||0).toFixed(2)}`,14,y);
  doc.save(`factuur-order-${order.id}.pdf`);
}
