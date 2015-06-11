<?xml version="1.0" encoding="UTF-8"?>
<%
    separator = java.text.DecimalFormatSymbols.getInstance().getDecimalSeparator();
    END = "${separator}0"
    FORMATTER = org.joda.time.format.ISODateTimeFormat.hourMinuteSecond()
    PRINT_FORMATTER = new org.joda.time.format.DateTimeFormatterBuilder().appendHourOfDay(1).appendLiteral(':').appendMinuteOfHour(2).toFormatter()
    
    def convertTime(time) {
        def parsed = FORMATTER.parseLocalTime(time)
        def result = PRINT_FORMATTER.print(parsed)
        return result
    }
%>

<root xmlns="http://www.w3.org/1999/XSL/Format">
  <layout-master-set>
    <simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="21cm" margin-top="1.5cm" margin-bottom="1.5cm" margin-left="1.5cm" margin-right="1.5cm">
      <region-body/>
    </simple-page-master>
  </layout-master-set>
  <page-sequence master-reference="simpleA4" font-family="Sans">
    <flow flow-name="xsl-region-body">
      <block font-size="5mm" font-weight="bold" space-after="3mm">${translator.getText("title", locale)}</block>
      <% printTitle(translator.getText("title_engines", locale)) %>
      <% printPositions(engines) %>
      <% printTitle(translator.getText("title_train_units", locale)) %>
      <% printPositions(train_units) %>
      <% for (cycles in custom_cycles) { %>
      <% printTitle(translator.translate(cycles.name, locale)) %>
      <% printPositions(cycles.positions) %>
      <% } %>
    </flow>
  </page-sequence>
</root>

<% def printTitle(title) { %>
<block font-size="4mm" font-weight="bold" space-after="0.5mm">${title}</block>
<% } %>

<% def printPositions(positions) { %>
<block font-size="3mm" space-after="3mm">
<table ${border()} border-collapse="collapse">
  <table-column column-width="2cm" ${border()} />
  <table-column column-width="6cm" ${border()} />
  <table-column column-width="3.5cm" ${border()} />
  <table-column column-width="1cm" ${border()} />
  <table-column column-width="1.5cm" ${border()} />
  <table-column column-width="3cm" ${border()} />
  <table-body>
<%
  for (position in positions) {
      printPosition(position)
  } %>
  </table-body>
</table>
</block>
<% } %>

<% def printPosition(position) { %>
    <table-row ${border()}>
      <table-cell ${padding()}><block>${position.cycleName}</block></table-cell>
      <table-cell ${padding()}><block>${position.cycleDescription?:""}</block></table-cell>
      <table-cell ${padding()}><block>${position.stationName}</block></table-cell>
      <table-cell ${padding()}><block text-align="right">${position.track}</block></table-cell>
      <table-cell ${padding()}><block text-align="right">${convertTime(position.time)}</block></table-cell>
      <table-cell ${padding()}><block>${position.trainName}</block></table-cell>
    </table-row>
<% } %>
    
<% def padding() {"padding=\".4mm .8mm .3mm 0.8mm\""} %>
<% def border() {"border=\"solid .3mm\""} %>
