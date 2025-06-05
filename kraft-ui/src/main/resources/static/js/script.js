document.addEventListener("DOMContentLoaded", function () {
  /** ---------------- Sidebar Toggle ---------------- **/
  const menuIcon = document.getElementById("menu-toggle");
  menuIcon?.addEventListener("click", function () {
    console.log("Toggling sidebar");
    document.getElementById("sidebar")?.classList.toggle("d-none");
  });

  /** ---------------- TinyMCE Editor ---------------- **/
  tinymce.init({
    selector: ".wysiwyg-editor",
    height: 300,
    menubar: false,
    plugins: "lists link image preview code",
    toolbar: "undo redo | formatselect | bold italic | bullist numlist | link image | code",
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

  /** ---------------- Select/Deselect All ---------------- **/
  const selectAll = document.getElementById("selectAll");
  const rowCheckboxes = document.querySelectorAll(".rowCheckbox");

  selectAll?.addEventListener("change", function () {
    console.log("Selecting all checkboxes:", selectAll.checked);
    rowCheckboxes.forEach((cb) => {
      cb.checked = selectAll.checked;
    });
  });

  rowCheckboxes.forEach((cb) => {
    cb.addEventListener("change", function () {
      if (!cb.checked) {
        selectAll.checked = false;
      } else if ([...rowCheckboxes].every((c) => c.checked)) {
        selectAll.checked = true;
      }
    });
  });

  /** ---------------- Row Click Select ---------------- **/
  document.querySelectorAll("tbody tr").forEach((row) => {
    row.addEventListener("click", function (e) {
      if (
        e.target.tagName !== "INPUT" &&
        e.target.tagName !== "A" &&
        !e.target.closest(".actions-menu")
      ) {
        const checkbox = row.querySelector(".rowCheckbox");
        checkbox.checked = !checkbox.checked;
        checkbox.dispatchEvent(new Event("change"));
      }
    });
  });

  /** ---------------- Dropdown Actions ---------------- **/
  const actionsMenu = document.querySelectorAll(".actions-menu");

  function toggleMenu(id) {
    console.log("Toggling actions menu for", id);
    actionsMenu.forEach((menu) => {
      if (menu.id !== id) menu.style.display = "none";
    });

    const menu = document.getElementById(id);
    if (menu) {
      menu.style.display = menu.style.display === "block" ? "none" : "block";
    }
  }

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
