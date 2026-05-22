export function buildOrderXml(order){
  const esc=(s='')=> String(s).replace(/[&<>]/g, c=>({ '&':'&amp;','<':'&lt;','>':'&gt;' }[c]));
  const items=(order.items||[]).map(it=>`  <item><title>${esc(it.title)}</title><price>${Number(it.price||0).toFixed(2)}</price></item>`).join("\n");
  return `<?xml version="1.0" encoding="UTF-8"?>\n<order id="${order.id}">\n<total>${Number(order.total||0).toFixed(2)}</total>\n<status>${order.status}</status>\n<items>\n${items}\n</items>\n</order>`;
}
export function downloadXml(xml, filename){
  const blob=new Blob([xml],{type:"application/xml"});
  const url=URL.createObjectURL(blob);
  const a=document.createElement("a"); a.href=url; a.download=filename; a.click(); URL.revokeObjectURL(url);
}
