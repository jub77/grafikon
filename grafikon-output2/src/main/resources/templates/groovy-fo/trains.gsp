<?xml version="1.0" encoding="UTF-8"?>

<%
  FORMATTER = org.joda.time.format.ISODateTimeFormat.hourMinuteSecond()
  PRINT_FORMATTER = new org.joda.time.format.DateTimeFormatterBuilder().appendHourOfDay(1).appendLiteral(':').appendMinuteOfHour(2).toFormatter()
  
  // adapt locale (if cycle contains definition of one)
  def cycleLocale = trains?.cycle?.company?.locale
  locale = cycleLocale ?: locale
  
  DAGGER = "&#8224;"
  DELTA = "&#916;"
  OMICRON = "&#927;"
  LOZ = "&#9674;"
  RARR = "&#8594;"

  limited = settings['timetable.limit.to.circulation'] && trains.cycle
  tiHolder = TrainsItemsHolder.create(trains, limited)
  showTitleStart = settings['timetable.title.start']
  useSetupTime = settings['timetable.setup.time']
  useTechnologicalTime = settings['timetable.technological.time']
  if (settings.containsKey('timetable.title.page')) {
      title_page = settings['timetable.title.page']
  }
%>

<root xmlns="http://www.w3.org/1999/XSL/Format">
<layout-master-set>
  <simple-page-master master-name="A5-Title" page-height="21cm" page-width="14.8cm" margin="1cm 1cm 1cm 1cm"
      line-stacking-strategy="font-height" line-height-shift-adjustment="disregard-shifts">
    <region-body />
    <region-after region-name="footer" precedence="true" extent="2.5cm"/>
  </simple-page-master>
  <simple-page-master master-name="A5-Pages-Even" page-height="21cm" page-width="14.8cm" margin=".5cm 1cm .5cm 1cm"
      line-stacking-strategy="font-height" line-height-shift-adjustment="disregard-shifts">
    <region-body margin-top=".5cm" margin-bottom=".5cm"/>
    <region-before region-name="header-even" precedence="true" extent=".5cm" />
    <region-after region-name="footer-even" precedence="true" extent=".5cm" />
  </simple-page-master>
  <simple-page-master master-name="A5-Pages-Odd" page-height="21cm" page-width="14.8cm" margin=".5cm 1cm .5cm 1cm"
      line-stacking-strategy="font-height" line-height-shift-adjustment="disregard-shifts">
    <region-body margin-top=".5cm" margin-bottom=".5cm"/>
    <region-before region-name="header-odd" precedence="true" extent=".5cm" />
    <region-after region-name="footer-odd" precedence="true" extent=".5cm" />
  </simple-page-master>
  <simple-page-master master-name="A5-Pages-List" page-height="21cm" page-width="14.8cm" margin=".5cm 1cm .5cm 1cm"
      line-stacking-strategy="font-height" line-height-shift-adjustment="disregard-shifts">
    <region-body margin-top=".5cm" margin-bottom=".5cm"/>
    <region-after region-name="footer-even" precedence="true" extent=".5cm" />
  </simple-page-master>
  <page-sequence-master master-name="A5-Pages">
    <repeatable-page-master-alternatives>
    <% if (title_page) { %><conditional-page-master-reference page-position="first" master-reference="A5-Pages-List"/><% } %>
    <conditional-page-master-reference odd-or-even="even" master-reference="A5-Pages-Even"/>
    <conditional-page-master-reference odd-or-even="odd" master-reference="A5-Pages-Odd"/>
    </repeatable-page-master-alternatives>
  </page-sequence-master>
</layout-master-set>
<% if (title_page) printTitlePage() %>
<page-sequence master-reference="A5-Pages" font-family="SansCondensed" font-size="4mm">
<static-content flow-name="header-even">
  <block text-align-last="justify" font-size="3mm"><page-number/><leader leader-pattern="space" />${localization.translate("header_publisher", locale)}</block>
</static-content>
<static-content flow-name="header-odd">
  <block text-align-last="justify" font-size="3mm">${localization.translate("header_publisher", locale)}<leader leader-pattern="space" /><page-number/></block>
</static-content>
<static-content flow-name="footer-even">
  <block></block>
</static-content>
<static-content flow-name="footer-odd">
  <block></block>
