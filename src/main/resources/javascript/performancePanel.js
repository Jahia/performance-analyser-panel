var myChart;

function change(newType, url) {
    var ctx = document.getElementById("myChart").getContext("2d");

    // Remove the old chart and all its event handles
    if (myChart) {
        myChart.destroy();
    }

    var numberOfElements = $("#numberOfElement").val();

    if(newType == "pie"){
        options_chart = {
            legend: {
                display: true,
                position:'right',
                fullWidth: false
            },
            title: {
                display: false,
                text: numberOfElements + " Slowest Loading Element"
            }
        };
    }else{
        options_chart = {
            legend: {
                display: false
            },
            title: {
                display: false,
                text: numberOfElements + " Slowest Loading Element"
            }
        };
    }

    $.ajax({
        url: url+".getElementForGraph.do",
        context: document.body,
        dataType: "json",
        data: {
            numberOfElement: numberOfElements
        },
    }).done(function(data) {
        chartData = {
            labels: data["keys"],
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
}