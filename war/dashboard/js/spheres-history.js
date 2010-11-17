var serverDataHistory = {
	masterCharts: [],
	// other data might be placed in this container
	
};

var masterChart,
	detailChart;
			

$.getJSON("spheres-history", function(data){
	
	seriesFromServer = data.series;
				
	for(i=0; i<seriesFromServer.length; i++)
	{
		tempSeries = {
				type: seriesFromServer[i].type,
				name: seriesFromServer[i].name,
				pointInterval: 24 * 3600 * 1000,
				pointStart: 1136073600000, //Date.UTC(2006, 0, 01),
				data: seriesFromServer[i].data
			};
			
			serverDataHistory.masterCharts.push(tempSeries);
	}
	
					
	// make the container smaller and add a second container for the master chart
	var $historicChart = $('#historicChart')
		.css('position', 'relative');
	
	var $detailContainer = $('<div id="detail-container">')
		.appendTo($historicChart);
	
	var $masterContainer = $('<div id="master-container">')
		.css({ position: 'absolute', top: 300, height: 80, width: 800 })
		.appendTo($historicChart);
				
			
	// create the master chart
	masterChart = new Highcharts.Chart({
		chart: {
			renderTo: 'master-container',
			borderWidth: 0,
			backgroundColor: null,
			zoomType: 'x',
			marginTop: 0,
			marginBottom: 20,
			events: {
				
				// listen to the selection event on the master chart to update the 
				// extremes of the detail chart
				selection: function(event) {
					var extremesObject = event.xAxis[0],
						min = extremesObject.min,
						max = extremesObject.max,
						xAxis = this.xAxis[0];
						
					for(j=0; j<masterChart.series.length; j++)
					{
						detailData = [];
						
						// reverse engineer the last part of the data
						jQuery.each(this.series[j].data, function(i, point) {
							if (point.x > min && point.x < max) {
								detailData.push({
									x: point.x,
									y: point.y
								});
							}
						});
						
						detailChart.series[j].setData(detailData);
					}
					
					
													
					// move the plot bands to reflect the new detail span
					// (darker areas on the master chart)
					xAxis.removePlotBand('mask-before');
					xAxis.addPlotBand({
						id: 'mask-before',
						from: Date.UTC(2000, 0, 1),
						to: min,
						color: 'rgba(0, 0, 0, 0.2)'
					});
					
					xAxis.removePlotBand('mask-after');
					xAxis.addPlotBand({
						id: 'mask-after',
						from: max,
						to: Date.UTC(2030, 11, 31),
						color: 'rgba(0, 0, 0, 0.2)'
					});
					
					return false;
				}
			}
		},
		title: {
			text: 'Mark period to zoom in'
		},
		xAxis: {
			type: 'datetime',
			showLastTickLabel: true,
			maxZoom: 7 * 24 * 3600000, // seven days
			minPadding: 0.01,
			maxPadding: 0.01,
			// set equal to the initial zoom on the detail chart
			plotBands: [{
				id: 'mask-before',
				from: Date.UTC(2006, 0, 1),
				to: Date.UTC(2008, 7, 1),
				color: 'rgba(0, 0, 0, 0.2)'
			}],
			title: {
				text: null
			}
		},
		yAxis: {
			gridLineWidth: 0,
			labels: {
				enabled: false
			},
			title: {
				text: null
			},
			min: 0.6,
			showFirstLabel: false
		},
		tooltip: {
			formatter: function() {
				return false;
			}
		},
		legend: {
			enabled: false
		},
		credits: {
			enabled: false
		},
		plotOptions: {
			series: {
				fillColor: {
					linearGradient: [0, 0, 0, 70],
					stops: [
						[0, '#4572A7'],
						[1, 'rgba(0,0,0,0)']
					]
				},
				lineWidth: 1,
				marker: {
					enabled: false
				},
				shadow: false,
				states: {
					hover: {
						lineWidth: 1						
					}
				},
				enableMouseTracking: false
			},
			area: {
				stacking: 'percent',
				lineColor: '#ffffff',
				lineWidth: 1,
				marker: {
					lineWidth: 1,
					lineColor: '#ffffff'
				}
			}
		},
	
		series: serverDataHistory.masterCharts,  // tu puste?
		
		exporting: {
			enabled: false
		}
	}); 
	
	
	var	detailStart = Date.UTC(2008, 7, 1);
	
	emptySeries = {
			type: 'area',
			pointStart: detailStart,
			data: []
		};
		
		
	// declares template data series for the detail graph
	templateSeries = [];
	for(i=0; i<serverDataHistory.masterCharts.length; i++)
	{
		templateSeries.push(emptySeries);
	}
	
	
	// create a detail chart referenced by a global variable
	detailChart = new Highcharts.Chart({
		chart: {
			marginBottom: 120,
			marginRight: 160,
			renderTo: 'detail-container',
			style: {
				position: 'absolute'
			}
		},
		credits: {
			enabled: false
		},
		title: {
			text: 'Historical USD to EUR Exchange Rate'
		},
		subtitle: {
			text: 'Select an area by dragging across the lower chart'
		},
		xAxis: {
			type: 'datetime'
		},
		yAxis: {
			title: null,
			maxZoom: 0.1
		},
		tooltip: {
			formatter: function() {
				return '<b>'+ (this.point.name || this.series.name) +'</b><br/>'+
					Highcharts.dateFormat('%A %B %e %Y', this.x) + ':<br/>'+
					'1 USD = '+ Highcharts.numberFormat(this.y, 2) +' EUR';
			}
		},
		legend: {
			layout: 'vertical',
			style: {
				left: 'auto',
				bottom: 'auto',
				right: '50px',
				top: '100px'
			}
		},
		plotOptions: {
			series: {
				marker: {
					enabled: false,
					states: {
						hover: {
							enabled: true,
							radius: 3
						}
					}
				}
			},
			area: {
				stacking: 'percent',
				lineColor: '#ffffff',
				lineWidth: 1,
				marker: {
					lineWidth: 1,
					lineColor: '#ffffff'
				}
			}
		},
		series: templateSeries,
		
		exporting: {
			enabled: false
		}

	});
	
	
	// copies data from masterChart to initial detailChart
	for(j=0; j<masterChart.series.length; j++)
	{
		detailChart.series[j].name = serverDataHistory.masterCharts[j].name;
		detailChart.series[j].pointInterval= 24 * 3600 * 1000; // one day?
		
		var detailData = [];
		jQuery.each(masterChart.series[j].data, function(i, point) {
		if (point.x >= detailStart) {
			detailData.push(point.y);
		}
		});
		detailChart.series[j].setData(detailData, false);
	}
	
	detailChart.redraw();
});