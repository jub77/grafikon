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
    table.titlepage {width: 120mm;}
    td.company {height: 20mm; font-size: 4mm; text-align: center; font-weight: bold;}
    td.space1 {height: 15mm; font-size: 4mm; text-align: center;}
    td.gtitle {height: 15mm; font-size: 8mm; text-align: center; font-weight: bold;}
    td.numbers {height: 15mm; font-size: 12mm; text-align: center; font-weight: bold;}
    td.line {height: 5mm; font-size: 4mm; text-align: center;}
    td.stations {height: 15mm; font-size: 4mm; text-align: center; font-weight: bold;}
    td.valid {height: 20mm; font-size: 4mm; text-align: center; font-weight: bold;}
    td.cycle {height: 8mm; font-size: 5mm; text-align: center; vertical-align: top; font-weight: bold;}
    td.cycledesc {height: 8mm; font-size: 4mm; text-align: center;}
    td.space2 {height: 50mm; font-size: 5mm; text-align: center; vertical-align: top;}
    td.publish {height: 5mm; font-size: 3mm; text-align: center;}
    table.list1 {width: 130mm;}
    td.list1 {font-size: 5mm; padding-left: 3mm; vertical-align: top;}
    table.list2 {font-size: 4mm; width: 125mm;}
    table.list2 tr td {vertical-align: text-bottom;}
    tr.listh {height: 5mm; font-size: 3mm;}
    tr.listh td {border-color: black; border-style: solid; border-width: 0.2mm 0 0.2mm 0;}
    td.ctrainh {width: 25mm; text-align: left;}
    td.cdepartureh {width: 15mm; text-align: left;}
    td.cfromtoh {width: 30mm; text-align: left;}
    td.cnoteh {width: 45mm; padding-left: 5mm;}
    td.ctrain {vertical-align: bottom;}
    td.cdeparture {vertical-align: bottom; text-align: right; font-weight: bold; padding-right: 3mm;}
    td.cfromto {vertical-align: bottom; text-align: left;}
    td.cnote {font-size: 3mm; padding-left: 2mm; vertical-align: bottom;}
    td.move {vertical-align: bottom;}
    tr.listabbr {font-size: 3.25mm;}
    span.no {visibility: hidden;}
 </style>
</head>
<body>
<%
    separator = java.text.DecimalFormatSymbols.getInstance().getDecimalSeparator();
    END = "${separator}0"
    FORMATTER = org.joda.time.format.ISODateTimeFormat.hourMinuteSecond()
    PRINT_FORMATTER = new org.joda.time.format.DateTimeFormatterBuilder().appendHourOfDay(1).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(separator).appendFractionOfMinute(1, 1).toFormatter()

    def convertTime(time) {
        def parsed = FORMATTER.parseLocalTime(time)
        def result = PRINT_FORMATTER.print(parsed)
        if (result.endsWith(END)) {
            result = result.replace("${END}", "<span class=\"no\">${END}</span>")
        }
        return result
    }
%>
<% for (c in cycles.cycles) {
    def loc = getLocale(c)
    def company = getCompany(c, loc)
%>
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
    <tr><td class="gtitle">${translator.getText("train_timetable", loc)}</td></tr>
    <tr><td class="numbers">${getRouteNames(c, cycles)}</td></tr>
    <tr><td class="line">${translator.getText("for_line", loc)}</td></tr>
    <tr><td class="stations">${getRoutePaths(c, cycles)}</td></tr>
    <tr><td class="valid"><% if (cycles.validity != null) { %>${translator.getText("validity_from", loc)} ${cycles.validity}<% } else { %>&nbsp;<% } %></td></tr>
    <tr><td class="cycle">${translator.getText("cycle", loc)}: ${c.name}</td></tr>
    <tr><td class="cycledesc">${c.description ?: "&nbsp"}</td></tr>
    <tr><td class="space2">&nbsp;</td></tr>
    <tr><td class="publish">${translator.getText("publisher", loc)}</td></tr>
  </table>
</td>
</tr>
</table>
<% }
   Collections.reverse(cycles.cycles);
   for (c in cycles.cycles) { 
       def loc = getLocale(c)
%>
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
        <tr>
          <td colspan="4">${translator.getText("list_train_title", loc)}:</td>
        </tr>
        <tr class="listh">
          <td class="ctrainh">${translator.getText("column_train", loc)}</td>
          <td class="cdepartureh">${translator.getText("column_departure", loc)}</td>
          <td class="cfromtoh">${translator.getText("column_from_to", loc)}</td>
          <td class="cnoteh">${translator.getText("column_note", loc)}</td>
        </tr><% lastNode = null;
                def abbrMap = [:]
                for (item in c.rows) {
                  abbrMap[item.fromAbbr] = item.from
                  abbrMap[item.toAbbr] = item.to
                  if (lastNode != null && lastNode != item.from) {
                    %>
        <tr>
          <td colspan="4" class="move">&mdash;  ${translator.getText("move_to_station", loc)} ${item.from} &mdash; </td>
        </tr><%
                  }
              %>
        <tr>
          <td class="ctrain">${item.trainName}</td>
          <td class="cdeparture">${convertTime(item.fromTime)}</td>
          <td class="cfromto">${item.fromAbbr} - ${item.toAbbr}</td>
          <td class="cnote">${getComment(item.comment, loc)}</td>
        </tr><% lastNode = item.to
                }
              %>
        <tr>
          <td colspan="4">&nbsp;</td>
        </tr>
        <tr>
          <td colspan="4">
            <table cellspacing="0" border="0"><%
                abbrMap.sort().each {
                  %>
              <tr class="listabbr">
                <td>${it.key}</td><td>&nbsp;- ${it.value}</td>
              </tr><%
                }
              %>
            </table>
          </td>
        </tr>
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
    def routeNames = [] as Set
    for (route in cycle.routes) {
      if (!routeNames.contains(route.name)) {
        result = add(result,"<br>",route.name)
        routeNames << route.name
      }
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
      stationsStr = add(stationsStr," - ",route.segments.first().name)
      stationsStr = add(stationsStr," - ",route.segments.last().name)
      result = add(result,"<br>",stationsStr)
    }
  }
  return result
}

def getComment(comment, loc) {
    if (comment) {
        comment = translator.translate(comment, loc)
    }
    return comment ?: "&nbsp;"
}

def add(str, delimiter, value) {
  if (str == null || str.isEmpty())
    str = value
  else
    str += delimiter + value
  return str
}

def getCompany(cycle, loc) {
  def company = cycle?.company?.name
  company ?: cycle?.company?.abbr
  return company ?: translator.getText("company", loc)
}
  
def getLocale(cycle) {
  def l = cycle?.company?.locale
  return l ?: locale
}
%>
