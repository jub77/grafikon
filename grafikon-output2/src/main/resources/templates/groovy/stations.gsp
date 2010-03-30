<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>${title}</title>
    <style type="text/css" media="all">
        table.station {page-break-after: always; font-family: arial, sans-serif; font-size: 3mm; width: 270mm; border-color: black; border-style: solid; border-width: 0.2mm;}
        td {padding: .1mm .8mm .1mm .8mm; border-color: black; border-style: solid; border-width: 0.2mm;}
        tr.row td {vertical-align: top; font-size: 3.4mm;}
        tr.header {height: 5mm;}
        tr.header td {font-weight:bold ;font-size: 3.4mm;}
        tr.header_station {height: 8mm;}
        tr.header_station td {font-weight:bold ; font-size: 5mm; text-align: center;}
    </style>
</head>
<%
  def print_out(str, variant) {
    if (str == null || str.equals(""))
      print variant
    else
      print str
  }
%>
<body>
<% for (station in stations) { %>
<table class="station" align="center" cellspacing=0 cellpadding=0>
<thead>
<tr class="header_station">
    <td colspan="9">${station.name}</td>
</tr>
<tr class="header">
    <td style="width: 5mm; text-align: center">X</td>
    <td style="width: 22mm">${column_train}</td>
    <td style="width: 10mm">${column_from}</td>
    <td style="width: 10mm">${column_arrival}</td>
    <td style="width: 5mm">${column_track}</td>
    <td style="width: 10mm">${column_departure}</td>
    <td style=" width: 10mm">${column_to}</td>
    <td style=" width: 10mm">${column_end}</td>
    <td style=" width: 188mm">${column_notes}</td>
</tr>
</thead>
<tbody>
<% for (row in station.rows) { %>
<tr class="row">
    <td>&nbsp;</td>
    <td>${row.trainName}</td>
    <td><% print_out(row.from,"&nbsp;") %></td>
    <td align="right"><% print_out(row.arrival,"&nbsp;") %>&nbsp;</td>
    <td align="center">${row.track}</td>
    <td align="right"><% print_out(row.departure,"&nbsp;") %>&nbsp;</td>
    <td><% print_out(row.to,"&nbsp;") %></td>
    <td><% print_out(row.end,"&nbsp;") %></td>
    <td><% (row.length != null) ? print("["+row.length.length+" ("+row.length.stationAbbr+")]") : print("&nbsp;") %></td>
</tr>
<% } %>
</tbody>
</table>
<% } %>
</body>
</html>
