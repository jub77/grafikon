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
    </style>
</head>
<body>
<div class="caption1">${title}</div>

<div class="caption2">${title_engines}</div>
<table class="list" border="0" cellspacing="0" cellpadding="0">
<%  for (engine in engines) { %>
    <tr>
        <td>${engine.cycleName}</td>
        <td>${engine.cycleDescription}</td>
        <td>${engine.stationName}</td>
        <td>${engine.trainName}</td>
    </tr>
<% } %>
</table>

<div class="caption2">${title_train_units}</div>
<table class="list" border="0" cellspacing="0" cellpadding="0">
<%  for (train_unit in train_units) { %>
    <tr>
        <td>${train_unit.cycleName}</td>
        <td>${train_unit.cycleDescription}</td>
        <td>${train_unit.stationName}</td>
        <td>${train_unit.trainName}</td>
    </tr>
<% } %>
</table>
</body>
</html>
