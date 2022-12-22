<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta http-equiv="X-UA-Compatible" content="ie=edge" />
    <title>ChartCube</title>
    <script src="https://gw.alipayobjects.com/os/lib/antv/g2plot/0.11.3/dist/g2plot.js"></script>
  </head>
  <body>
  <div id="app"></div>

  <div id="app2">${content}</div>
  <script>
    const container = document.getElementById('app');
    const data = ${data};
    const config = {
      "title": {
        "visible": true,
        "text": "基金-3月/12月标准差曲线"
      },
      "description": {
        "visible": true,
        "text": "${fundName}"
      },
      "legend": {
        "flipPage": false
      },
      "forceFit": false,
      "width": 1800,
      "height": 700,
      "smooth": true,
      "xField": "x",
      "legend": {
        "flipPage": false
      },
      "xAxis": {
        "autoHideLabel": true
      },
      "yAxis": {
        "autoHideLabel": true
      },
      "smooth": true,
      "forceFit": false,
      "yField": "y",
      "seriesField": "series"
      // ,
      // "color": [
      //   "#5B8FF9",
      //   "#5AD8A6"
      // ]
    }
    const plot = new g2plot.Line(container, {
      data,
      ...config,
    });
    plot.render();

  </script>
  </body>
</html>
