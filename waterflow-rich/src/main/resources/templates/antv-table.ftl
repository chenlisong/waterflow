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
  <br/>
  ${url}
  <script>
      const container = document.getElementById('app');
      const data = ${data};
      const config = {
        "title": {
          "visible": true,
          "text": "分组条形图"
        },
        "description": {
          "visible": true,
          "text": "一个简单的分组条形图"
        },
        "legend": {
          "position": "top-left",
          "flipPage": false
        },
        "xAxis": {
          "title": {
            "visible": false
          }
        },
        "forceFit": false,
        "width": 580,
        "height": 776,
        "xField": "y",
        "yField": "x",
        "groupField": "type",
        "barSize": null,
        "color": [
          "#5B8FF9",
          "#5AD8A6",
          "#5d7092"
        ]
      }
    const plot = new g2plot.GroupBar(container, {
      data,
      ...config,
    });
    plot.render();
  </script>
  </body>
</html>
