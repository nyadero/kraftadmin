document.addEventListener("DOMContentLoaded", function () {
         console.log("scriptjs loaded");
 const loader = document.getElementById('loader');
 if (loader) loader.style.display = 'none';

  /** ---------------- Sidebar Toggle ---------------- **/
const menuToggle = document.getElementById('menu-toggle');
const sidebar = document.getElementById('sidebar');

if (menuToggle && sidebar) {
    console.log("toggling sidebar");
    menuToggle.addEventListener('click', () => {
        // Toggle sidebar width between expanded (w-80) and collapsed (w-fit)
        sidebar.classList.toggle('w-80');
        sidebar.classList.toggle('w-fit');
sidebar.classList.toggle('collapsed');

        // Toggle visibility of text elements, but preserve dropdown state
        const textElements = sidebar.querySelectorAll('.sidebar-text');
        textElements.forEach(element => {
            // Skip dropdown containers - they should maintain their current state
            if (element.id === 'entity-list' || element.id === 'monitoring-list') {
                return;
            }
            element.classList.toggle('hidden');
        });

        // Handle dropdown containers separately
        const entityList = document.getElementById('entity-list');
        const monitoringList = document.getElementById('monitoring-list');

        if (sidebar.classList.contains('w-fit')) {
            // When collapsed, ensure dropdowns are hidden
            entityList.classList.add('hidden');
            monitoringList.classList.add('hidden');
        }

        // Adjust horizontal padding for smaller state
        sidebar.classList.toggle('px-2');
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


//document.querySelectorAll("charts").forEach(canvas => {

//  const ctx = document.getElementById("revenueChart");
//
//  new Chart(ctx, {
//      type: 'line',
//      data: {
//          labels: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
//          datasets: [{
//              label: 'Revenue',
//              data: [1200, 1300, 1400, 1800, 2100, 2000, 2300],
//              borderColor: "#00b894",
//              fill: false
//          }]
//      },
//      options: {
//          responsive: true,
//          plugins: { legend: { display: false } }
//      }
//  });

});
