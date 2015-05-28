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
        span.no {visibility: hidden;}
        td.time {text-align: right; padding-left: 1mm; padding-right: 0mm;}
        span.emph {font-style: italic;}
    </style>
</head>
<%
    separator = java.text.DecimalFormatSymbols.getInstance().getDecimalSeparator();
    END = "${separator}0"
    FORMATTER = org.joda.time.format.ISODateTimeFormat.hourMinuteSecond()
    PRINT_FORMATTER = new org.joda.time.format.DateTimeFormatterBuilder().appendHourOfDay(1).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(separator).appendFractionOfMinute(1, 1).toFormatter()

    def convertTime(time, includePart) {
        if (time == "" || time == null)
            return "";
        def parsed = FORMATTER.parseLocalTime(time)
        def result = PRINT_FORMATTER.print(parsed)
        if (result.endsWith(END)) {
            result = result.replace("${END}", includePart ? "<span class=\"no\">${END}</span>" : "")
        }
        return result
    }
    
    def highlight(str) {
        return "<b>${str}</b>"
    }
    
    def highlightHelper(str) {
        return "<span class=\"emph\">${str}</span>";
    }
    
    def comment(separator, str) {
        return (str == null || str =="") ? "" : "${separator}(${str})"
    }
%>

<%
  def print_out(str, variant) {
    print get_out(str, variant)
  }

  def get_out(str, variant) {
    if (str == null || str == "")
      return variant
    else
      return str
  }

  def get_note(row, loc) {
    if (row.technologicalTime) {
      return translator.getText("technological_time", loc)
    }

    note_parts = []
    // length
    if (row.length != null) {
      length_unit_s = row.length.lengthInAxles ? " ${translator.getText('length_axles', loc)}" : row.length.lengthUnit.getUnitsOfString(loc)
      note_parts << "[${row.length.length}${length_unit_s}]"
    }
    // engine
    def engineText = translator.getText("engine", loc)
    if (row.engine != null)
      for (engine_t in row.engine) {
        def str = ""
        if (engine_t.in)
          str = "${engineText}: ${engine_t.name}${comment(' ', engine_t.desc)}"
        else
          if (engine_t.trainName == null)
            str = "${engineText}: ${engine_t.name} ${translator.getText('ends', loc)}"
          else
            str = "${engineText}: ${engine_t.name} ${translator.getText('move_to', loc)} ${engine_t.trainName} (${convertTime(engine_t.time, false)})"
        str = engine_t.start ? highlight(str) : str
        str = engine_t.helper == true ? highlightHelper(str) : str
        note_parts << str
      }
    // train unit
    def trainUnitText = translator.getText("train_unit", loc)
    if (row.trainUnit != null)
      for (train_unit_t in row.trainUnit) {
        def str = ""
        if (train_unit_t.in)
          str = "${trainUnitText}: ${train_unit_t.name}${comment(' ', train_unit_t.desc)}"
        else
          if (train_unit_t.trainName == null)
            str = "${trainUnitText}: ${train_unit_t.name} ${translator.getText('ends', loc)}"
          else
            str = "${trainUnitText}: ${train_unit_t.name} ${translator.getText('move_to', loc)} ${train_unit_t.trainName} (${convertTime(train_unit_t.time, false)})"
        note_parts << (train_unit_t.start ? highlight(str) : str)
      }
    // other
    if (row.cycle != null)
      for (cycle_t in row.cycle) {
        def str = ""
        def cycleTypeText = translator.translate(cycle_t.type, loc)
        if (cycle_t.in)
          str = "${cycleTypeText}: ${cycle_t.name}${comment(' ', cycle_t.desc)}"
        else
          if (cycle_t.trainName == null)
            str = "${cycleTypeText}: ${cycle_t.name} ${translator.getText('ends', loc)}"
          else
            str = "${cycleTypeText}: ${cycle_t.name} ${translator.getText('move_to', loc)} ${cycle_t.trainName} (${convertTime(cycle_t.time, false)})"
        note_parts << (cycle_t.start ? highlight(str) : str)
      }
    // comment
    if (row.comment != null) {
      note_parts << translator.translate(row.comment, loc)
    }
    if (row.freightTo != null) {
      note_parts << "<i>${row.freightTo.collect{i -> i.toString(loc, true)}.join(', ')}</i> &rArr;"
    }
    if (row.freightToTrain != null) {
      note_parts << "<i>${row.freightToTrain.collect{i -> '(&rarr; ' + i.train + ': ' + (i.freightTo.collect{j -> j.toString(loc, true)}.join(', ')) + ')'}.join(', ')}</i>"
    }
    if (row.freightFromTrain != null) {
      note_parts << "<i>(${row.freightFromTrain.collect{i -> i + ' &rarr;'}.join(', ')})</i>"
    }
    // occupied track
    if (row.occupied) {
      note_parts << translator.getText("occupied", loc)
    }

    return create_note_str(note_parts)
  }

  def create_note_str(note_parts) {
    return note_parts.collect{i -> i}.join(", ");
  }
%>
<body>
<%
  for (station in stations) {
      def loc = locale
%>
<table class="station" align="center" cellspacing=0 cellpadding=0>
<thead>
<tr class="header_station">
    <td colspan="9">${station.name}</td>
</tr>
<tr class="header">
    <td style="width: 5mm; text-align: center">X</td>
    <td style="width: 22mm">${translator.getText("column_train", loc)}</td>
    <td style="width: 10mm">${translator.getText("column_from", loc)}</td>
    <td style="width: 10mm">${translator.getText("column_arrival", loc)}</td>
    <td style="width: 5mm">${translator.getText("column_track", loc)}</td>
    <td style="width: 10mm">${translator.getText("column_departure", loc)}</td>
    <td style="width: 10mm">${translator.getText("column_to", loc)}</td>
    <td style="width: 10mm">${translator.getText("column_end", loc)}</td>
    <td style="width: 188mm">${translator.getText("column_notes", loc)}</td>
</tr>
</thead>
<tbody>
<% for (row in station.rows) { %>
<tr class="row">
    <td>&nbsp;</td>
    <td>${row.trainName}</td>
    <td><% print_out(row.from,"&nbsp;") %></td>
    <td class="time"><% print_out(convertTime(row.arrival, true),"&nbsp;") %></td>
    <td align="center">${row.track}</td>
    <td class="time"><% print_out(convertTime(row.departure, true),"&nbsp;") %></td>
    <td><% print_out(row.to,"&nbsp;") %></td>
    <td><% print_out(row.end,"&nbsp;") %></td>
    <td><% note = get_note(row, loc); note != "" ? print(note) : print("&nbsp;") %></td>
</tr>
<% } %>
</tbody>
</table>
<% } %>
</body>
</html>
