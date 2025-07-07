document.addEventListener("DOMContentLoaded", function () {
 const loader = document.getElementById('loader');
 if (loader) loader.style.display = 'none';

  /** ---------------- Sidebar Toggle ---------------- **/
  const menuToggle = document.getElementById('menu-toggle');
      const sidebar = document.getElementById('sidebar');

      if (menuToggle && sidebar) {
          menuToggle.addEventListener('click', () => {
              sidebar.classList.toggle('-translate-x-full'); // hide
              sidebar.classList.toggle('translate-x-0');     // show
          });
      }
//   const menuToggle = document.getElementById('menu-toggle');
//   const sidebar = document.getElementById('sidebar');
//   const sidebarWrapper = document.getElementById('sidebar-wrapper');
//
//   if (menuToggle && sidebar && sidebarWrapper) {
//     menuToggle.addEventListener('click', () => {
//       sidebar.classList.toggle('-translate-x-full');
//       sidebarWrapper.classList.toggle('w-64');
//       sidebarWrapper.classList.toggle('w-0');
//     });
//   }

//const main = document.getElementById('main');
//
//if (main) {
//  main.classList.toggle('ml-64');
//  main.classList.toggle('ml-0');
//}


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
