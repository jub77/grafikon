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
    
    def comment(separator, str) {
        return (str == null || str =="") ? str : "${separator}(${str})"
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

  def get_note(row) {
    if (row.technologicalTime)
      return technological_time

    note_parts = []
    // length
    if (row.length != null) {
      length_unit_s = row.length.lengthInAxles ? " ${length_axles}" : row.length.lengthUnit
      note_parts << "[${row.length.length}${length_unit_s}]"
    }
    // engine
    if (row.engine != null)
      for (engine_t in row.engine) {
        def str = ""
        if (engine_t.in)
          str = "${engine}: ${engine_t.name}${comment(' ', engine_t.desc)}"
        else
          if (engine_t.trainName == null)
            str = "${engine}: ${engine_t.name} ${ends}"
          else
            str = "${engine}: ${engine_t.name} ${move_to} ${engine_t.trainName} (${convertTime(engine_t.time, false)})"
        note_parts << (engine_t.start ? highlight(str) : str)
      }
    // train unit
    if (row.trainUnit != null)
      for (train_unit_t in row.trainUnit) {
        def str = ""
        if (train_unit_t.in)
          str = "${train_unit}: ${train_unit_t.name}${comment(' ', train_unit_t.desc)}"
        else
          if (train_unit_t.trainName == null)
            str = "${train_unit}: ${train_unit_t.name} ${ends}"
          else
            str = "${train_unit}: ${train_unit_t.name} ${move_to} ${train_unit_t.trainName} (${convertTime(train_unit_t.time, false)})"
        note_parts << (train_unit_t.start ? highlight(str) : str)
      }
    // other
    if (row.cycle != null)
      for (cycle_t in row.cycle) {
        def str = ""
        if (cycle_t.in)
          str = "${cycle_t.type}: ${cycle_t.name}${comment(' ', cycle_t.desc)}"
        else
          if (cycle_t.trainName == null)
            str = "${cycle_t.type}: ${cycle_t.name} ${ends}"
          else
            str = "${cycle_t.type}: ${cycle_t.name} ${move_to} ${cycle_t.trainName} (${convertTime(cycle_t.time, false)})"
        note_parts << (cycle_t.start ? highlight(str) : str)
      }
    // comment
    if (row.comment != null)
      note_parts << row.comment
    if (row.freightTo != null) {
      note_parts << "<i>${row.freightTo.collect{i -> i}.join(', ')}</i>"
    }
    if (row.freightToTrain != null) {
      note_parts << "<i>(${row.freightToTrain.collect{i -> i}.join(', ')} &rarr;)</i>"
    }
    if (row.freightFromTrain != null) {
      note_parts << "<i>(&rarr; ${row.freightFromTrain.collect{i -> i}.join(', ')})</i>"
    }
    // occupied track
    if (row.occupied)
      note_parts << occupied

    return create_note_str(note_parts)
  }

  def create_note_str(note_parts) {
    return note_parts.collect{i -> i}.join(", ");
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
    <td style="width: 10mm">${column_to}</td>
    <td style="width: 10mm">${column_end}</td>
    <td style="width: 188mm">${column_notes}</td>
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
    <td><% note = get_note(row); note != "" ? print(note) : print("&nbsp;") %></td>
</tr>
<% } %>
</tbody>
</table>
<% } %>
</body>
</html>
