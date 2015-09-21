<?xml version="1.0" encoding="UTF-8"?>
<%
    // definition of constants
    ROW_COUNT = 2
    COLUMN_COUNT = 2
    HEIGHT = 95
    
    BORDER = "solid 0.4mm black"
    RARR = "&#8594;"
    MARGIN = ".4mm .5mm .1mm .5mm"

    FORMATTER = org.joda.time.format.ISODateTimeFormat.hourMinuteSecond()
    PRINT_FORMATTER = new org.joda.time.format.DateTimeFormatterBuilder().appendHourOfDay(1).appendLiteral(':').appendMinuteOfHour(2).toFormatter()

    def convertTime(time) {
        def parsed = FORMATTER.parseLocalTime(time)
        def result = PRINT_FORMATTER.print(parsed)
        return result
    }

    // counter of sequences
    seqId = 1
    
    def getSequence(cycle) {
        if (!cycle.next) {
            return [new CycleWrapper(cycle)]
        } else {
            def sequence = []
            def current = cycle
            def cnt = 0
            while (true) {
                sequence << new CycleWrapper(current, ++cnt, seqId)
                current = current.next
                if (current == cycle) break
            }
            for (w in sequence) {
                w.cnt = cnt
            }
            seqId++
            return sequence
        }
    }
    
    def getFirstFreeSequence(sequences, freeColumns) {
        if (sequences.empty) {
            return null
        } else {
            def selected = [null]
            for (sequence in sequences) {
                if (sequence.size <= freeColumns || sequence.size > COLUMN_COUNT) {
                    selected = sequence
                    sequences.remove(selected)
                    break
                }
            }
            return selected
        }
    }

    class CycleWrapper {
        def cycle
        def cnt
        def seq
        def id

        CycleWrapper(cycle, seq, id) {
            this.cycle = cycle
            this.seq = seq
            this.id = id
        }

        CycleWrapper(cycle) {
            this.cycle = cycle
        }
    }

    // prepare cycles - duplicate the ones needed in sequences
    def sequences = [] 
    for (cycle in cycles) {
        sequences << getSequence(cycle)
    }
    def wrappers = []
    def currentColumn = 0
    while (true) {
        if (sequences.empty) break;
        def freeColumns = COLUMN_COUNT - currentColumn
        def sequence = getFirstFreeSequence(sequences, freeColumns)
        for (w in sequence) {
            ++currentColumn
            wrappers << w
            if (currentColumn == COLUMN_COUNT) currentColumn = 0
        }
    }
    def notAligned = wrappers.size % COLUMN_COUNT
    if (notAligned != 0) {
        (COLUMN_COUNT - notAligned).times {wrappers << null}
    }
%>
<root xmlns="http://www.w3.org/1999/XSL/Format">
<layout-master-set>
  <simple-page-master master-name="simpleA4-portrait" page-height="21cm" page-width="29.7cm" margin=".5cm .5cm .5cm .5cm"
      line-stacking-strategy="font-height" line-height-shift-adjustment="disregard-shifts">
    <region-body />
  </simple-page-master>
</layout-master-set>
<page-sequence master-reference="simpleA4-portrait" font-family="SansCondensed">
  <flow flow-name="xsl-region-body">
  <% printWrappers(wrappers) %>
  <% if (!wrappers) { %><block></block><% } %>
  </flow>
