import Chart from 'chart.js/auto';

document.addEventListener("DOMContentLoaded", function() {
    console.log('Chart.js loaded:', Chart);

    // Find all canvas elements with the chart-canvas class
    const canvases = document.querySelectorAll('.chart-canvas');
    console.log('Found canvases:', canvases.length);

     // Debug: Log all canvas IDs to see what we're finding
       canvases.forEach((canvas, index) => {
            console.log(`Canvas ${index}:`, canvas.id, canvas);
        });

            const processedIds = new Set();

    canvases.forEach((canvas, index) => {
    console.log(`Canvas ${index}:`, canvas.id, canvas);

                // Get data from Thymeleaf attributes
                const labels = JSON.parse(canvas.dataset.labels || '["No Data"]');
                const values = JSON.parse(canvas.dataset.values || '[0]');
                const title = canvas.dataset.title || 'Chart';

        const ctx = canvas.getContext('2d');
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: title,
                    data: values,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 205, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)',
                        'rgba(255, 159, 64, 0.2)'
                    ],
                    borderColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 205, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)',
                        'rgba(255, 159, 64, 1)'
                    ],
                    borderWidth: 1,
                     borderWidth: 2,
                                        fill: true,
                                        tension: 0.4, // Smooth curves
                                        pointRadius: 0, // Hide individual points for cleaner look
                                        pointHoverRadius: 4
                }]
            },
//            options: {
//                scales: {
//                    y: {
//                        beginAtZero: true
//                    }
//                }
//            }
 options: {
                responsive: false,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    x: {
                        display: true,
                        ticks: {
                            maxTicksLimit: 24, // Show only 6 labels max
                            font: {
                                size: 10
                            }
                        },
                        grid: {
                            display: true
                        }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: {
                            font: {
                                size: 10
                            }
                        },
                        grid: {
                            color: 'rgba(0,0,0,0.1)'
                        }
                    }
                },
                interaction: {
                    intersect: false,
                    mode: 'index'
                }
                }
        });
    });
});
