var serverDataPlan = {
			masterCharts: [],
			// other data might be placed in this container
			
		};
		
		var planAchievement;
		
								
		// gets data from the server
		$.getJSON("plan-achievement", sphereOfInterest, function(data){
			
			seriesFromServer = data.series;
			
			for(i=0; i<seriesFromServer.length; i++)
			{
				currentSeries = {
						name: seriesFromServer[i].name,
						data: seriesFromServer[i].data
					};
					
				serverDataPlan.masterCharts.push(currentSeries);
			}
			
			planAchievement = new Highcharts.Chart({
					chart: {
						renderTo: 'plan-achievement', 
						defaultSeriesType: 'area'
					},
					title: {
						text: 'US and USSR nuclear stockpiles'
					},
					subtitle: {
						text: 'Source: <a href="http://thebulletin.metapress.com/content/c4120650912x74k7/fulltext.pdf">'+
							'thebulletin.metapress.com</a>'
					},
					xAxis: {
					},
					yAxis: {
						title: {
							text: 'Nuclear weapon states'
						},
						labels: {
							formatter: function() {
								return this.value / 1000 +'k';
							}
						}
					},
					tooltip: {
						formatter: function() {
							return this.series.name +' produced <b>'+
								Highcharts.numberFormat(this.y, 0, null, ' ') +'</b><br/>warheads in '+ this.x;
						}
					},
					plotOptions: {
						area: {
							pointStart: 1940,
							marker: {
								enabled: false,
								symbol: 'circle',
								radius: 2,
								states: {
									hover: {
										enabled: true
									}
								}
							}
						}
					},
					legend: {
						enabled: true
					},
					credits: {
						enabled: false
					},
					series: serverDataPlan.masterCharts
				});
		});