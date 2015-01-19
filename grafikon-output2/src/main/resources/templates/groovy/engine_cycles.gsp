<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>${title}</title>
  <style type="text/css" media="all">
    table.cycles {width: 150mm; border-color: black; border-style: solid; border-width: 0mm;}
    tr.cycles {height: 85mm;}
    .break {page-break-before: always; font-size: 1mm;}
    td.cycle {border-color: black; border-style: solid; border-width: 0.4mm; vertical-align: top;}
    td.cycle table {font-family: arial, sans-serif; font-size: 3mm; width: 50mm; border-color: black; border-style: solid; border-width: 0mm;}
    tr.title {height: 3mm;}
    tr.title td {font-size: 2mm;}
    tr.info {height: 6mm;}
    td.info1 {font-size: 5mm; text-align: center; font-weight: bold;}
    td.info2 {font-size: 4mm; text-align: center; font-weight: bold;}
    tr.listh {height: 3mm; text-align: center;}
    td.trainh {border-color: black; border-style: solid; border-width: 0.2mm 0mm 0mm 0mm; font-size: 2mm; width: 20mm;}
    td.timeh {border-color: black; border-style: solid; border-width: 0.2mm 0mm 0mm 0mm; font-size: 2mm; width: 13mm;}
    td.fromtoh {border-color: black; border-style: solid; border-width: 0.2mm 0mm 0mm 0mm; font-size: 2mm; width: 17mm;}
    tr.row {height: 4mm; text-align: center;}
    tr.emph {font-style: italic;}
    td.trow {text-align: left;}
    td.drow {font-weight: bold; text-align: right; padding-right: 1mm;}
    td.ftrow {}
    tr.delim {height: .5mm;}
    tr.delim td {font-size: 0.2mm; border-color: black; border-style: solid; border-width: 0.35mm 0mm 0mm 0mm; width: 21mm;}
    span.no {visibility: hidden;}
  </style>
</head>
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
<body>
<%
  Iterator iterator = cycles.iterator();
  count = 0;
  while(true) {
%>
<table class="cycles" align="center" cellspacing=0 cellpadding=0>
  <tr class="cycles"><%
      countInRow = 0;
      c = null;
      while (true) {
        if (iterator.hasNext()) {
          c = iterator.next();
        } else
          c = null;

        if (c != null) {
          %>
    <td class="cycle">
      <table align="center" cellspacing=0>
        <tr class="title">
          <td colspan="3">${cycle}:</td>
        </tr>
        <tr class="info">
          <td class="info1">${c.name}</td>
          <td class="info2" colspan="2">${c.description}</td>
        </tr>
        <tr class="listh">
          <td class="trainh">${column_train}</td>
          <td class="timeh">${column_departure}</td>
          <td class="fromtoh">${column_from_to}</td>
        </tr><% for (row in c.rows) {
                  if (row.wait > 25*60) {
                    %>
        <tr class="delim">
          <td colspan=3>&nbsp;</td>
        </tr><%
                  }
              %>
        <tr class="row${row.helper == true ? " emph" : ""}">
          <td class="trow">&nbsp;${row.trainName}</td>
          <td class="drow">${convertTime(row.fromTime)}</td>
          <td class="ftrow">${row.fromAbbr} - ${row.toAbbr}</td>
        </tr><% } %>
      </table>
    </td><%
        } else {
          %>
    <td class="cycle">
      <table align="center" cellspacing=0>
        <tr><td>&nbsp;</td></tr>
      </table>
    </td><%
        }

        countInRow++;
        if (countInRow == 3)
          break;
      }
    %>
  </tr>
</table>
<%
    if (!iterator.hasNext())
      break;
    count++;
    if (count % 3 == 0) {
      %><div class="break">&nbsp;</div>
<%
    }
  }
%>
</body>
</html>
