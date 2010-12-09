var priorities;

		
$.getJSON("chosen-priorities", parametersForServlet, function(data){


	priorities = new Highcharts.Chart({
		chart: {
			renderTo: 'priorities-chart',
			margin: [50, 200, 60, 170]
		},
		title: {
			text: 'Life Spheres Priority Assignment'
		},
		plotArea: {
			shadow: null,
			borderWidth: null,
			backgroundColor: null
		},
		tooltip: {
			formatter: function() {
				return '<b>'+ this.point.name +'</b>: '+ 100*this.y +' %';
			}
		},
		// hides the "highchats.com" credits
		credits: {
			enabled: false
		},
		plotOptions: {
			pie: {
				allowPointSelect: true,
				cursor: 'pointer',
				dataLabels: {
					enabled: true,
					formatter: function() {
						if (this.y > 5) return this.point.name;
					},
					color: 'black',
					style: {
						font: '13px Trebuchet MS, Verdana, sans-serif'
					}
				}
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
		series: [{
			type: 'pie',
			name: 'Spheres',
			data: data 
		}]
	});
});	