</static-content>
<flow flow-name="xsl-region-body">
  <% if (title_page) printTrainList() %>
  <% printTimetables() %>
</flow>
</page-sequence>
</root>

<!-- ======================== Title Page ======================== -->
<%
def printTitlePage() {
  def company = getCompany(trains.cycle, locale)
  def company_part = getCompanyPart(trains.cycle, locale)
%>
<page-sequence master-reference="A5-Title" font-family="Sans" font-size="4mm">
<static-content flow-name="footer">
  <block text-align="center" font-size="3.5mm" space-after="15mm">${localization.translate("internal_use", locale)}</block>
  <block text-align="center" font-size="3mm">${localization.translate("publisher", locale)}</block>
</static-content>
<flow flow-name="xsl-region-body">
  <block-container text-align="center" font-weight="bold" height="35mm">
    <block>${company}</block>
    <block>${company_part}</block>
  </block-container>
  <block-container text-align="center" font-weight="bold">
    <block font-size="8mm" space-after="7mm">${localization.translate("train_timetable", locale)}</block>
    <block linefeed-treatment="preserve" font-size="12mm">${getRouteNames(trains)}</block>
  </block-container>
  <block text-align="center">${localization.translate("for_line", locale)}</block>
  <block linefeed-treatment="preserve" text-align="center" font-weight="bold" space-after="7mm">${getRoutePaths(trains)}</block>
  <% if (trains.validity) { %><block font-weight="bold" text-align="center" space-after="15mm">${localization.translate("validity_from", locale)} ${trains.validity}</block><% } %>
  <% if (trains.cycle) { %><block font-weight="bold" text-align="center" space-after="3mm" font-size="5mm">${localization.translate("cycle", locale)}: ${trains.cycle.name}</block><% } %>
  <% if (trains.cycle && trains.cycle.description) { %><block text-align="center" space-after="4mm">${trains.cycle.description}</block><% } %>
  <% if (showTitleStart && trains.cycle && tiHolder.items) { %><block text-align="center" font-size="4.75mm">${getStartTimeComment(tiHolder.items[0].item, locale)}</block><% } %>
</flow>
</page-sequence>
<%
}
%>

<!-- ======================== Train List ======================== -->
<%
def printTrainList() { 
if (trains.cycle) { %>

<block>${localization.translate("list_train_title", locale)}:</block>
<table border-collapse="collapse" table-layout="fixed" space-after="4mm" width="100%">
  <table-column column-width="23%" />
  <table-column column-width="12%" />
  <table-column column-width="19%" />
  <table-column column-width="38.7%" />
  <table-column column-width="7.3%" />
  <table-header font-size="3mm">
    <table-row border-top="solid .2mm" border-bottom="solid .2mm">
      <table-cell><block>${localization.translate("column_train", locale)}</block></table-cell>
      <table-cell><block>${localization.translate("column_departure", locale)}</block></table-cell>
      <table-cell><block>${localization.translate("column_from_to", locale)}</block></table-cell>
      <table-cell number-columns-spanned="2"><block text-align-last="justify"><inline>${localization.translate("column_note", locale)}</inline><leader leader-pattern="space" /><inline>${localization.translate("index_page", locale)}</inline></block></table-cell>
    </table-row>
  </table-header>
  <table-body>
<%
  def lastNode = null
  def abbrMap = [:]
  def index = 0
  for (iHolder in tiHolder.items) {
    def item = iHolder.item
    abbrMap[item.fromAbbr] = item.from
    abbrMap[item.toAbbr] = item.to
    if (lastNode != null && lastNode != item.from) {
    %><table-row><table-cell number-columns-spanned="4"><block>&#8212; ${localization.translate("move_to_station", locale)} ${item.from} &#8212;</block></table-cell></table-row><%
    }
    lastNode = item.to
%>
    <table-row>
      <table-cell><block>${item.trainName}</block></table-cell>
      <table-cell margin-right="2mm"><block text-align="right" font-weight="bold">${convertTime(item.fromTime)}</block></table-cell>
      <table-cell><block>${item.fromAbbr} - ${item.toAbbr}</block></table-cell>
      <table-cell><block><inline font-size="3.5mm" vertical-align="middle">${getItemComment(item, locale)}</inline></block></table-cell>
      <table-cell><block text-align="right"><page-number-citation ref-id="${iHolder.trainHolder.refId}"/></block></table-cell>
    </table-row>
<%
    index++
  } %>
  </table-body>
</table>
<%
  abbrMap.sort().each {
    %><block font-size="3.25mm">${it.key} - ${it.value}</block><%
  }
}
%>
<block page-break-after="always">&#160;</block>
<%
}
%>