</page-sequence>
</root>
<%
def printWrappers(wrappers) {
  boolean pageBreak = true;
  def rowCnt = 0
  def columnCnt = 0
  for (w in wrappers) {
      switch (columnCnt) {
          case 0:
              rowPrologue()
              break;
          default:
              columnDivide()
              break;
      }
      if (w == null) {
          generateEmpty();
      } else {
          generateCirculation(w)
      }
      columnCnt++
      switch (columnCnt) {
          case COLUMN_COUNT:
              rowEpilogue()
              break;
      }
      if (columnCnt == COLUMN_COUNT) {
          columnCnt = 0
          rowCnt++
      }
  }
}
  
  def generateEmpty() {
    %><table-cell><block></block></table-cell><%
  }

  def rowPrologue() {
      %><table border-collapse="separate" border-separation="4mm" table-layout="fixed" width="100%"><table-body><table-row height="${HEIGHT}mm">
  <%
  }
  
  def rowEpilogue() {
      %></table-row></table-body></table>
  <%
  }
  
  def columnDivide() {
      %>
  <%
  }

  def generateCirculation(w) {
      def c = w.cycle
      def lLoc = getLocale(c) %>
<table-cell border="${BORDER}"><block>
<table font-size="3mm" border-collapse="collapse" border-separation="0" table-layout="fixed" width="100%">
<table-body><table-row height="${HEIGHT-4.5}mm"><table-cell number-columns-spanned="2"><block>
<table table-layout="fixed" width="100%">
  <table-body>
  <table-row border-bottom="${BORDER}" height="11mm">
    <table-cell width="25mm" border-right="${BORDER}" display-align="center" text-align="center"><block font-size="5.5mm" font-weight="bold" font-family="Sans" margin="2mm 1mm .3mm 1mm">${getCompany(c, lLoc)}</block></table-cell>
    <table-cell width="25mm" border-right="${BORDER}">
      <block margin="${MARGIN}" font-size="2.5mm">${localization.translate("cycle", lLoc)}:</block>
      <block margin="${MARGIN}" font-size="4.5mm" font-weight="bold">${c.name}</block>
    </table-cell>
    <table-cell>
      <block margin="${MARGIN}" font-size="2.5mm">${localization.translate("composition", lLoc)}:</block>
      <block margin="${MARGIN}" font-size="4mm" font-weight="bold">${c.description ?: ""}</block>
     </table-cell>
  </table-row>
  <table-row>
    <table-cell number-columns-spanned="3" padding="2mm 2mm 0mm 2mm"><block>
      <table table-layout="fixed" width="100%"><table-body>
        <table-row text-align="center" font-weight="bold" font-size="2.5mm">
          <table-cell border="${BORDER}" width="20mm"><block margin="${MARGIN}">${localization.translate("column_train", lLoc)}</block></table-cell>
          <table-cell border="${BORDER}" width="12mm"><block margin="${MARGIN}">${localization.translate("column_departure", lLoc)}</block></table-cell>
          <table-cell border="${BORDER}" width="20mm"><block margin="${MARGIN}">${localization.translate("column_from_to", lLoc)}</block></table-cell>
          <table-cell border="${BORDER}"><block margin="${MARGIN}">${localization.translate("column_note", lLoc)}</block></table-cell>
        </table-row><% for (row in c.rows) { %>
        <table-row>
          <table-cell border="${BORDER}"><block margin="${MARGIN}">${row.trainName}</block></table-cell>
          <table-cell border="${BORDER}"><block margin="${MARGIN}" text-align="right" font-weight="bold">${convertTime(row.fromTime)}</block></table-cell>
          <table-cell border="${BORDER}"><block margin="${MARGIN}">${row.fromAbbr} - ${row.toAbbr}</block></table-cell>
          <table-cell border="${BORDER}"><block margin="${MARGIN}">${createComment(row, lLoc)}</block></table-cell>
        </table-row><% } %>
      </table-body></table></block>
    </table-cell>
  </table-row>
</table-body></table>
</block></table-cell></table-row><%
  if (c.next) { %>
  <table-row border-top="${BORDER}">
    <table-cell><block margin="${MARGIN}">${RARR} ${c.next.name}</block></table-cell>
    <table-cell text-align="right"><block margin="${MARGIN}">${w.id}[${w.seq}/${w.cnt}]</block></table-cell>
  </table-row><%
  } %>
</table-body></table>
</block></table-cell>
<% 
  }

  def createComment(row, loc) {
    def result = row.cycle.inject(row.comment) {
      str, item ->
        if (str == null)
          str = ""
        if (str != "")
          str += ", "
        def value = "${translator.translate(item.type, loc)}: ${item.name}"
        if (item.fromAbbr != null)
          value = "${value} (${item.fromAbbr} - ${item.toAbbr})"
        str + value
    }
    return result == null ? "" : result
  }
  
  def getCompany(cycle, loc) {
      def company = cycle?.company?.abbr
      return company ?: localization.translate("company", loc)
  }
  
  def getLocale(cycle) {
      def l = cycle?.company?.locale
      return l ?: locale
  }
%>
