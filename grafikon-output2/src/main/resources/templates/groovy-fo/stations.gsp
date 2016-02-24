<?xml version="1.0" encoding="UTF-8"?>
<%
    FORMATTER = org.joda.time.format.ISODateTimeFormat.hourMinuteSecond()
    PRINT_FORMATTER = new org.joda.time.format.DateTimeFormatterBuilder().appendHourOfDay(1).appendLiteral(':').appendMinuteOfHour(2).toFormatter()

    def convertTime(time, includePart) {
        if (time == "" || time == null)
            return "";
        def parsed = FORMATTER.parseLocalTime(time)
        def result = PRINT_FORMATTER.print(parsed)
        return result
    }
    
    def highlight(str) {
        return "<inline font-weight=\"bold\">${str}</inline>"
    }
    
    def highlightHelper(str) {
        return "<inline font-style=\"italic\">${str}</inline>"
    }
    
    def comment(separator, str) {
        return (str == null || str =="") ? "" : "${separator}(${str})"
    }
    
    def getLocale(station) {
        def loc = locale
        for (region in station.regions) {
            loc = region.locale ?: loc
        }
        loc = station.company?.locale ?: loc
        return loc
    }
    
    def rarr() { return "&#8594;" }
    def trainRight() { return "&#10704;" }
    
    def textForCycle(text, cycle_t, loc) {
      def str = ""
      def name = cycle_t.name
      if (cycle_t.adjacent && !cycle_t.in) {
        name = cycle_t.in ? "${cycle_t.adjacent}${rarr()}${name}" : "${name}${rarr()}${cycle_t.adjacent}"
      }
      if (cycle_t.in) {
        str = "${text}: ${name}${comment(' ', cycle_t.desc)}"
      } else {
        if (cycle_t.trainName == null) {
          str = "${text}: ${name} ${localization.translate('ends', loc)}"
        } else {
          str = "${text}: ${name} ${localization.translate('move_to', loc)} ${cycle_t.trainName} (${convertTime(cycle_t.time, false)})"
        }
      }
      return str
    }
    
    def padding() {"padding=\".4mm .6mm .1mm 0.6mm\""}
    def border() {"border=\"solid .3mm black\""}
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
      return localization.translate("technological_time", loc)
    }

    note_parts = []
    // length
    if (row.length != null) {
      length_unit_s = row.length.lengthInAxles ? " ${localization.translate('length_axles', loc)}" : row.length.lengthUnit.getUnitsOfString(loc)
      note_parts << "[${row.length.length}${length_unit_s}]"
    }
    // engine
    def engineText = localization.translate("engine", loc)
    if (row.engine != null)
      for (engine_t in row.engine) {
        def str = textForCycle(engineText, engine_t, loc)
        str = engine_t.start ? highlight(str) : str
        str = engine_t.helper == true ? highlightHelper(str) : str
        note_parts << str
      }
    // train unit
    def trainUnitText = localization.translate("train_unit", loc)
    if (row.trainUnit != null)
      for (train_unit_t in row.trainUnit) {
        def str = textForCycle(trainUnitText, train_unit_t, loc)
        note_parts << (train_unit_t.start ? highlight(str) : str)
      }
    // other
    if (row.cycle != null)
      for (cycle_t in row.cycle) {
        def cycleTypeText = translator.translate(cycle_t.type, loc)
        def str = textForCycle(cycleTypeText, cycle_t, loc)
        note_parts << (cycle_t.start ? highlight(str) : str)
      }
    // comment
    if (row.comment != null) {
      note_parts << translator.translate(row.comment, loc)
    }
    if (row.freightTo != null) {
      note_parts << "<inline font-style=\"italic\">${row.freightTo.collect{i -> i.toString(loc, true)}.join(', ')}</inline> ${trainRight()}"
    }
    if (row.freightToTrain != null) {
      note_parts << "<inline font-style=\"italic\">${row.freightToTrain.collect{i -> '(' + rarr() + ' ' + i.train + ': ' + (i.freightTo.collect{j -> j.toString(loc, true)}.join(', ')) + ')'}.join(', ')}</inline>"
    }
    if (row.freightFromTrain != null) {
      note_parts << "<inline font-style=\"italic\">(${row.freightFromTrain.collect{i -> i + ' ' + rarr()}.join(', ')})</inline>"
    }
    // occupied track
    if (row.occupied) {
      note_parts << localization.translate("occupied", loc)
    }

    return create_note_str(note_parts)
  }

  def create_note_str(note_parts) {
    return note_parts.collect{i -> i}.join(", ");
  }