<!-- ======================== Timetables ======================== -->
<%
def printTimetables() {
  def index = 0
  for (tHolder in tiHolder.trains) {
    def train = tHolder.train
    def iHolder = tiHolder.items ? tiHolder.items[index] : null
%>
<block-container keep-together.within-page="always" space-before.minimum="1mm" space-before.optimum="5mm" space-before.maximum="7mm">
  <block text-align="center" id="${tHolder.refId}" font-weight="bold" font-size="5mm">${train.completeName}</block>
  <% if (settings['timetable.show.circulation'] && trains.cycle) { %><block text-align="center" font-size="3.5mm" font-weight="bold">${trains.cycle.name}</block><% } %>
  <% if (iHolder) {
       def setupComment = getSetupTimeComment(iHolder.item, locale) 
       if(setupComment) { %><block text-align="center" font-size="3.5mm" space-after=".75mm">${setupComment}</block><% }
     }
  %>
  <block-container font-size="3mm">
  <!-- ======== Route info ========= -->
  <% if (train.routeInfo) { %><block text-align="center" space-after=".8mm">${train.routeInfo.collect{it.part}.join(' - ')}</block><% } %>
  <% if (!train.routeInfo && !tHolder.whole) { %><block text-align="center" space-after=".8mm">${train.rows.first().station} - ${train.rows.last().station}</block><% } %>
  <!-- ======== Weight info ========= -->
  <%
     def fwt = true
     if (train.weightData && !settings['timetable.hide.weight.info']) {
       lastEngine = null
       %><table border-collapse="collapse" table-layout="fixed" width="100%" font-size="2.75mm"><table-column column-width="50%"/><table-column column-width="50%"/><table-body><%
       for (wr in train.weightData) {
         currentEngine = wr.engines.join(", ") %>
      <table-row>
        <table-cell><block text-align-last="justify" margin-right="1mm"><inline>${(currentEngine && currentEngine != lastEngine) ? (localization.translate(train.diesel ? "diesel_unit" : "engine", locale) + " " + currentEngine + ". ") : ""}</inline>
        <leader leader-pattern="space" /><inline>${(wr.weight  && (fwt || (currentEngine && currentEngine != lastEngine))) ? localization.translate("norm_load", locale) + ":" : "&#160;"}</inline></block></table-cell>
        <table-cell><block text-align-last="justify"><inline>${wr.from  && wr.to ? wr.from + " - " + wr.to : "&#160;"}</inline><leader leader-pattern="space" />
        <inline>${wr.weight ? wr.weight + " " + localization.translate("tons", locale) : "&#160;"}</inline></block></table-cell>
      </table-row><%
         fwt = false
         lastEngine = currentEngine
       }
       %></table-body></table><%
     } %>
  <!-- ======== Length info ========= -->
  <%
     if (train.lengthData) {
       if (train.lengthData.length % 2 == 1) {
         train.lengthData.length = train.lengthData.length - 1
       } %>
      <block font-size="2.75mm">
        <inline>${localization.translate("length", locale)}: ${train.lengthData.length} ${train.lengthData.lengthInAxles ? localization.translate("length_axles", locale) : train.lengthData.lengthUnit.getUnitsOfString(locale)}</inline>
      </block><%
      } %>
  <!-- ========= Rows ============ -->
  <%
  printTimetableHeader(train.controlled)
  def rowL = train.rows.size - 1
  def cnt = 0
  def lastSpeed = null
  def lastSpeed2 = null
  def isSpeed2 = false
  def fromT = new Time()
  def toT = new Time()
  def stopDur = new Duration()
  def runDur = new Duration()
  def lastTo = null
  def lastLineClass = null
  def cChar = "*"
  def fChar = DAGGER // dagger
  for (row in train.rows) {
    def speed = row.setSpeed != null ? row.setSpeed : row.speed
    def speed2 = row.speed
    isSpeed2 = isSpeed2 || speed != speed2
    def emphName = (cnt == 0) || (cnt == rowL) || row.stationType == "branch.station"
    def speedStr = ((lastSpeed == null || lastSpeed != speed) && speed != null) ?  speed : " "
    fromT.compute(row.arrival, cnt == rowL || tHolder.isLimit(cnt), row.arrival != row.departure)
    toT.compute(row.departure, tHolder.isLimit(cnt), true)
    def stationName = row.station
    def desc = ""
    if (row.stationType == "stop.with.freight") stationName += " ${localization.translate('abbr_stop_freight', locale)}"
    if (row.stationType == "stop") stationName += " ${localization.translate('abbr_stop', locale)}"
    if (emphName) stationName = "<inline font-weight=\"bold\">${stationName}</inline>"
    if (row.straight == false && !row.lightSignals) desc += RARR // rarr
    if (row.lightSignals) { desc += getImage("images/signal.svg", "3.4mm") } 
    if (train.controlled && row.trapezoid) {
      desc += getImage("images/trapezoid_sign.svg", "3.2mm")
    }
    if (row.lineEnd) desc += DELTA // Delta
    if (row.occupied) desc += OMICRON // Omicron
    if (row.shunt) desc += LOZ // loz
    if (row.comment != null) {desc += cChar; cChar += "*"}
    if (freight && row.freightDest != null) {desc += fChar; fChar += DAGGER} // dagger
    if (desc == "") desc = " "
    def speed2Str = (lastSpeed2 == null || lastSpeed2 != speed2) && speed2 != null && isSpeed2 ? speed2 : null;
    def lineClassStr = " "
    if ((lastLineClass == null || (lastLineClass != row.lineClass)) && row.lineClass != null) {
      lineClassStr += row.lineClass
      if (isSpeed2)
        lineClassStr += "/" + speed2
    } else if (speed2Str != null) {
        lineClassStr += (row.lineClass != null ? row.lineClass : "-") + "/" + speed2Str
    }
    lastLineClass = row.lineClass
    def showTrack
    def tTrains
    if (train.controlled) {
      showTrack = row.track != null && !row.controlStation && row.onControlled
      tTrains = null
      if (row.trapezoidTrains != null) {
        for (tTrain in row.trapezoidTrains) {
          if (tTrains == null) tTrains = ""
          else tTrains += ", "
          tTrains += tTrain
        }
      }
      if (tTrains == null) tTrains = " "
    }
    if (!limited || tHolder.isIn(cnt)) {
      %>
      <table-row>
        <table-cell><block text-align-last="justify" margin-right="1mm"><inline>${stationName}${train.controlled && row.controlStation ? " " + getImage("images/control_station.svg", "2.7mm") : ""}</inline> <leader leader-pattern="dots" font-family="Sans" /></block></table-cell>
        <table-cell><block text-align="center">${desc}</block></table-cell>
        <% if (train.controlled) { %><table-cell><block text-align="center">${showTrack ? row.track : " "}</block></table-cell><% } %>
        <table-cell><block text-align="right" ${marginTR()} font-weight="bold">${cnt != tHolder.start ? runDur.show(lastTo, row.arrival) : ""}</block></table-cell>
        <table-cell><block text-align="right" ${marginTR()} font-weight="bold" font-size="4mm">${fromT.out}</block></table-cell>
        <table-cell><block text-align="right" ${marginTR()}>${stopDur.show(row.arrival,row.departure)}</block></table-cell>
        <table-cell><block text-align="right" ${marginTR()} font-weight="bold" font-size="4mm">${toT.out}</block></table-cell>
        <table-cell><block text-align="right" ${marginTR()}>${speedStr}</block></table-cell>
        <table-cell><block font-size="3mm" ${marginTL()}>${lineClassStr}</block></table-cell>
        <% if (train.controlled) { %><table-cell><block font-size="2.5mm" ${marginTL()}>${tTrains}</block></table-cell><% } %>
      </table-row><%
    }
    cnt++
    lastSpeed = speed
    lastSpeed2 = speed2
    lastTo = row.departure
  }
  
  def timeTotal = stopDur.total + runDur.total
  def totalHours = (int) (timeTotal / 60)
  def totalMinutes = timeTotal - totalHours * 60
  def totalMinutesStr = Duration.show(totalMinutes)

  %>
  <table-row border-top="solid .4mm" border-bottom="solid .4mm">
    <table-cell number-columns-spanned="${train.controlled ? '3' : '2'}"><block margin-top=".3mm" margin-right="1mm" text-align="right">${localization.translate("total_train_time", locale)} . . .</block></table-cell>
    <table-cell><block margin-top=".3mm" text-align="right" ${marginTR()}>${runDur.showTotal()}</block></table-cell>
    <table-cell><block margin-top=".3mm" text-align="center">+</block></table-cell>
    <table-cell><block margin-top=".3mm" text-align="right" ${marginTR()}>${stopDur.showTotal()}</block></table-cell>
    <table-cell number-columns-spanned="${train.controlled ? '4' : '3'}"><block margin-top=".3mm" margin-left="1mm">= ${totalHours != 0 ? totalHours + " " : ""}${totalHours != 0 ? localization.translate("hours", locale) + " " : ""}${totalMinutes != 0 ? totalMinutesStr : ""}${totalMinutes != 0 ? localization.translate("minutes", locale) : ""}</block></table-cell>
  </table-row><%
  // ---------------- footer of the timetable -----------
  printTimetableFooter()
  
  comments = createComments(tHolder)
  for (comment in comments) {
    // ----------- comments --------------
    %><block><inline-container width="8mm"><block text-align-last="justify">${comment[0]}<leader leader-pattern="space" />=</block></inline-container><inline-container width="100%"><block margin-left="1mm">${comment[1]}</block></inline-container></block><%
  }

  %>
  </block-container>
</block-container>
<%
    index++
  }
}
%>

