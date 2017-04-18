Chart.defaults.global.animation.duration = 0;
var ctx = [];
var data = [];
var myLineChart = [];

var hz = Horizon();

var labels = [];
var points = [];

hz.connect();

var id = getParameterByName("account");
initCharts(id);


function getParameterByName(name, url) {
    if (!url) {
        url = window.location.href;
    }
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function initCharts(id) {
    hz('points').findAll({ accountId: id }).limit(300).fetch().subscribe(initDocs => {
        var noUniqueLabels = initDocs.map(d => d.label);
        var uniqueLabels = noUniqueLabels.filter(function (item, pos) {
            return noUniqueLabels.indexOf(item) == pos;
        });
        console.log("Labels: " + uniqueLabels);

        uniqueLabels.forEach(function (element) {

            addChart(element);
            console.log(element);
        });

    });
}

function addChart(chart) {
    var div = document.createElement('div');

    div.innerHTML = '<div class="col-md-6">\
                    <div class="panel panel-default">\
                        <div class="panel-heading">\
                            <i class="fa fa-bar-chart-o fa-fw"></i> ' + chart + '\
                        </div>\
                        <div class="panel-body">\
                            <div>\
                                <canvas id="' + chart.trim() + '" width="400" height="400"></canvas>\
                            </div>\
                        </div>\
                    </div>\
                </div>';

    document.getElementById('charts').appendChild(div);    


    ctx[chart] = document.getElementById(chart).getContext("2d");

    data[chart] = {
        labels: labels[chart],
        datasets: [
            {
                label: chart,
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
                data: points[chart],
                spanGaps: false,
            }
        ]
    };

    myLineChart[chart] = new Chart(ctx[chart], {
        type: 'line',
        data: data[chart],
        options: {
        }
    });



hz('points').findAll({ accountId: id, label: chart.trim() }).watch().subscribe((docs) => {

        //labels.push(docs.date);
        console.log(docs)

        labels[chart] = docs.map(d => d.epoch).slice(-100);
        points[chart] = docs.map(d => d.value).slice(-100);


        myLineChart[chart].data.labels = labels[chart];
        myLineChart[chart].data.datasets[0].data = points[chart];
        myLineChart[chart].update();
    });

}