%>
<root xmlns="http://www.w3.org/1999/XSL/Format">
  <layout-master-set>
    <simple-page-master master-name="simpleA4-portrait" page-height="21cm" page-width="29.7cm" margin="1cm 1cm 1cm 1cm"
        line-stacking-strategy="font-height" line-height-shift-adjustment="disregard-shifts">
      <region-body />
    </simple-page-master>
  </layout-master-set>
  <page-sequence master-reference="simpleA4-portrait" font-family="Sans">
    <flow flow-name="xsl-region-body">
    <% printStations() %>
    </flow>
  </page-sequence>
</root>

<%
  def printStations() {
  if (!stations) { %><block></block><% }
  for (station in stations) {
      def loc = getLocale(station)
      if (!station.rows) { %><block></block><% continue }
%>
<block font-size="3mm" font-family="SansCondensed">
<table ${border()} border-collapse="collapse" table-layout="fixed" width="100%" break-after="page">
  <table-column column-width="5mm" ${border()} />
  <table-column column-width="22mm" ${border()} />
  <table-column column-width="10mm" ${border()} />
  <table-column column-width="10mm" ${border()} />
  <table-column column-width="7mm" ${border()} />
  <table-column column-width="10mm" ${border()} />
  <table-column column-width="10mm" ${border()} />
  <table-column column-width="10mm" ${border()} />
  <table-column column-width="192mm" ${border()} />
  <table-header>
      <table-row ${border()}>
          <table-cell number-columns-spanned="9" padding="1mm .6mm .1mm 0.6mm"><block font-size="5mm" text-align="center" font-weight="bold" font-family="Sans">${station.name}</block></table-cell>
      </table-row>
      <table-row ${border()} font-size="2.3mm" font-weight="bold" text-align="center">
          <table-cell ${padding()}><block>X</block></table-cell>
          <table-cell ${padding()}><block>${localization.translate("column_train", loc)}</block></table-cell>
          <table-cell ${padding()}><block>${localization.translate("column_from", loc)}</block></table-cell>
          <table-cell ${padding()}><block>${localization.translate("column_arrival", loc)}</block></table-cell>
          <table-cell ${padding()}><block>${localization.translate("column_track", loc)}</block></table-cell>
          <table-cell ${padding()}><block>${localization.translate("column_departure", loc)}</block></table-cell>
          <table-cell ${padding()}><block>${localization.translate("column_to", loc)}</block></table-cell>
          <table-cell ${padding()}><block>${localization.translate("column_end", loc)}</block></table-cell>
          <table-cell ${padding()} text-align="left"><block>${localization.translate("column_notes", loc)}</block></table-cell>
      </table-row>
  </table-header>
  <table-body>
<% for (row in station.rows) { %>
      <table-row ${border()}>
          <table-cell><block></block></table-cell>
          <table-cell ${padding()}><block>${row.trainName}</block></table-cell>
          <table-cell ${padding()}><block><% print_out(row.from,"") %></block></table-cell>
          <table-cell ${padding()}><block text-align="right"><% print_out(convertTime(row.arrival, true),"") %></block></table-cell>
          <table-cell ${padding()}><block text-align="center">${row.track}</block></table-cell>
          <table-cell ${padding()}><block text-align="right"><% print_out(convertTime(row.departure, true),"") %></block></table-cell>
          <table-cell ${padding()}><block><% print_out(row.to,"") %></block></table-cell>
          <table-cell ${padding()}><block><% print_out(row.end,"") %></block></table-cell>
          <table-cell ${padding()}><block><% note = get_note(row, loc); note != "" ? print(note) : print("") %></block></table-cell>
      </table-row>
<% } %>
  </table-body>
</table>
</block>
<% } %>
<% } %>