<!-- ======================== Print timetable =================== -->
<%
def printTimetableHeader(controlled) {
%><table border-collapse="collapse" table-layout="fixed" width="100%" font-size="3.5mm" display-align="center" space-after=".7mm">
  <table-column column-width="31%" ${separationT()} />
  <table-column column-width="6%" ${separationT()} />
  <% if (controlled) { %><table-column column-width="6%" ${separationT()} /><% } %>
  <table-column column-width="6%" ${separationT()} />
  <table-column column-width="11%" ${separationT()} />
  <table-column column-width="6%" ${separationT()} />
  <table-column column-width="11%" ${separationT()} />
  <table-column column-width="6%" ${separationT()}  />
  <table-column column-width="${controlled ? '4.5%' : '23%'}" ${controlled ? separationT() : ""}/>
  <% if (controlled) { %><table-column column-width="12.5%" /><% } %>
  <table-header font-size="2mm">
    <table-row border-top="solid .7mm" border-bottom="solid .4mm" text-align="center">
      <table-cell ${paddingTTop()}><block>1</block></table-cell>
      <table-cell ${paddingTTop()}><block>2</block></table-cell>
      <% if (controlled) { %><table-cell ${paddingTTop()}><block>2a</block></table-cell><% } %>
      <table-cell ${paddingTTop()}><block>3</block></table-cell>
      <table-cell ${paddingTTop()}><block>4</block></table-cell>
      <table-cell ${paddingTTop()}><block>5</block></table-cell>
      <table-cell ${paddingTTop()}><block>6</block></table-cell>
      <table-cell ${paddingTTop()}><block>7</block></table-cell>
      <table-cell ${paddingTTop()}><block>8</block></table-cell>
      <% if (controlled) { %><table-cell ${paddingTTop()}><block>9</block></table-cell><% } %>
    </table-row>
  </table-header>
  <table-body><%
}
  
