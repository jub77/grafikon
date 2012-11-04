<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>${title}</title>
  <style type="text/css" media="all">
    td {padding: 0.5mm .5mm 0.5mm .5mm; border-color: black; border-style: solid; border-width: 0.2mm;}
    table.card {font-family: arial, sans-serif; font-size: 3mm; height: 96mm; width: 138mm; border-color: black; border-style: solid; border-width: 0.2mm; page-break-inside: avoid;}
    .nobreak {page-break-inside: avoid;}
    .break {page-break-before: always;}
    td.company {width: 25mm; font-size: 7mm; font-weight:bold;}
    td.description {border-width: 0mm; font-size:3mm;}
    td.value {border-width: 0mm; font-size:5mm; font-weight:bold;}
    div.spacer {height: 10mm;}
    tr.header {height: 12mm;}
    td.headerw1 {width: 20mm;}
    td.headerw2 {width: 93mm;}
    td.ctrain {width: 20mm; text-align: center; vertical-align: top;}
    td.cdeparture {width: 13mm; text-align: center; vertical-align: top; font-weight:bold;}
    td.cfromto {width: 17mm; text-align: center; vertical-align: top;}
    td.cnote {width: 85mm; text-align: center; vertical-align: top; font-weight:bold;}
    tr.listheader {height: 6mm; font-weight:bold ;font-size: 3mm; height: 5mm;}
    table.list {font-family: arial, sans-serif; font-size: 3mm;  width: 135mm; border-color: black; border-style: solid; border-width: 0.2mm;}
    tr.listitem {}
    td.listwrap {padding: 1mm 1mm 1mm 1mm; vertical-align: top;}
  </style>
</head>
<body>
<%
  boolean pageBreak = true;
  for (c in cycles) {
%><div class="nobreak">
<table class="card" align="center" cellspacing=0>
  <tr class="header">
    <td align="center" class="company">${company}</td>
    <td class="headerw1">
      <table  cellpadding=0 cellspacing=0>
        <tr>
          <td class="description">${cycle}:</td></tr>
        <tr>
          <td class="value">${c.name}</td></tr>
      </table>
    </td>
    <td class="headerw2">
      <table class="nobreak" cellpadding=0 cellspacing=0>
        <tr>
          <td class="description">${composition}:</td></tr>
        <tr>
          <td class="value">${c.description}</td></tr>
      </table>
     </td>
  </tr>
  <tr>
    <td colspan="3" class="listwrap">
      <table align="center" class="list"  cellspacing=0>
        <tr class="listheader">
          <td class="ctrain">${column_train}</td>
          <td class="cdeparture">${column_departure}</td>
          <td class="cfromto">${column_from_to}</td>
          <td class="cnote">${column_note}</td>
        </tr><% for (row in c.rows) { %>
        <tr class="listitem">
          <td class="ctrain">${row.trainName}</td>
          <td class="cdeparture">${row.fromTime}</td>
          <td class="cfromto">${row.fromAbbr} - ${row.toAbbr}</td>
          <td>${createComment(row)}</td>
        </tr><% } %>
      </table>
    </td>
  </tr>
</table>
</div>
<div class="spacer">&nbsp;</div><%
    pageBreak = !pageBreak;
    if (pageBreak) {
      %><div class="break">&nbsp;</div>
<%
    }
  }

  def createComment(row) {
    def result = row.cycle.inject(row.comment) {
      str, item ->
        if (str == null)
          str = ""
        if (str != "")
          str += ", "
        def value = "${item.type}: ${item.name}"
        if (item.fromAbbr != null)
          value = "${value} (${item.fromAbbr} - ${item.toAbbr})"
        str + value
    }
    return result == null ? "&nbsp;" : result
  }
%>
</body>
</html>
