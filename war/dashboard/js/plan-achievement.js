var serverDataPlan = {
			allSpheres: []
			// other data might be placed in this container
			
		};
		
		var planAchievementGraph;
		var planAchievementOptions;
		
								
		// gets data from the server
		$.getJSON("plan-achievement", parametersForServlet, function(data){
			
			 // Store data from server for future (when another sphere is chosen, no get method is required)
			serverDataPlan.allSpheres = data.spheres;
			
			//alert(serverDataPlan.allSpheres[0].series[0].name),
					
			// Prepare settings of a chart
			planAchievementOptions = {
				chart: {
					renderTo: 'plan-achievement', 
					defaultSeriesType: 'line'
				},
				title: {
					text: 'Planned vs. achieved sphere realisation'
				},
				subtitle: {
					text: ''
				},
				xAxis: {
					type: 'datetime'
				},
				yAxis: {
					title: null,
					labels: {
						formatter: function() {
							return Highcharts.numberFormat(this.value*100, 0) +' %'; 
						}
					}
				},
				tooltip: {
					formatter: function() {
						return '<b>'+ this.series.name  +'</b><br/>' +
							'Year: ' + Highcharts.dateFormat('%Y', this.x) + ', week: ' + getWeek(this.x, 4) + ':<br/>'+
							''+ Highcharts.numberFormat(this.y*100, 0) +' %';
					}
				},
				plotOptions: {
					line: {
						lineWidth: 3,
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
				series: serverDataPlan.allSpheres[0].series
			};
			
			
			// Produce sphere buttons
			for(i = 0; i<serverDataPlan.allSpheres.length; i++)
			{
				sphere = serverDataPlan.allSpheres[i].sphereName;
				
				$('#sphereButtonsHolder').append('<button id="' + sphere + '" class="sphereButton">' + sphere + '</button>');
			}
			
			// Declare on-click function
			function changeSphere(sphereName) {
				for(i=0; i<serverDataPlan.allSpheres.length; i++)
				{
					if(serverDataPlan.allSpheres[i].sphereName == sphereName)
					{
						planAchievementOptions.series = serverDataPlan.allSpheres[i].series;
						break;
					}
				}
				planAchievementGraph = new Highcharts.Chart(planAchievementOptions);
			};
			
			// Assign on-click function to buttons responsible for changing spheres
			$(function() {
				$( ".sphereButton").click(function(){
					changeSphere(this.id);
				});
			}); 
				
			// Produce chart
			planAchievementGraph = new Highcharts.Chart(planAchievementOptions);
				
		});