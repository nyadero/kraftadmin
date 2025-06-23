//
// let stompClient = null;
////        const socket = new SockJS('/ws'); // should match your server's endpoint
////        const stompClient = Stomp.over(socket);
//
//    function connectWS() {
//        const socket = new SockJS("/ws");
//        stompClient = Stomp.over(socket);
//
////        stompClient.connect({}, function (frame) {
////            console.log("Connected: " + frame);
////
////            // Subscribe to all analytics topics dynamically (one per entity type)
////            let entityTypes = /*[[${analyticsComponents.stream().map(a -> a.type).collect(Collectors.toList())}]]*/ [];
////
////            entityTypes.forEach(type => {
////                stompClient.subscribe("/topic/analytics/" + type, function (message) {
////                    let data = JSON.parse(message.body);
////                    updateChart(type, data);
////                });
////            });
////        });
//
//         stompClient.connect({}, function (frame) {
//            console.log("Connected: " + frame);
//            const wsData = document.querySelector(".ws-data");
//
//            stompClient.subscribe('/topic/analytics/', function (message) {
//                const payload = JSON.parse(message.body);
//                ws.innerText = payload;
//
//                // Make sure this chart is the right one for this payload
//                if (payload.title && payload.title.replaceAll(' ', '') === chartId) {
//                    const newLabels = payload.labels || [];
//                    const newValues = payload.values || [];
//
//                    chart.data.labels = newLabels;
//                    chart.data.datasets[0].data = newValues;
//                    chart.update();
//                }
//            });
//        });
//    }
//
//    // Setup WebSocket connection
//
//
//
//
//    function updateChart(type, data) {
//        let chartId = `${type}-chart`;
//        let canvas = document.getElementById(chartId);
//
//        if (!canvas) {
//            const container = document.getElementById('charts-container');
//            const div = document.createElement("div");
//            div.innerHTML = `
//                <h4>${type}</h4>
//                <canvas id="${chartId}" width="400" height="200"></canvas>
//            `;
//            container.appendChild(div);
//            canvas = document.getElementById(chartId);
//        }
//
//        const ctx = canvas.getContext("2d");
//        new Chart(ctx, {
//            type: 'bar',
//            data: {
//                labels: data.labels,
//                datasets: [{
//                    label: data.title,
//                    data: data.values,
//                    backgroundColor: 'rgba(54, 162, 235, 0.6)'
//                }]
//            }
//        });
//    }
//
//    connectWS();

document.addEventListener('DOMContentLoaded', () => {
    const socket = new SockJS('/ws'); // Make sure this matches your backend endpoint
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);

        const wsData = document.querySelector(".ws-data"); // This should exist in your HTML

        stompClient.subscribe('/topic/analytics/', function (message) {
            const payload = JSON.parse(message.body);

            // Optional: nicely format JSON
            wsData.innerText = JSON.stringify(payload, null, 2);

            // Or if it's simple text:
             wsData.innerText = payload.someField;
        });
    });
});
