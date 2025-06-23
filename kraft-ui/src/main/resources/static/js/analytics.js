document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll("canvas[data-type]").forEach(canvas => {
        const type = canvas.dataset.type;
        const title = canvas.dataset.title;
//        const labels = canvas.dataset.labels;
//        const values = canvas.dataset.values;
        const labels = JSON.parse(canvas.dataset.labels);
        const values = JSON.parse(canvas.dataset.values);

       console.log("labels" + labels + " values " + values)
        const ctx = canvas.getContext("2d");

        const config = {
            type: type === "area" ? "line" : type,
            data: {
                labels,
                datasets: [{
                    label: title,
                    data: values,
                    borderColor: 'rgba(54, 162, 235, 1)',
                    backgroundColor: generateBackgroundColors(values.length, type),
                    fill: type === "area"
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    x: {
                        ticks: {
                            autoSkip: false,
                            maxRotation: 0,
                            minRotation: 0,
                            callback: function(value, index, values) {
                                const total = values.length;
                                if (total > 60) {
                                    return index % 10 === 0 ? this.getLabelForValue(value) : '';
                                } else if (total > 30) {
                                    return index % 5 === 0 ? this.getLabelForValue(value) : '';
                                } else {
                                    return this.getLabelForValue(value);
                                }
                            }
                        }
                    },
                    y: {
                        beginAtZero: true
                    }
                },
                plugins: {
                    legend: { display: type !== "bar" }
                }
            }

        };

        new Chart(ctx, config);
    });

    function generateBackgroundColors(count, type) {
        const palette = [
            '#007bff', '#28a745', '#ffc107', '#dc3545', '#6f42c1', '#FF6384', '#36A2EB'
        ];
        return type === "pie" || type === "doughnut"
            ? palette.slice(0, count)
            : 'rgba(54, 162, 235, 0.6)';
    }
});

