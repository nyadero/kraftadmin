//import Chart from 'chart.js/auto';
//import { ChoroplethController, GeoFeature, ProjectionScale } from 'chartjs-chart-geo'; // ✅ Add ProjectionScale
//import * as topojson from 'topojson-client';
//import * as world from 'world-atlas/countries-110m.json';
//
//// ✅ Register required components
//Chart.register(ChoroplethController, GeoFeature, ProjectionScale);
//
//// Convert TopoJSON to GeoJSON
//const features = topojson.feature(world, world.objects.countries).features;
//
//// Create the chart
//const ctx = document.getElementById('heatmap').getContext('2d');
//new Chart(ctx, {
//    type: 'choropleth',
//    data: {
//        labels: features.map(f => f.properties.name),
//        datasets: [{
//            label: 'World Map',
//            data: features.map(f => ({
//                feature: f,
//                value: 0
//            }))
//        }]
//    },
//    options: {
//        showOutline: true,
//        showGraticule: true,
//        scales: {
//            xy: {
//                projection: 'equalEarth' // ✅ Now it will work
//            }
//        },
//        plugins: {
//            legend: {
//                display: false
//            }
//        }
//    }
//});
 import Chart from 'chart.js/auto';
    import { ChoroplethController, GeoFeature, ProjectionScale, ColorScale } from 'chartjs-chart-geo';
    import * as topojson from 'topojson-client';
    import * as world from 'world-atlas/countries-110m.json';

document.addEventListener('DOMContentLoaded', function () {


    Chart.register(ChoroplethController, GeoFeature, ProjectionScale, ColorScale);

    const features = topojson.feature(world, world.objects.countries).features;
    const ctx = document.getElementById('heatmap')?.getContext('2d');

    if (!ctx) {
        console.error("Canvas #heatmap not found or not ready");
        return;
    }

    new Chart(ctx, {
        type: 'choropleth',
        data: {
            labels: features.map(f => f.properties.name),
            datasets: [{
                label: 'World Map',
                data: features.map(f => ({
                    feature: f,
                    value: 0
                }))
            }]
        },
        options: {
            showOutline: true,
            showGraticule: true,
            scales: {
                xy: {
                    projection: 'equalEarth'
                }
            },
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });
});

