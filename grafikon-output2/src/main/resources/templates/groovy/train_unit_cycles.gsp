<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>${title}</title>
  <style type="text/css" media="all">
    td {padding: 0.5mm .5mm 0.5mm .5mm; border-color: black; border-style: solid; border-width: 0.2mm;}
  </style>
</head>
<body>
<%
  boolean pageBreak = true;
  for (c in cycles) {
%>
<div style="page-break-inside: avoid;">
<table style="font-family: arial, sans-serif; font-size: 3mm; height: 96mm; width: 138mm; border-color: black; border-style: solid; border-width: 0.2mm; page-break-inside: avoid;" align="center" cellspacing=0>
  <tr style="height: 12mm">
    <td align="center" style="width: 25mm; font-size: 7mm"><b>${company}</b></td>
    <td style="width: 20mm;">
      <table  cellpadding=0 cellspacing=0 >
        <tr>
          <td style="border-width: 0mm; font-size:3mm">${cycle}:</td></tr>
        <tr>
          <td style="border-width: 0mm; font-size:5mm"><b>${c.name}</b></td></tr>
      </table>
    </td>
    <td style="width: 93mm;">
      <table style="page-break-inside: avoid;" cellpadding=0 cellspacing=0 >
        <tr>
          <td style="border-width: 0mm; font-size:3mm">${composition}:</td></tr>
        <tr>
          <td style="border-width: 0mm; font-size:5mm"><b>${c.description}</b></td></tr>
      </table>
     </td>
  </tr>
  <tr>
    <td colspan="3" valign="top" style="padding: 1mm 1mm 1mm 1mm; ">
      <table  align="center"style="font-family: arial, sans-serif; font-size: 3mm;  width: 135mm; border-color: black; border-style: solid; border-width: 0.2mm;"  cellspacing=0>
        <tr align="center" style="height: 6mm; font-weight:bold ;font-size: 3mm; height: 5mm">
          <td style="width: 20mm">${column_train}</td>
          <td style="width: 13mm">${column_departure}</td>
          <td style="width: 17mm">${column_from_to}</td>
          <td style="width: 85mm">${column_note}</td>
        </tr>
<% for (row in c.rows) { %>
        <tr valign="top" >
          <td align="center">${row.trainName}</td>
          <td align="center"><b>${row.fromTime}</b></td>
          <td align="center">${row.fromAbbr} - ${row.toAbbr}</td>
          <td>${row.comment == null ? "&nbsp;" : row.comment}</td>
        </tr>
<% } %>
      </table>
    </td>
  </tr>
</table>
</div>
<div style="height: 10mm;">&nbsp;</div>
<%
    pageBreak = !pageBreak;
    if (pageBreak) {
      %><div style="page-break-before: always;">&nbsp;</div><%
    }
  }
%>
</body>
</html>
