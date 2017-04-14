Chart.defaults.global.animation.duration = 0;
var ctx = document.getElementById("rpmChart").getContext("2d");

var data = {
    labels: labels,
    datasets: [
        {
            label: "RPM",
            fill: false,
            lineTension: 0.1,
            backgroundColor: "rgba(75,192,192,0.4)",
            borderColor: "rgba(75,192,192,1)",
            borderCapStyle: 'butt',
            borderDash: [],
            borderDashOffset: 0.0,
            borderJoinStyle: 'miter',
            pointBorderColor: "rgba(75,192,192,1)",
            pointBackgroundColor: "#fff",
            pointBorderWidth: 1,
            pointHoverRadius: 5,
            pointHoverBackgroundColor: "rgba(75,192,192,1)",
            pointHoverBorderColor: "rgba(220,220,220,1)",
            pointHoverBorderWidth: 2,
            pointRadius: 1,
            pointHitRadius: 10,
            data: points,
            spanGaps: false,
        }
    ]
};

var myLineChart = new Chart(ctx, {
    type: 'line',
    data: data,
    options: {
    }
});


var hz = Horizon();

var labels = [];
var points = [];

hz.connect();
hz('points').watch().subscribe((docs) => {

    //labels.push(docs.date);

    labels = docs.map(d => +d.date).slice(-100);
    points = docs.map(d => d.rpm).slice(-100);

    console.log(labels);

    console.log(docs)

    myLineChart.data.labels = labels;
    myLineChart.data.datasets[0].data = points;
    myLineChart.update();
});