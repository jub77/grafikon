<%
    // definition of constants
    WIDTH = 46.5
    HEIGHT = 85
    ROW_COUNT = 3
    COLUMN_COUNT = 4

%><html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>${title}</title>
  <style type="text/css" media="all">
    table.cycles {border-color: black; border-style: solid; border-width: 0mm;}
    tr.cycles {height: ${HEIGHT}mm;}
    .break {page-break-before: always; font-size: 1mm;}
    td.cycle {border-color: black; border-style: solid; border-width: 0.4mm; vertical-align: top; position: relative;}
    td.cycle table {font-family: arial, sans-serif; font-size: 3mm; width: ${WIDTH}mm; border-color: black; border-style: solid; border-width: 0mm;}
    tr.title {height: 3mm;}
    tr.title td {font-size: 2mm;}
    tr.info {height: 6mm;}
    td.info1 {font-size: 5mm; text-align: center; font-weight: bold;}
    td.info2 {font-size: 4mm; text-align: center; font-weight: bold;}
    tr.listh {height: 3mm; text-align: center;}
    td.trainh {border-color: black; border-style: solid; border-width: 0.2mm 0mm 0mm 0mm; font-size: 2mm; width: 20mm;}
    td.timeh {border-color: black; border-style: solid; border-width: 0.2mm 0mm 0mm 0mm; font-size: 2mm; width: 10mm;}
    td.fromtoh {border-color: black; border-style: solid; border-width: 0.2mm 0mm 0mm 0mm; font-size: 2mm; width: 20mm;}
    tr.row {height: 4mm; text-align: center;}
    tr.emph {font-style: italic;}
    td.trow {text-align: left; padding-left: 0.8mm;}
    td.drow {font-weight: bold; text-align: right; padding-right: 0.1mm;}
    td.ftrow {}
    tr.delim {height: .5mm;}
    tr.delim td {font-size: 0.2mm; border-color: black; border-style: solid; border-width: 0.35mm 0mm 0mm 0mm; width: 21mm;}
    span.no {visibility: hidden;}
    div.footer {font-family: arial, sans-serif; font-size: 3mm; position: absolute; bottom: 0; border-width: 0.2mm 0 0 0; border-style: solid; width: 100%;}
    div.next {padding-left: 0.8mm; float: left;}
    div.citem {padding-right: 0.8mm; float: right;}
    td.sequence {background-image: url("arrow_seq.svg");}
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
            images << "arrow_seq.svg"
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
%>
<body>
<%
  Iterator iterator = wrappers.iterator();
  count = 0;
  while(true) {
%>
<table class="cycles" align="center" cellspacing="0" cellpadding="0">
  <tr class="cycles"><%
      countInRow = 0;
      c = null;
      while (true) {
        if (iterator.hasNext()) {
          c = iterator.next();
        } else {
          c = null;
        }
        if (c != null) {
          %>
    <td class="cycle${c?.cycle?.next ? ' sequence' : ''}">
      <% print_cycle(c) %>
    </td><%
        } else {
          %>
    <td class="cycle">
      <table align="center" cellspacing="0">
        <tr><td>&nbsp;</td></tr>
      </table>
    </td><%
        }
        countInRow++;
        if (countInRow == COLUMN_COUNT)
          break;
      }
    %>
  </tr>
</table>
<%
    if (!iterator.hasNext())
      break;
    count++;
    if (count % ROW_COUNT == 0) {
      %><div class="break">&nbsp;</div>
<%
    }
  }

def print_cycle(w) {
    def c = w.cycle
    def loc = getLocale(c)
    def company = getCompany(c)
%><table align="center" cellspacing="0">
        <tr class="title">
          <td colspan="3">${translator.getText("cycle", loc)}:${company ? " " + company : ""}</td>
        </tr>
        <tr class="info">
          <td class="info1">${c.name}</td>
          <td class="info2" colspan="2">${c.description ?: ""}</td>
        </tr>
        <tr class="listh">
          <td class="trainh">${translator.getText("column_train", loc)}</td>
          <td class="timeh">${translator.getText("column_departure", loc)}</td>
          <td class="fromtoh">${translator.getText("column_from_to", loc)}</td>
        </tr><% for (row in c.rows) {
                  if (row.wait > 25*60) {
                    %>
        <tr class="delim">
          <td colspan="3">&nbsp;</td>
        </tr><%
                  }
              %>
        <tr class="row${row.helper ? " emph" : ""}">
          <td class="trow">${row.trainName}</td>
          <td class="drow">${convertTime(row.fromTime)}</td>
          <td class="ftrow">${row.fromAbbr} - ${row.toAbbr}</td>
        </tr><% } %>
      </table><% if (c.next) { %>
      <div class="footer">
          <div class="next">&nbsp;&rarr; ${c.next.name}</div>
          <div class="citem">${w.id}[${w.seq}/${w.cnt}]</div>
      </div><% }
}

def getCompany(cycle) {
    def company = cycle?.company?.abbr
}

def getLocale(cycle) {
    def l = cycle?.company?.locale
    return l ?: locale
}
%>
</body>
</html>
