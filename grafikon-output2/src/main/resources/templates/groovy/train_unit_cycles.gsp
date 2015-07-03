<%
    // definition of constants
    ROW_COUNT = 2
    COLUMN_COUNT = 2
    HEIGHT = 93
    WIDTH = 136

%><!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>${title}</title>
  <style type="text/css" media="all">
    @page {margin: 5mm; size: A4 landscape;}
    body {margin: 0; padding: 0; border: 0; width: 282mm;}
    td {padding: 0.5mm .5mm 0.5mm .5mm; border-color: black; border-style: solid; border-width: 0.2mm;}
    table.card {font-family: arial, sans-serif; font-size: 3mm; height: ${HEIGHT}mm; width: ${WIDTH}mm; border-color: black; border-style: solid; border-width: 0.2mm; page-break-inside: avoid; border-collapse: separate; border-spacing: 0;}
    table.title {border-collapse: separate; border-spacing: 0; border: 0; margin: 0; padding: 0;}
    .break {page-break-after: always;}
    .separator {height: 4mm;}
    .hseparator {margin-left: 4mm;}
    td.company {width: 25mm; font-size: 7mm; font-weight: bold;}
    td.description {border-width: 0mm; font-size:3mm;}
    td.value {border-width: 0mm; font-size:5mm; font-weight: bold;}
    div.spacer {height: 10mm;}
    tr.header {height: 12mm;}
    td.headerw1 {width: 24mm;}
    td.headerw2 {width: 89mm;}
    td.ctrain {width: 20mm; text-align: center; vertical-align: top;}
    td.cdepartureh {width: 12mm; text-align: center; vertical-align: top; font-weight: bold; padding-right: 1mm;}
    td.cdeparture {text-align: right; vertical-align: top; font-weight: bold;}
    td.cfromto {width: 17mm; text-align: center; vertical-align: top;}
    td.cnote {width: 85mm; text-align: center; vertical-align: top; font-weight: bold;}
    tr.listheader {height: 6mm; font-weight: bold ;font-size: 3mm; height: 5mm;}
    table.list {font-family: arial, sans-serif; font-size: 3mm;  border-color: black; border-style: solid; border-width: 0.2mm; border-collapse: separate; border-spacing: 0;}
    tr.listitem {}
    td.listwrap {padding: 1mm 1mm 1mm 1mm; vertical-align: top;}
    span.no {visibility: hidden;}
    
    div.row {display: flex; display: -ms-flexbox;}
    div.item {}
    div.seqf {display: flex; display: -ms-flexbox;}
    div.seql {margin-left: 1mm;}
    div.seqr {text-align: right; flex-grow: 1; margin-right: 1mm; -ms-flex: 1;}
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
<body>
<%
  boolean pageBreak = true;
  def rowCnt = 0
  def columnCnt = 0
  for (w in wrappers) {
      if (columnCnt == 0) {
          if ((rowCnt % ROW_COUNT) == 0) {
              if (rowCnt != 0) generatePageBreak()
          } else {
              generateSeparator()
          }
      }
      switch (columnCnt) {
          case 0:
              rowPrologue()
              break;
          default:
              rowDivide()
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
  
  def generateEmpty() {
  }

  def rowPrologue() {
      %><div class="row"><div class="item"><%
  }
  
  def rowEpilogue() {
      %></div></div><%
  }
  
  def rowDivide() {
      %></div><div class="item hseparator"><%
  }

  def generatePageBreak() {
      %><div class="break"></div><%
  }
  
  def generateSeparator() {
      %><div class="separator"></div><%
  }

  def generateCirculation(w) {
      def c = w.cycle
      def lLoc = getLocale(c) %>
<table class="card">
  <tr class="header">
    <td align="center" class="company">${getCompany(c, lLoc)}</td>
    <td class="headerw1">
      <table class="title">
        <tr>
          <td class="description">${localization.translate("cycle", lLoc)}:</td></tr>
        <tr>
          <td class="value">${c.name}</td></tr>
      </table>
    </td>
    <td class="headerw2">
      <table class="title">
        <tr>
          <td class="description">${localization.translate("composition", lLoc)}:</td></tr>
        <tr>
          <td class="value">${c.description == null ? "&nbsp;" : c.description}</td></tr>
      </table>
     </td>
  </tr>
  <tr style="height: 100%;">
    <td colspan="3" class="listwrap">
      <table align="center" class="list">
        <tr class="listheader">
          <td class="ctrain">${localization.translate("column_train", lLoc)}</td>
          <td class="cdepartureh">${localization.translate("column_departure", lLoc)}</td>
          <td class="cfromto">${localization.translate("column_from_to", lLoc)}</td>
          <td class="cnote">${localization.translate("column_note", lLoc)}</td>
        </tr><% for (row in c.rows) { %>
        <tr class="listitem">
          <td class="ctrain">${row.trainName}</td>
          <td class="cdeparture">${convertTime(row.fromTime)}</td>
          <td class="cfromto">${row.fromAbbr} - ${row.toAbbr}</td>
          <td>${createComment(row, lLoc)}</td>
        </tr><% } %>
      </table>
    </td>
  </tr><%
  if (c.next) { %>
  <tr>
    <td colspan="3"><div class="seqf">
      <div class="seql">&rarr; ${c.next.name}</div>
      <div class="seqr">${w.id}[${w.seq}/${w.cnt}]</div>
    <div></td>
  </tr><%
  } %>
</table><% 
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
    return result == null ? "&nbsp;" : result
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
</body>
</html>
