<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>${title}</title>
    <style type="text/css" media="all">
        div.caption1 {
            font-family: arial, sans-serif;
            font-size: 6mm;
            font-weight: bold;
        }
        div.caption2 {
            font-family: arial, sans-serif;
            font-size: 4.5mm;
            font-weight: bold;
            padding: 3mm 0 3mm 0;
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
<body>
<div class="caption1">${translator.getText("title", locale)}</div>
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

<div class="caption2">${translator.getText("title_engines", locale)}</div>
<table class="list" border="0" cellspacing="0" cellpadding="0">
<%  for (engine in engines) { %>
    <tr>
        <td>${engine.cycleName}</td>
        <td>${engine.cycleDescription}&nbsp;</td>
        <td>${engine.stationName}</td>
        <td>${engine.track}</td>
        <td class="right">${convertTime(engine.time)}</td>
        <td>${engine.trainName}</td>
    </tr>
<% } %>
</table>

<div class="caption2">${translator.getText("title_train_units", locale)}</div>
<table class="list" border="0" cellspacing="0" cellpadding="0">
<%  for (train_unit in train_units) { %>
    <tr>
        <td>${train_unit.cycleName}</td>
        <td>${train_unit.cycleDescription}&nbsp;</td>
        <td>${train_unit.stationName}</td>
        <td>${train_unit.track}</td>
        <td class="right">${convertTime(train_unit.time)}</td>
        <td>${train_unit.trainName}</td>
    </tr>
<% } %>
</table>
</body>
</html>
