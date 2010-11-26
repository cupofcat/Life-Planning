var serverDataPlan = {
			allSpheres: []
			// other data might be placed in this container
			
		};
		
		var planAchievementGraph;
		var planAchievementOptions;
		
								
		// gets data from the server
		$.getJSON("plan-achievement", /*sphereOfInterest,*/ function(data){
			
			 // Store data from server for future (when another sphere is chosen, no get method is required)
			serverDataPlan.allSpheres = data.spheres;
					
			// Prepare settings of a chart
			planAchievementOptions = {
				chart: {
					renderTo: 'plan-achievement', 
					defaultSeriesType: 'line'
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
					},
					line: {
						lineWidth: 4,
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
				series: serverDataPlan.allSpheres[0].series
			};
			
			
			// Produce sphere buttons
			for(i = 0; i<serverDataPlan.allSpheres.length; i++)
			{
				sphere = serverDataPlan.allSpheres[i].sphere;
				
				$('#sphereButtonsHolder').append('<button id="' + sphere + '" class="sphereButton">' + sphere + '</button>');
			}
			
			// Declare on-click function
			function changeSphere(sphereName) {
				for(i=0; i<serverDataPlan.allSpheres.length; i++)
				{
					if(serverDataPlan.allSpheres[i].sphere == sphereName)
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