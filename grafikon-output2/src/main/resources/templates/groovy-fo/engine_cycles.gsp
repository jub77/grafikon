<?xml version="1.0" encoding="UTF-8"?>
<%
    // definition of constants
    HEIGHT = 85
    ROW_COUNT = 3
    COLUMN_COUNT = 4

    separator = java.text.DecimalFormatSymbols.getInstance().getDecimalSeparator();
    END = "${separator}0"
    FORMATTER = org.joda.time.format.ISODateTimeFormat.hourMinuteSecond()
    PRINT_FORMATTER = new org.joda.time.format.DateTimeFormatterBuilder().appendHourOfDay(1).appendLiteral(':').appendMinuteOfHour(2).toFormatter()

    def convertTime(time) {
        def parsed = FORMATTER.parseLocalTime(time)
        def result = PRINT_FORMATTER.print(parsed)
        return result
    }

    def padding() {'margin=".4mm .5mm .1mm .5mm"'}
    def paddingTime() {'margin=".4mm 1mm .1mm .5mm"'}
    def border() {"border=${borderValue()}"}
    def borderCirc() {'border="solid .3mm black"'}
    def borderValue() {'"solid .3mm black"'}
    def rarr() { return "&#8594;" }
    
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
%>

<root xmlns="http://www.w3.org/1999/XSL/Format">
<layout-master-set>
  <simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="21cm" margin="1cm 1cm 1cm 1cm"
      line-stacking-strategy="font-height" line-height-shift-adjustment="disregard-shifts">
    <region-body />
  </simple-page-master>
</layout-master-set>
<page-sequence master-reference="simpleA4" font-family="SansCondensed">
  <flow flow-name="xsl-region-body">
  <% printWrappers(wrappers) %>
  </flow>
</page-sequence>
</root>
<%
def printWrappers(wrappers) {
  Iterator iterator = wrappers.iterator();
  count = 0;
  while(true) {
%>
<block font-size="3mm">
<table border-collapse="separate" table-layout="fixed" width="100%">
<% for (ind in 1..COLUMN_COUNT) { %>
    <table-column column-width="${100/COLUMN_COUNT}%"/>
<% } %>
    <table-body>
        <table-row height="${HEIGHT}mm"><%
      countInRow = 0;
      c = null;
      while (true) {
        if (iterator.hasNext()) {
          c = iterator.next();
        } else {
          c = null;
        }
        if (c != null) {
            // TODO cycle${c?.cycle?.next -> check for background image for sequence
          %>
    <table-cell ${borderCirc()}>
      <% print_cycle(c) %>
    </table-cell><%
        } else {
          %>
    <table-cell><block></block></table-cell><%
        }
        countInRow++;
        if (countInRow == COLUMN_COUNT)
          break;
      }
    %>
    </table-row>
    </table-body>
</table>
</block>
<%
    if (!iterator.hasNext())
      break;
    count++;
    if (count % ROW_COUNT == 0) {
      // page break if needed
    }
  }
}

def print_cycle(w) {
    def c = w.cycle
    def loc = getLocale(c)
    def company = getCompany(c)
%>
<block>
<table border-collapse="collapse" table-layout="fixed" width="100%">
<table-body>
<table-row height="81mm"><table-cell number-columns-spanned="2"><block>
<table border-collapse="collapse" table-layout="fixed" width="100%">
    <table-column column-width="38%" />
    <table-column column-width="22%" />
    <table-column column-width="40%" />
    <table-body>
        <table-row font-size="2mm">
          <table-cell number-columns-spanned="3"><block ${padding()}>${translator.getText("cycle", loc)}:${company ? " " + company : ""}</block></table-cell>
        </table-row>
        <table-row>
          <table-cell font-size="4.5mm" font-weight="bold"><block margin-left=".2mm">${c.name}</block></table-cell>
          <table-cell font-size="3.5mm" font-weight="bold" number-columns-spanned="2"><block>${c.description ?: ""}</block></table-cell>
        </table-row>
        <table-row font-size="2mm" text-align="center" border-top=${borderValue()}>
          <table-cell><block margin-top=".2mm">${translator.getText("column_train", loc)}</block></table-cell>
          <table-cell><block margin-top=".2mm">${translator.getText("column_departure", loc)}</block></table-cell>
          <table-cell><block margin-top=".2mm">${translator.getText("column_from_to", loc)}</block></table-cell>
        </table-row><% for (row in c.rows) {
                  if (row.wait > 25*60) {
                    %>
        <table-row border-top="solid .2mm black">
          <table-cell number-columns-spanned="3"><block></block></table-cell>
        </table-row><%
                  }
              %>
        <table-row${row.helper ? ' font-style="italic"' : ""}>
          <table-cell><block ${padding()}>${row.trainName}</block></table-cell>
          <table-cell><block ${paddingTime()} text-align="right" font-weight="bold">${convertTime(row.fromTime)}</block></table-cell>
          <table-cell><block ${padding()}>${row.fromAbbr} - ${row.toAbbr}</block></table-cell>
        </table-row><% } %></table-body></table></block></table-cell></table-row>
<% if (c.next) { %> 
        <table-row height="4mm" border-top=${borderValue()}>
          <table-cell><block ${padding()}>${rarr()} ${c.next.name}</block></table-cell>
          <table-cell text-align="right"><block ${padding()}>${w.id}[${w.seq}/${w.cnt}]</block></table-cell>
        </table-row><% } else {%>
            <table-row height="4.3mm"><table-cell number-columns-spanned="2"><block></block></table-cell></table-row>
        <% } %>
    </table-body>
</table>
</block><%
}

def getCompany(cycle) {
    def company = cycle?.company?.abbr
}
  
def getLocale(cycle) {
    def l = cycle?.company?.locale
    return l ?: locale
}
%>
