<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>${title}</title>
  <style type="text/css">
    table.pages {page-break-after: always; height: 185mm; width: 274mm; font-size: 3mm; font-family: arial, sans-serif;}
    td.upperll {height: 4mm; padding-left: 0mm; text-align: left; width: 25%;}
    td.upperlr {padding-right: 12mm; text-align: right; width: 25%;}
    td.upperrl {padding-left: 12mm; text-align: left; width: 25%;}
    td.upperrr {padding-right: 0mm; text-align: right; width: 25%;}
    td.page {padding-left:0mm; width: 50%; vertical-align: top;}
    table.titlepage {width: 130mm;}
    td.company {height: 20mm; font-size: 4mm; text-align: center; font-weight: bold;}
    td.space1 {height: 15mm; font-size: 4mm; text-align: center;}
    td.gtitle {height: 15mm; font-size: 8mm; text-align: center; font-weight: bold;}
    td.numbers {height: 15mm; font-size: 12mm; text-align: center; font-weight: bold;}
    td.line {height: 5mm; font-size: 4mm; text-align: center;}
    td.stations {height: 15mm; font-size: 4mm; text-align: center; font-weight: bold;}
    td.valid {height: 20mm; font-size: 4mm; text-align: center; font-weight: bold;}
    td.cycle {height: 8mm; font-size: 5mm; text-align: center; vertical-align: top; font-weight: bold;}
    td.space2 {height: 58mm; font-size: 5mm; text-align: center; vertical-align: top;}
    td.publish {height: 5mm; font-size: 3mm; text-align: center;}
    table.list1 {width: 130mm;}
    td.list1 {font-size: 5mm; padding-left: 3mm; vertical-align: top;}
    table.list2 {font-size: 4mm; width: 125mm;}
    tr.listh {height: 5mm; font-size: 3mm;}
    td.ctrainh {width: 25mm; text-align: center;}
    td.cdepartureh {width: 15mm; text-align: center;}
    td.cfromtoh {width: 22mm; text-align: center;}
    td.cnoteh {width: 53mm; padding-left: 5mm;}
    td.ctrain {vertical-align: bottom;}
    td.cdeparture {vertical-align: bottom; text-align: center; font-weight: bold;}
    td.cfromto {vertical-align: bottom; text-align: center;}
    td.cnote {font-size: 3mm; padding-left: 2mm; vertical-align: bottom;}
    td.move {vertical-align: bottom;}
 </style>
</head>
<body>
<% for (c in cycles.cycles) { %>
<table class="pages" border="0" cellspacing="0" cellpadding="0">
<tr>
    <td class="upperll">&nbsp;</td>
    <td class="upperlr">&nbsp;</td>
    <td class="upperrl">&nbsp;</td>
    <td class="upperrr">&nbsp;</td>
</tr>
<tr>
<td class="page" colspan="2"></td>
<td class="page" colspan="2">
  <table align="right" class="titlepage" border="0" cellspacing="0">
    <tr><td class="company">${company}<br>${company_part}</td></tr>
    <tr><td class="space1"></td></tr>
    <tr><td class="gtitle">${train_timetable}</td></tr>
    <tr><td class="numbers">${getRouteNames(c, cycles)}</td></tr>
    <tr><td class="line">${for_line}</td></tr>
    <tr><td class="stations">${getRoutePaths(c, cycles)}</td></tr>
    <tr><td class="valid"><% if (cycles.validity != null) { %>${validity_from} ${cycles.validity}<% } else { %>&nbsp;<% } %></td></tr>
    <tr><td class="cycle">${cycle}: ${c.name}</td></tr>
    <tr><td class="space2">&nbsp;</td></tr>
    <tr><td class="publish">${publisher}</td></tr>
  </table>
</td>
</tr>
</table>
<% }
   Collections.reverse(cycles.cycles);
   for (c in cycles.cycles) { %>
<table class="pages" border="0" cellspacing="0" cellpadding="0">
<tr>
    <td class="upperll">&nbsp;</td>
    <td class="upperlr">&nbsp;</td>
    <td class="upperrl">&nbsp;</td>
    <td class="upperrr">&nbsp;</td>
</tr>
<tr>
<td class="page" colspan="2">
  <table align="left" class="list1" border="0" cellspacing="0">
    <tr><td align="center" class="list1">
      <table class="list2" border="0" cellspacing="0">
        <tr class="listh">
          <td class="ctrainh">${column_train}</td>
          <td class="cdepartureh">${column_departure}</td>
          <td class="cfromtoh">${column_from_to}</td>
          <td class="cnoteh">${column_note}</td>
        </tr><% lastNode = null;
                for (item in c.rows) {
                  if (lastNode != null && lastNode != item.from) {
                    %>
        <tr>
          <td colspan="4" class="move">&#151;  ${move_to_station} ${item.from} &#151; </td>
        </tr><%
                  }
              %>
        <tr>
          <td class="ctrain">${item.trainName}</td>
          <td class="cdeparture">${item.fromTime}</td>
          <td class="cfromto">${item.fromAbbr} - ${item.toAbbr}</td>
          <td class="cnote">${item.comment != null ? item.comment : "&nbsp;"}</td>
        </tr><% lastNode = item.to
                }
              %>
      </table>
    </td></tr>
  </table>
</td>
<td class="page" colspan="2"></td>
</tr>
</table>
<% } %>
</body>
</html>

<%
// returns names of routes for driver cycle
def getRouteNames(cycle, cycles) {
  def result = null
  if (cycle.routes == null || cycle.routes.isEmpty()) {
    result = (cycles.routeNumbers == null) ? "-" : cycles.routeNumbers.replace("\n","<br>")
  } else {
    for (route in cycle.routes) {
      result = add(result,"<br>",route.name)
    }
  }
  return result
}

// returns paths of routes for driver cycle
def getRoutePaths(cycle,cycles) {
  def result = null
  if (cycle.routes == null || cycle.routes.isEmpty()) {
    result = (cycles.routeStations == null) ? "-" : cycles.routeStations.replace("\n","<br>")
  } else {
    for (route in cycle.routes) {
      def stationsStr = null
      for (station in route.stations) {
        stationsStr = add(stationsStr," - ",station)
      }
      result = add(result,"<br>",stationsStr)
    }
  }
  return result
}

def add(str, delimiter, value) {
  if (str == null || str.isEmpty())
    str = value
  else
    str += delimiter + value
  return str
}
%>
