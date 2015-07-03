<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>${title}</title>
    <style type="text/css" media="all">
        div.caption1 {
            font-family: arial, sans-serif;
            font-size: 5mm;
            font-weight: bold;
            padding: 1mm 0 0.5mm 0;
        }
        div.caption2 {
            font-family: arial, sans-serif;
            font-size: 4.5mm;
            font-weight: bold;
            padding: 1.5mm 0 0.5mm 0;
        }
        table.list {
            font-family: arial, sans-serif;
            font-size: 3mm;
            border-color: black;
            border-style: solid;
            border-width: 0.3mm;
        }
        table.list tr td {
            border-color: black;
            border-style: solid;
            border-width: 0.3mm;
            padding: 0.5mm 4mm 0.5mm 1mm;
        }
        span.no {
            visibility: hidden;
        }
        table.list tr td.right {
            text-align: right;
            padding-right: 1mm;
            padding-left: 4mm;
        }
    </style>
</head>
<body><%
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
    
    def getStartTimeTitle() {
        return start_time ? " (${diagram.timeConverter.convertIntToText(start_time)})" : "";
    }
%>
<div class="caption1">${localization.translate("title", locale)}${getStartTimeTitle()}</div>
<div class="caption2">${localization.translate("title_engines", locale)}</div>
<% printPositions(engines) %>

<div class="caption2">${localization.translate("title_train_units", locale)}</div>
<% printPositions(train_units) %>

<% for (cycles in custom_cycles) { %>
<div class="caption2">${translator.translate(cycles.name, locale)}</div>
<% printPositions(cycles.positions) %>
<% } %>
    
<% def printPositions(positions) {
%><table class="list" border="0" cellspacing="0" cellpadding="0"><%
  for (position in positions) { %>
    <tr>
        <td>${position.cycleName}</td>
        <td>${position.cycleDescription?:""}&nbsp;</td>
        <td>${position.stationName}</td>
        <td>${position.track}</td>
        <td class="right">${convertTime(position.time)}</td>
        <td>${position.trainName}</td>
    </tr><% 
  } %>
</table><% 
   } %>
</body>
</html>
