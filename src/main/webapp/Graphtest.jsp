<!-- Styles -->
<style>
#chartdiv {
	width	: 100%;
	height	: 500px;
}
										
</style>

<!-- Resources -->
<script src="https://www.amcharts.com/lib/3/amcharts.js"></script>
<script src="https://www.amcharts.com/lib/3/serial.js"></script>
<script src="https://www.amcharts.com/lib/3/plugins/export/export.min.js"></script>
<link rel="stylesheet" href="https://www.amcharts.com/lib/3/plugins/export/export.css" type="text/css" media="all" />
<script src="https://www.amcharts.com/lib/3/themes/light.js"></script>

<!-- Chart code -->
<script>
var chart = AmCharts.makeChart("chartdiv", {
    "type": "serial",
    "theme": "light",
    "marginRight": 40,
    "marginLeft": 40,
    "autoMarginOffset": 20,
    "mouseWheelZoomEnabled":true,
    "dataDateFormat": "YYYY-MM-DD",
    "valueAxes": [{
        "id": "v1",
        "axisAlpha": 0,
        "position": "left",
        "ignoreAxisWidth":true
    },{
        "id": "v3",
        "axisAlpha": 0,
        "position": "left",
        "ignoreAxisWidth":true
    },{
        "id": "v3",
        "axisAlpha": 0,
        "position": "left",
        "ignoreAxisWidth":true
    },{
        "id": "v4",
        "axisAlpha": 0,
        "position": "left",
        "ignoreAxisWidth":true
    }],
    "balloon": {
        "borderThickness": 1,
        "shadowAlpha": 0
    },
    "graphs": [{
        "id": "g1",
        "valueAxis" : "v1",
        "balloon":{
          "drop":true,
          "adjustBorderColor":false,
          "color":"#ffffff"
        },
        "bullet": "round",
        "bulletBorderAlpha": 1,
        "bulletColor": "#FFFFFF",
        "bulletSize": 5,
        "hideBulletsCount": 50,
        "lineThickness": 2,
        "title": "red line",
        "useLineColorForBulletBorder": true,
        "valueField": "value",
        "balloonText": "<span style='font-size:18px;'>[[value]]</span>"
    },{
        "id": "g1",
        "valueAxis" : "v2",
        "balloon":{
          "drop":true,
          "adjustBorderColor":false,
          "color":"#ffffff"
        },
        "bullet": "round",
        "bulletBorderAlpha": 1,
        "bulletColor": "#FFFFFF",
        "bulletSize": 5,
        "hideBulletsCount": 50,
        "lineThickness": 2,
        "title": "red line",
        "useLineColorForBulletBorder": true,
        "valueField": "value",
        "balloonText": "<span style='font-size:18px;'>[[value]]</span>"
    },{
        "id": "g1",
        "valueAxis" : "v3",
        "balloon":{
          "drop":true,
          "adjustBorderColor":false,
          "color":"#ffffff"
        },
        "bullet": "round",
        "bulletBorderAlpha": 1,
        "bulletColor": "#FFFFFF",
        "bulletSize": 5,
        "hideBulletsCount": 50,
        "lineThickness": 2,
        "title": "red line",
        "useLineColorForBulletBorder": true,
        "valueField": "value",
        "balloonText": "<span style='font-size:18px;'>[[value]]</span>"
    },{
        "id": "g1",
        "valueAxis" : "v4",
        "balloon":{
          "drop":true,
          "adjustBorderColor":false,
          "color":"#ffffff"
        },
        "bullet": "round",
        "bulletBorderAlpha": 1,
        "bulletColor": "#FFFFFF",
        "bulletSize": 5,
        "hideBulletsCount": 50,
        "lineThickness": 2,
        "title": "red line",
        "useLineColorForBulletBorder": true,
        "valueField": "value",
        "balloonText": "<span style='font-size:18px;'>[[value]]</span>"
    }],
    "chartScrollbar": {
        "graph": "g1",
        "oppositeAxis":false,
        "offset":30,
        "scrollbarHeight": 80,
        "backgroundAlpha": 0,
        "selectedBackgroundAlpha": 0.1,
        "selectedBackgroundColor": "#888888",
        "graphFillAlpha": 0,
        "graphLineAlpha": 0.5,
        "selectedGraphFillAlpha": 0,
        "selectedGraphLineAlpha": 1,
        "autoGridCount":true,
        "color":"#AAAAAA"
    },
    "chartCursor": {
        "pan": true,
        "valueLineEnabled": true,
        "valueLineBalloonEnabled": true,
        "cursorAlpha":1,
        "cursorColor":"#258cbb",
        "limitToGraph":"g1",
        "valueLineAlpha":0.2,
        "valueZoomable":true
    },
    "valueScrollbar":{
      "oppositeAxis":false,
      "offset":50,
      "scrollbarHeight":10
    },
    "categoryField": "DateTime",
    "categoryAxis": {
        "parseDates": true,
        "dashLength": 1,
        "minorGridEnabled": true
    },
    "export": {
        "enabled": true
    },
    "dataProvider": [{"_OPEN1":"","HIGH":"751.65","OPEN1":"740.5","_HIGH":"","DateTime":"2017-01-27 00:00:00.0"},
    {"_OPEN1":"","HIGH":"754.5","OPEN1":"730.25","_HIGH":"","DateTime":"2017-01-25 00:00:00.0"},
    {"_OPEN1":"","HIGH":"734.85","OPEN1":"730.8","_HIGH":"","DateTime":"2017-01-24 00:00:00.0"},
    {"_OPEN1":"","HIGH":"734.75","OPEN1":"730.9","_HIGH":"","DateTime":"2017-01-23 00:00:00.0"},
    {"_OPEN1":"","HIGH":"744.9","OPEN1":"735.35","_HIGH":"","DateTime":"2017-01-20 00:00:00.0"},
    {"_OPEN1":"","HIGH":"747.5","OPEN1":"729.95","_HIGH":"","DateTime":"2017-01-19 00:00:00.0"},
    {"_OPEN1":"","HIGH":"737.35","OPEN1":"737.35","_HIGH":"","DateTime":"2017-01-18 00:00:00.0"},
    {"_OPEN1":"","HIGH":"737.0","OPEN1":"706.25","_HIGH":"","DateTime":"2017-01-17 00:00:00.0"},
    {"_OPEN1":"","HIGH":"704.9","OPEN1":"687.8","_HIGH":"","DateTime":"2017-01-16 00:00:00.0"},
    {"_OPEN1":"","HIGH":"696.95","OPEN1":"689.0","_HIGH":"","DateTime":"2017-01-13 00:00:00.0"},
    {"_OPEN1":"","HIGH":"687.0","OPEN1":"676.3","_HIGH":"","DateTime":"2017-01-12 00:00:00.0"},
    {"_OPEN1":"","HIGH":"672.5","OPEN1":"666.0","_HIGH":"","DateTime":"2017-01-11 00:00:00.0"},
    {"_OPEN1":"","HIGH":"680.0","OPEN1":"672.9","_HIGH":"","DateTime":"2017-01-10 00:00:00.0"},
    {"_OPEN1":"","HIGH":"676.45","OPEN1":"662.0","_HIGH":"","DateTime":"2017-01-09 00:00:00.0"},
    {"_OPEN1":"","HIGH":"677.7","OPEN1":"676.0","_HIGH":"","DateTime":"2017-01-06 00:00:00.0"},
    {"_OPEN1":"","HIGH":"677.0","OPEN1":"668.0","_HIGH":"","DateTime":"2017-01-05 00:00:00.0"},
    {"_OPEN1":"","HIGH":"671.45","OPEN1":"653.5","_HIGH":"","DateTime":"2017-01-04 00:00:00.0"},
    {"_OPEN1":"","HIGH":"664.4","OPEN1":"660.5","_HIGH":"","DateTime":"2017-01-03 00:00:00.0"},
    {"_OPEN1":"","HIGH":"660.45","OPEN1":"649.3","_HIGH":"","DateTime":"2017-01-02 00:00:00.0"},
    {"_OPEN1":"","HIGH":"653.5","OPEN1":"644.8","_HIGH":"","DateTime":"2016-12-30 00:00:00.0"},
    {"_OPEN1":"","HIGH":"640.0","OPEN1":"632.1","_HIGH":"","DateTime":"2016-12-29 00:00:00.0"},
    {"_OPEN1":"","HIGH":"646.0","OPEN1":"642.05","_HIGH":"","DateTime":"2016-12-28 00:00:00.0"},
    {"_OPEN1":"","HIGH":"640.0","OPEN1":"610.0","_HIGH":"","DateTime":"2016-12-27 00:00:00.0"},
    {"_OPEN1":"","HIGH":"628.9","OPEN1":"628.9","_HIGH":"","DateTime":"2016-12-26 00:00:00.0"},
    {"_OPEN1":"","HIGH":"636.35","OPEN1":"623.0","_HIGH":"","DateTime":"2016-12-23 00:00:00.0"},
    {"_OPEN1":"","HIGH":"630.9","OPEN1":"628.1","_HIGH":"","DateTime":"2016-12-22 00:00:00.0"},
    {"_OPEN1":"","HIGH":"638.4","OPEN1":"635.0","_HIGH":"","DateTime":"2016-12-21 00:00:00.0"},
    {"_OPEN1":"","HIGH":"649.1","OPEN1":"641.0","_HIGH":"","DateTime":"2016-12-20 00:00:00.0"},
    {"_OPEN1":"","HIGH":"662.85","OPEN1":"662.85","_HIGH":"","DateTime":"2016-12-19 00:00:00.0"},
    {"_OPEN1":"","HIGH":"666.95","OPEN1":"649.7","_HIGH":"","DateTime":"2016-12-16 00:00:00.0"},
    {"_OPEN1":"","HIGH":"660.0","OPEN1":"638.0","_HIGH":"","DateTime":"2016-12-15 00:00:00.0"},
    {"_OPEN1":"","HIGH":"680.05","OPEN1":"680.0","_HIGH":"","DateTime":"2016-12-14 00:00:00.0"},
    {"_OPEN1":"","HIGH":"702.45","OPEN1":"695.0","_HIGH":"","DateTime":"2016-12-13 00:00:00.0"},
    {"_OPEN1":"","HIGH":"703.0","OPEN1":"695.0","_HIGH":"","DateTime":"2016-12-12 00:00:00.0"},
    {"_OPEN1":"","HIGH":"712.0","OPEN1":"702.5","_HIGH":"","DateTime":"2016-12-09 00:00:00.0"},
    {"_OPEN1":"","HIGH":"703.5","OPEN1":"679.45","_HIGH":"","DateTime":"2016-12-08 00:00:00.0"},
    {"_OPEN1":"","HIGH":"678.1","OPEN1":"677.2","_HIGH":"","DateTime":"2016-12-07 00:00:00.0"},
    {"_OPEN1":"","HIGH":"680.0","OPEN1":"667.3","_HIGH":"","DateTime":"2016-12-06 00:00:00.0"},
    {"_OPEN1":"","HIGH":"666.85","OPEN1":"649.0","_HIGH":"","DateTime":"2016-12-05 00:00:00.0"},
    {"_OPEN1":"","HIGH":"660.45","OPEN1":"647.8","_HIGH":"","DateTime":"2016-12-02 00:00:00.0"},
    {"_OPEN1":"","HIGH":"654.1","OPEN1":"631.0","_HIGH":"","DateTime":"2016-12-01 00:00:00.0"},
    {"_OPEN1":"","HIGH":"636.0","OPEN1":"628.0","_HIGH":"","DateTime":"2016-11-30 00:00:00.0"},
    {"_OPEN1":"","HIGH":"638.7","OPEN1":"620.2","_HIGH":"","DateTime":"2016-11-29 00:00:00.0"},
    {"_OPEN1":"","HIGH":"634.5","OPEN1":"625.1","_HIGH":"","DateTime":"2016-11-28 00:00:00.0"},
    {"_OPEN1":"","HIGH":"631.0","OPEN1":"609.35","_HIGH":"","DateTime":"2016-11-25 00:00:00.0"},
    {"_OPEN1":"","HIGH":"614.8","OPEN1":"610.9","_HIGH":"","DateTime":"2016-11-24 00:00:00.0"},
    {"_OPEN1":"","HIGH":"620.5","OPEN1":"613.9","_HIGH":"","DateTime":"2016-11-23 00:00:00.0"},
    {"_OPEN1":"","HIGH":"623.0","OPEN1":"610.15","_HIGH":"","DateTime":"2016-11-22 00:00:00.0"},
    {"_OPEN1":"","HIGH":"628.5","OPEN1":"623.5","_HIGH":"","DateTime":"2016-11-21 00:00:00.0"},
    {"_OPEN1":"","HIGH":"629.2","OPEN1":"624.9","_HIGH":"","DateTime":"2016-11-18 00:00:00.0"},
    {"_OPEN1":"","HIGH":"626.6","OPEN1":"620.7","_HIGH":"","DateTime":"2016-11-17 00:00:00.0"},
    {"_OPEN1":"","HIGH":"636.3","OPEN1":"601.05","_HIGH":"","DateTime":"2016-11-16 00:00:00.0"},
    {"_OPEN1":"","HIGH":"647.0","OPEN1":"647.0","_HIGH":"","DateTime":"2016-11-15 00:00:00.0"},
    {"_OPEN1":"","HIGH":"692.05","OPEN1":"692.0","_HIGH":"","DateTime":"2016-11-11 00:00:00.0"},
    {"_OPEN1":"","HIGH":"703.3","OPEN1":"671.7","_HIGH":"","DateTime":"2016-11-10 00:00:00.0"},
    {"_OPEN1":"","HIGH":"670.0","OPEN1":"641.5","_HIGH":"","DateTime":"2016-11-09 00:00:00.0"},
    {"_OPEN1":"","HIGH":"687.35","OPEN1":"671.0","_HIGH":"","DateTime":"2016-11-08 00:00:00.0"},
    {"_OPEN1":"","HIGH":"684.9","OPEN1":"675.0","_HIGH":"","DateTime":"2016-11-07 00:00:00.0"},
    {"_OPEN1":"","HIGH":"676.0","OPEN1":"675.0","_HIGH":"","DateTime":"2016-11-04 00:00:00.0"},
    {"_OPEN1":"","HIGH":"687.0","OPEN1":"683.95","_HIGH":"","DateTime":"2016-11-03 00:00:00.0"},
    {"_OPEN1":"","HIGH":"701.4","OPEN1":"696.0","_HIGH":"","DateTime":"2016-11-02 00:00:00.0"},
    {"_OPEN1":"","HIGH":"709.45","OPEN1":"698.9","_HIGH":"","DateTime":"2016-11-01 00:00:00.0"},
    {"_OPEN1":"","HIGH":"703.25","OPEN1":"695.0","_HIGH":"","DateTime":"2016-10-30 00:00:00.0"},
    {"_OPEN1":"","HIGH":"713.65","OPEN1":"704.1","_HIGH":"","DateTime":"2016-10-28 00:00:00.0"},
    {"_OPEN1":"","HIGH":"714.15","OPEN1":"699.0","_HIGH":"","DateTime":"2016-10-27 00:00:00.0"},
    {"_OPEN1":"","HIGH":"718.4","OPEN1":"710.55","_HIGH":"","DateTime":"2016-10-26 00:00:00.0"},
    {"_OPEN1":"","HIGH":"722.1","OPEN1":"716.6","_HIGH":"","DateTime":"2016-10-25 00:00:00.0"},
    {"_OPEN1":"","HIGH":"730.7","OPEN1":"717.1","_HIGH":"","DateTime":"2016-10-24 00:00:00.0"},
    {"_OPEN1":"","HIGH":"722.2","OPEN1":"712.0","_HIGH":"","DateTime":"2016-10-21 00:00:00.0"},
    {"_OPEN1":"","HIGH":"720.9","OPEN1":"712.0","_HIGH":"","DateTime":"2016-10-20 00:00:00.0"},
    {"_OPEN1":"","HIGH":"712.75","OPEN1":"693.0","_HIGH":"","DateTime":"2016-10-19 00:00:00.0"},
    {"_OPEN1":"","HIGH":"694.35","OPEN1":"690.0","_HIGH":"","DateTime":"2016-10-18 00:00:00.0"},
    {"_OPEN1":"","HIGH":"693.0","OPEN1":"678.8","_HIGH":"","DateTime":"2016-10-17 00:00:00.0"},
    {"_OPEN1":"","HIGH":"686.0","OPEN1":"670.0","_HIGH":"","DateTime":"2016-10-14 00:00:00.0"},
    {"_OPEN1":"","HIGH":"673.3","OPEN1":"667.9","_HIGH":"","DateTime":"2016-10-13 00:00:00.0"},
    {"_OPEN1":"","HIGH":"679.4","OPEN1":"678.3","_HIGH":"","DateTime":"2016-10-10 00:00:00.0"},
    {"_OPEN1":"","HIGH":"681.0","OPEN1":"673.5","_HIGH":"","DateTime":"2016-10-07 00:00:00.0"},
    {"_OPEN1":"","HIGH":"683.3","OPEN1":"682.0","_HIGH":"","DateTime":"2016-10-06 00:00:00.0"},
    {"_OPEN1":"","HIGH":"688.8","OPEN1":"681.0","_HIGH":"","DateTime":"2016-10-05 00:00:00.0"},
    {"_OPEN1":"","HIGH":"682.7","OPEN1":"682.7","_HIGH":"","DateTime":"2016-10-04 00:00:00.0"},
    {"_OPEN1":"","HIGH":"686.2","OPEN1":"675.9","_HIGH":"","DateTime":"2016-10-03 00:00:00.0"},
    {"_OPEN1":"","HIGH":"677.0","OPEN1":"645.0","_HIGH":"","DateTime":"2016-09-30 00:00:00.0"},
    {"_OPEN1":"","HIGH":"687.55","OPEN1":"687.45","_HIGH":"","DateTime":"2016-09-29 00:00:00.0"},
    {"_OPEN1":"","HIGH":"690.85","OPEN1":"688.0","_HIGH":"","DateTime":"2016-09-28 00:00:00.0"},
    {"_OPEN1":"","HIGH":"694.9","OPEN1":"686.7","_HIGH":"","DateTime":"2016-09-27 00:00:00.0"},
    {"_OPEN1":"","HIGH":"694.2","OPEN1":"694.2","_HIGH":"","DateTime":"2016-09-26 00:00:00.0"},
    {"_OPEN1":"","HIGH":"706.9","OPEN1":"688.0","_HIGH":"","DateTime":"2016-09-23 00:00:00.0"},
    {"_OPEN1":"","HIGH":"696.3","OPEN1":"691.0","_HIGH":"","DateTime":"2016-09-22 00:00:00.0"},
    {"_OPEN1":"","HIGH":"697.0","OPEN1":"682.4","_HIGH":"","DateTime":"2016-09-21 00:00:00.0"},
    {"_OPEN1":"","HIGH":"691.25","OPEN1":"686.0","_HIGH":"","DateTime":"2016-09-20 00:00:00.0"},
    {"_OPEN1":"","HIGH":"699.7","OPEN1":"699.7","_HIGH":"","DateTime":"2016-09-19 00:00:00.0"},
    {"_OPEN1":"","HIGH":"700.5","OPEN1":"686.55","_HIGH":"","DateTime":"2016-09-16 00:00:00.0"},
    {"_OPEN1":"","HIGH":"710.7","OPEN1":"703.1","_HIGH":"","DateTime":"2016-09-15 00:00:00.0"},
    {"_OPEN1":"","HIGH":"700.25","OPEN1":"648.35","_HIGH":"","DateTime":"2016-09-14 00:00:00.0"},
    {"_OPEN1":"","HIGH":"657.9","OPEN1":"648.0","_HIGH":"","DateTime":"2016-09-12 00:00:00.0"},
    {"_OPEN1":"","HIGH":"671.35","OPEN1":"650.0","_HIGH":"","DateTime":"2016-09-09 00:00:00.0"},
    {"_OPEN1":"","HIGH":"662.6","OPEN1":"661.0","_HIGH":"","DateTime":"2016-09-08 00:00:00.0"},
    {"_OPEN1":"","HIGH":"668.0","OPEN1":"646.6","_HIGH":"","DateTime":"2016-09-07 00:00:00.0"},
    {"_OPEN1":"","HIGH":"650.8","OPEN1":"632.0","_HIGH":"","DateTime":"2016-09-06 00:00:00.0"}]
});

chart.addListener("rendered", zoomChart);

zoomChart();

function zoomChart() {
    chart.zoomToIndexes(chart.dataProvider.length - 40, chart.dataProvider.length - 1);
}
</script>

<!-- HTML -->
<div id="chartdiv"></div>	