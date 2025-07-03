document.addEventListener("DOMContentLoaded", function () {
 const loader = document.getElementById('loader');
 if (loader) loader.style.display = 'none';

  /** ---------------- Sidebar Toggle ---------------- **/
//  const menuIcon = document.getElementById("menu-toggle");
//  menuIcon?.addEventListener("click", function () {
//    console.log("Toggling sidebar");
//    document.getElementById("sidebar")?.classList.toggle("d-none");
//  });

  const menuToggle = document.getElementById('menu-toggle');
      const sidebar = document.getElementById('sidebar');

      if (menuToggle && sidebar) {
          menuToggle.addEventListener('click', () => {
              sidebar.classList.toggle('-translate-x-full'); // hide
              sidebar.classList.toggle('translate-x-0');     // show
          });
      }

  /** ---------------- Tab Navigation ---------------- **/
  const buttons = document.querySelectorAll(".tab-button");
  const contents = document.querySelectorAll(".tab-content");

  function activateTab(tabName) {
    buttons.forEach((btn) => {
      btn.classList.toggle("active", btn.dataset.tab === tabName);
    });
    contents.forEach((content) => {
      content.classList.toggle("active", content.id === `tab-${tabName}`);
    });
  }

  buttons.forEach((button) => {
    button.addEventListener("click", () => {
      activateTab(button.dataset.tab);
    });
  });

  // Set default tab
  activateTab("account");

  /** ---------------- Filtering Select Inputs ---------------- **/
  function filterSelectOptions(selectId) {
    const input = document.getElementById(selectId + "-search");
    const filter = input.value.toLowerCase();
    const select = document.getElementById(selectId);
    const options = select.getElementsByTagName("option");

    for (let i = 0; i < options.length; i++) {
      const option = options[i];
      const text = option.textContent || option.innerText;
      option.style.display = text.toLowerCase().includes(filter) ? "" : "none";
    }
  }

  /** ---------------- Toggle All Helper ---------------- **/
  function toggleSelectAll(source) {
    rowCheckboxes.forEach((cb) => {
      cb.checked = source.checked;
    });
  }

  /** ---------------- Placeholder: Search Form Toggle ---------------- **/
  // Uncomment and link your search modal button if needed
  /*
  const filtersBtn = document.getElementById("filtersBtn");
  const searchForm = document.getElementById("searchModal");

  filtersBtn?.addEventListener("click", function () {
    console.log("Toggling search modal");
    if (searchForm.style.display === "none" || searchForm.style.display === "") {
      searchForm.style.display = "flex";
    } else {
      searchForm.style.display = "none";
    }
  });
  */

//document.querySelectorAll("charts").forEach(canvas => {

  const ctx = document.getElementById("revenueChart");

  new Chart(ctx, {
      type: 'line',
      data: {
          labels: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
          datasets: [{
              label: 'Revenue',
              data: [1200, 1300, 1400, 1800, 2100, 2000, 2300],
              borderColor: "#00b894",
              fill: false
          }]
      },
      options: {
          responsive: true,
          plugins: { legend: { display: false } }
      }
  });

//});



//document.querySelectorAll('canvas[data-chart-title]').forEach(canvas => {
//    const ctx = canvas.getContext('2d');
//    const chartTitle = canvas.dataset.chartTitle;
//
//    try {
////        const labels = canvas.dataset.chartLabels;
//        const values = canvas.dataset.chartValues;
//         const labels = JSON.parse(canvas.dataset.labels);
////                const values = JSON.parse(canvas.dataset.values);
//
//        new Chart(ctx, {
//            type: 'line',
//            data: {
//                labels: labels,
//                datasets: [{
//                    label: chartTitle,
//                    data: values,
//                    borderColor: "#00b894",
//                    fill: false
//                }]
//            },
//            options: {
//                responsive: true,
//                plugins: {
//                    legend: { display: false }
//                }
//            }
//        });
//    } catch (err) {
//        console.error("Invalid JSON for chart data:", err);
//    }
//});




});