def getImage(image, height) {
  return "<external-graphic src=\"${image}\" height=\"${height}\" content-width=\"scale-to-fit\" content-height=\"scale-to-fit\" vertical-align=\"middle\" padding-right=\".3mm\"/>"
}
  
def paddingTTop() {
  return 'padding-top=".3mm"'
}
  
def separationT() {
  return 'border-right="solid .2mm"'
}

def marginTR() {
  return 'margin-right=".7mm"'
}

def marginTL() {
  return 'margin-left=".7mm"'
}

def printTimetableFooter() {
%></table-body></table><%
}
%>


<!-- ======================== Helper methods =================== -->
<%
  def getCompany(cycle, loc) {
    def company = cycle?.company?.name
    company = company ?: cycle?.company?.abbr
    return company ?: localization.translate("company", loc)
  }

  def getCompanyPart(cycle, loc) {
    def part = cycle?.company?.part
    if (!part && !cycle?.company?.abbr) {
        part = localization.translate("company_part", loc)
    }
    return part ?: ""
  }

  // returns names of routes
  def getRouteNames(trains) {
    def result = ""
    def routeNames = [] as Set
    if (trains.routes != null && !trains.routes.isEmpty()) {
      for (route in trains.routes) {
        if (!routeNames.contains(route.name)) {
          result = add(result,"\n",route.name)
          routeNames << route.name
        }
      }
    } else {
      result = (trains.routeNumbers == null) ? "-" : trains.routeNumbers
    }
    return result
  }

  // returns paths of routes
  def getRoutePaths(trains) {
    def result = ""
    if (trains.routes != null && !trains.routes.isEmpty()) {
      for (route in trains.routes) {
        def stationsStr = null
        stationsStr = add(stationsStr," - ",route.segments.first().name)
        stationsStr = add(stationsStr," - ",route.segments.last().name)
        result = add(result,"\n",stationsStr)
      }
    } else {
      result = (trains.routeStations == null) ? "-" : trains.routeStations
    }
    return result
  }

  def add(str, delimiter, value) {
    if (str == null || str.isEmpty())
      str = value
    else
      str += delimiter + value
    return str
  }

  def getItemComment(item, loc) {
    def comment = item.comment
    if (comment) {
      comment = translator.translate(comment, loc)
    }
    def setupComment = getSetupTimeComment(item, loc)
    if (setupComment) {
      comment = comment ? "${comment}, ${setupComment}" : setupComment
    }
    return comment ?: ""
  }

  def getSetupTimeComment(item, loc) {
    def setupTime = getSetupTime(item)
    return setupTime ? getStartTimeComment(item, setupTime, loc) : null
  }

  def getStartTimeComment(item, loc) {
    return getStartTimeComment(item, getSetupTime(item), loc)
  }

  def getStartTimeComment(item, setupTime, loc) {
    def time = Time.parse(item.fromTime)
    if (setupTime) time = time.minusSeconds(setupTime)
    return localization.translate("circulation_start", loc) + ": " + PRINT_FORMATTER.print(time)
  }

  def getSetupTime(item) {
    def time = null
    if (useTechnologicalTime && item.technologicalTime) time = item.technologicalTime
    if (useSetupTime && item.setupTime) time = item.setupTime
    return time
  }

  def convertTime(time) {
    def parsed = FORMATTER.parseLocalTime(time)
    def result = PRINT_FORMATTER.print(parsed)
    return result
  }

  def createComments(tHolder) {
    def train = tHolder.train
    def symbol = "*";
    def fSymbol = DAGGER // dagger
    def list = []
    def shunt = false
    def occupied = false
    def lineEnd = false
    def cnt = 0
    for (row in train.rows) {
      def isIn = tHolder.isIn(cnt)
      if (!lineEnd && row.lineEnd && isIn) { // Delta
        list << [DELTA, localization.translate("entry_line_end", locale)]
        lineEnd = true
      }
      if (!occupied && row.occupied && isIn) { // Omicron
        list << [OMICRON, localization.translate("entry_occupied", locale)]
        occupied = true
      }
      if (!shunt && row.shunt && isIn) { // loz
        list << [LOZ ,localization.translate("entry_shunt", locale)]
        shunt = true
      }
      if (row.comment != null) {
        if (isIn) list << [symbol,translator.translate(row.comment, locale)]
        symbol += "*"
      }
      if (freight && row.freightDest != null) {
        if (isIn) list << [fSymbol, convertFreight(row.freightDest)]
        fSymbol += DAGGER
      }
      cnt++
    }
    return list
  }
  
  def convertFreight(freightDest) {
      return freightDest.collect{i -> convertFreightDest(i)}.join(', ')
  }
  
  def convertFreightDest(i) {
      def str = "${i.toString(locale, true)}${i.center ? '(' + i.regions.join(', ') + ')' : ''}"
      return i.center ? "<inline font-weight=\"bold\">${str}</inline>" : str
  }

