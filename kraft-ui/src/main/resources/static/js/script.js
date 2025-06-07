document.addEventListener("DOMContentLoaded", function () {
  /** ---------------- Sidebar Toggle ---------------- **/
  const menuIcon = document.getElementById("menu-toggle");
  menuIcon?.addEventListener("click", function () {
    console.log("Toggling sidebar");
    document.getElementById("sidebar")?.classList.toggle("d-none");
  });

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
});
