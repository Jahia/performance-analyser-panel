var myChart;

function change(newType, url) {
    var ctx = document.getElementById("myChart").getContext("2d");
    var uuidTable;

    // Remove the old chart and all its event handles
    if (myChart) {
        myChart.destroy();
    }

    var numberOfElements = $("#numberOfElement").val();


    options_chart = {
        legend: {
            display: true,
            position: 'right',
            fullWidth: false
        },
        title: {
            display: false,
            text: numberOfElements + " Slowest Loading Element"
        },
        tooltips: {
            enabled: false,
            //Set the name of the custom function here
            custom: customTooltips,
            callbacks: {
                title: function(item, data) {
                    // Pick first xLabel for now
                    var title = '';

                    if (item.length > 0) {
                        if (item[0].yLabel) {
                            title = item[0].yLabel;
                        } else if (data.labels.length > 0 && item[0].index < data.labels.length) {
                            title = data.labels[item[0].index];
                        }
                    }

                    return title;
                },
                label: function (tooltipItem,data) {
                    var UUID = uuidTable[tooltipItem.index];
                    var value =  data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
                    return "Time : "+ value+
                         " ms" + "</br>  UUID : " + UUID;
                }
            }
        }
    }

    if(newType != "pie") {
        options_chart.legend.display = false;
    }

    $.ajax({
        url: url+".getElementForGraph.do",
        context: document.body,
        dataType: "json",
        data: {
            numberOfElement: numberOfElements
        },
    }).done(function(data) {
        uuidTable = data["keys"];
        chartData = {
            labels: data["displayName"],
            datasets: [
                {
                    backgroundColor: ["#1abc9c","#2ecc71","#3498db","#9b59b6","#34495e","#f1c40f","#e67e22","#e74c3c","#ecf0f1","#95a5a6","#16a085","#27ae60","#2980b9","#8e44ad","#2c3e50","#f39c12","#d35400","#c0392b","#bdc3c7","#7f8c8d"],
                    data: data["data"]
                }
            ]
        };
        myChart = new Chart(ctx,{
            type: newType,
            data: chartData,
            options:options_chart
        });
    });
    // Chart.js modifies the object you pass in. Pass a copy of the object so we can use the original object later
    var temp = jQuery.extend(true, {}, config);
    temp.type = newType;
    myChart = new Chart(ctx, temp);

    //Change title $("#" + targetId + "").val(node.href);
    document.getElementById("titleChart").innerHTML=""+numberOfElements+" Slowest Elements load";
};


function runPerformancePanel(path, sitePath, flush){
    $('body').append('<div style="" id="loadingDiv"><div class="loader">Loading...</div></div>');
    if(path == sitePath){
        alert("Please select a page");
        removeLoader();
        return;
    }else{
        $.ajax({
            url:path +".perfCacheFlush.do",
            context: document.body,
            data: {
                path: path,
                flush: flush
            },
            success: function(){
                $.ajax({ url: path + ".html?perfAnalyse",
                    context: document.body,
                    success: function(){
                        setTimeout(location.reload(), 7000);

                    }
                });
            }
        })
    }
}

function removeLoader(){
    $( "#loadingDiv" ).fadeOut(500, function() {
        // fadeOut complete. Remove the loading div
        $( "#loadingDiv" ).remove(); //makes page more lightweight
    });
};

var customTooltips = function(tooltip) {
    // Tooltip Element
    var tooltipEl = document.getElementById('chartjs-tooltip');
    if (!tooltipEl) {
        tooltipEl = document.createElement('div');
        tooltipEl.id = 'chartjs-tooltip';
        tooltipEl.innerHTML = "<table></table>"
        document.body.appendChild(tooltipEl);
    }
    // Hide if no tooltip
    if (tooltip.opacity === 0) {
       // tooltipEl.style.opacity = 0;
        return;
    }
    // Set caret Position
    tooltipEl.classList.remove('above', 'below', 'no-transform');
    if (tooltip.yAlign) {
        tooltipEl.classList.add(tooltip.yAlign);
    } else {
        tooltipEl.classList.add('above');
    }
    function getBody(bodyItem) {
        return bodyItem.lines;
    }
    // Set Text
    if (tooltip.body) {
        var titleLines = tooltip.title || [];
        var bodyLines = tooltip.body.map(getBody);
        //PUT CUSTOM HTML TOOLTIP CONTENT HERE (innerHTML)
        var innerHtml = '';
        titleLines.forEach(function(title) {
            innerHtml += '<h1>&bull; ' + title + ' &bull;</h1>';
        });
        bodyLines.forEach(function(body, i) {
            var colors = tooltip.labelColors[i];
            var style = 'background:' + colors.backgroundColor;
            style += '; border-color:' + colors.borderColor;
            style += '; border-width: 2px';
            var span = '<span class="chartjs-tooltip-key" style="' + style + '"></span>';
            innerHtml += '<span>' + span + body + '</span>';
        });
        // var tableRoot = tooltipEl.querySelector('table');
        tooltipEl.innerHTML = innerHtml;
    }
    var position = this._chart.canvas.getBoundingClientRect();
    // Display, position, and set styles for font
    tooltipEl.style.opacity = 1;
};