%>
<!-- ================ Train + circulation holder ================ -->
<%
  class TrainsItemsHolder {
    def items = []
    def trains = []
    
    def static create(trains, limited) {
      def holder = new TrainsItemsHolder()
      if (!limited) {
        def ts = [:]
        // trains
        for (train in trains.trainTimetables) {
          def tHolder = TrainHolder.createFromTrain(train)
          holder.trains << tHolder
          ts[train.name] = tHolder
        }
        // cirulation items
        if (trains.cycle) {
          for (item in trains.cycle.rows) {
            def tHolder = ts[item.trainName]
            def iHolder = new CirculationItemHolder(item, tHolder)
            holder.items << iHolder
          }
        }
      } else {
        // limited
        def ts = [:]
        for (train in trains.trainTimetables) {
          ts[train.name] = train
        }
        for (item in trains.cycle.rows) {
          def tHolder = TrainHolder.createFromCirculationItem(item, ts[item.trainName])
          def iHolder = new CirculationItemHolder(item, tHolder)
          holder.items << iHolder
          holder.trains << tHolder
        }
      }
      return holder
    }
  }

  class CirculationItemHolder {
    def trainHolder
    def item
    
    CirculationItemHolder(item, train) {
      this.trainHolder = train
      this.item = item
    }
  }

  class TrainHolder {
    def train
    def start
    def end
    def refId
    
    private static counter = 0
    
    private TrainHolder(train) {
      this.train = train
      this.refId = "train${counter++}"
    }
    
    def static createFromTrain(train) {
      def holder = new TrainHolder(train)
      holder.start = 0
      holder.end = train.rows.size - 1
      return holder
    }
    
    def static createFromCirculationItem(item, train) {
      def holder = new TrainHolder(train)
      // define limits
      holder.start = getIndex(train, item.fromAbbr)
      holder.end = getIndex(train, item.toAbbr)
      return holder
    }
    
    private static getIndex(train, abbr) {
      def cnt = 0
      for (row in train.rows) {
        if (row.stationAbbr == abbr) {
            return cnt
        }
        cnt++
      }
    }
    
    def isIn(position) {
      return start <= position && position <= end
    }
    
    def isLimit(position) {
      return position == start || position == end
    }
    
    def getWhole() {
      return start == 0 && end == train.rows.size - 1
    }
  }
