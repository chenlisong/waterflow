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
  <script>
    const container = document.getElementById('app');
    const data = ${data};
    const config = {
      "title": {
        "visible": true,
        "text": "多折线图"
      },
      "description": {
        "visible": true,
        "text": "一个简单的多折线图"
      },
      "legend": {
        "flipPage": false
      },
      "forceFit": false,
      "width": 900,
      "height": 600,
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
