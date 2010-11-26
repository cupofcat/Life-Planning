Highcharts.setOptions({
   colors: ["#DDDF0D", "#7798BF", "#55BF3B", "#DF5353", "#aaeeee", "#ff0066", "#eeaaee",
	  "#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
   chart: {
	  /*backgroundColor: {
		 linearGradient: [0, 0, 0, 400],
		 stops: [
			[0, 'rgba(255, 96, 96, 0)'],
			[1, 'rgba(255, 255, 16, 0)']
		 ]
		 //opacity: 0,
	  },*/
	  //background: transparent,
	  backgroundColor: 'rgba(0, 0, 0, 0)',
	  borderWidth: 0,
	  borderRadius: 15,
	  plotBackgroundColor: null,
	  plotShadow: true,
	  plotBorderWidth: 0
   },
   title: {
	  style: {
		 color: '#FFF',
		 font: '16px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
	  }
   },
   subtitle: {
	  style: {
		 color: '#DDD',
		 font: '12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
	  }
   },
   xAxis: {
	  gridLineWidth: 0,
	  lineColor: '#999',
	  tickColor: '#999',
	  labels: {
		 style: {
			color: '#999',
			fontWeight: 'bold'
		 }
	  },
	  title: {
		 style: {
			color: '#AAA',
			font: 'bold 12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
		 }            
	  }
   },
   yAxis: {
	  alternateGridColor: null,
	  minorTickInterval: null,
	  gridLineColor: 'rgba(255, 255, 255, .1)',
	  lineWidth: 0,
	  tickWidth: 0,
	  labels: {
		 style: {
			color: '#999',
			fontWeight: 'bold'
		 }
	  },
	  title: {
		 style: {
			color: '#AAA',
			font: 'bold 12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
		 }            
	  }
   },
   legend: {
	  itemStyle: {
		 color: '#CCC'
	  },
	  itemHoverStyle: {
		 color: '#FFF'
	  },
	  itemHiddenStyle: {
		 color: '#333'
	  }
   },
   credits: {
	  style: {
		 right: '50px'
	  }
   },
   labels: {
	  style: {
		 color: '#CCC'
	  }
   },
   tooltip: {
	  backgroundColor: {
		 linearGradient: [0, 0, 0, 50],
		 stops: [
			[0, 'rgba(96, 96, 96, .8)'],
			[1, 'rgba(16, 16, 16, .8)']
		 ]
	  },
	  borderWidth: 0,
	  style: {
		 color: '#FFF'
	  }
   },
   
   
   plotOptions: {
	  line: {
		 dataLabels: {
			color: '#CCC'
		 },
		 marker: {
			lineColor: '#333'
		 }
	  },
	  spline: {
		 marker: {
			lineColor: '#333'
		 }
	  },
	  scatter: {
		 marker: {
			lineColor: '#333'
		 }
	  }
   },
   
   toolbar: {
	  itemStyle: {
		 color: '#CCC'
	  }
   }
});