%>
<!-- ================ Time helpers ================ -->
<%
  class Duration {
    def total = 0

    def show(from,to) {
      if (from == null || to == null)
        return " "
      def f = Time.parse(from)
      def t = Time.parse(to)
      def period = new org.joda.time.Period(f,t)
      if (t < f) {
        period = period.plusDays(1).normalizedStandard()
      }
      double dur = period.toStandardMinutes().minutes
      dur += period.seconds / 60
      total += dur
      return Duration.show(dur)
    }

    def showTotal() {
      return Duration.show(total)
    }

    def static show(dur) {
      // convert to html
      if (dur == null || dur == 0)
        return " "
      else {
        def minutes = (int) dur
        def seconds = (int) (dur - minutes) * 10
        def str = minutes + (seconds == 0 ?  " " : "<inline vertical-align=\"text-top\" font-size=\"1.85mm\">${seconds}</inline>") // TODO super position smaller size
        return str
      }
    }
  }

  class Time {
    def hour
    def out = " "
    static org.joda.time.format.DateTimeFormatter FORMATTER = org.joda.time.format.ISODateTimeFormat.hourMinuteSecond()
    static org.joda.time.format.DateTimeFormatter PRINT_FORMATTER = new org.joda.time.format.DateTimeFormatterBuilder().appendHourOfDay(1).appendLiteral(' ').appendMinuteOfHour(2).toFormatter();

    def compute(timeStr, forceShowHour, show) {
      def parsed = parse(timeStr)
      if (parsed == null)
        out = " "
      else {
        def result
        if (parsed.hourOfDay != hour || forceShowHour)
          result = PRINT_FORMATTER.print(parsed)
        else
          result = parsed.minuteOfHour

        if (show)
          hour = parsed.hourOfDay

        if (parsed.secondOfMinute != 0) {
          def part = (int) (parsed.secondOfMinute / 60.0 * 10)
          result += "<inline vertical-align=\"text-top\" font-size=\"2mm\">${part}</inline>" // TODO super position smaller size
        } else {
          result += " "
        }
        out = show ? result : " "
      }
    }

    def static parse(str) {
      if (str == null)
        return null
      else {
        return FORMATTER.parseLocalTime(str)
      }
    }
  }
%>
