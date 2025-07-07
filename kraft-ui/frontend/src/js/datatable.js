//document.addEventListener("DOMContentLoaded", () => {
//        const headers = document.querySelectorAll(".sortable-header");
//        const currentUrl = new URL(window.location.href);
//
//        console.log("contentUrl " + currentUrl);
//
//        headers.forEach(header => {
//            header.addEventListener("click", (e) => {
//                e.preventDefault();
//
//                const field = header.dataset.field;
//                const defaultOrder = header.dataset.defaultOrder || 'asc';
//
//                let sortBy = currentUrl.searchParams.get("filter.sortBy") || "";
//                let sortOrder = currentUrl.searchParams.get("filter.sortOrder") || "";
//
//                let sortFields = sortBy ? sortBy.split(",") : [];
//                let sortOrders = sortOrder ? sortOrder.split(",") : [];
//
//                const index = sortFields.indexOf(field);
//
//                if (index > -1) {
//                    // toggle order
//                    sortOrders[index] = sortOrders[index] === "asc" ? "desc" : "asc";
//                } else {
//                    // add new field
//                    sortFields.push(field);
//                    sortOrders.push(defaultOrder);
//                }
//
//                currentUrl.searchParams.set("filter.sortBy", sortFields.join(",&"));
//                currentUrl.searchParams.set("filter.sortOrder", sortOrders.join(",&"));
//
//                console.log("currentUrl " + currentUrl);
//
//                window.location.href = currentUrl.toString();
//            });
//        });
//});
//
//// Close dropdowns when clicking outside-->
//window.addEventListener('click', function (event) {
//  if (!event.target.closest('.action-dropdown')) {
//    document.querySelectorAll('.actions-menu').forEach(menu => {
//      menu.style.display = 'none';
//    });
//  }
//});
//
//// show confirm delete modal
// function showDeleteModal(id, entityName) {
//      const form = document.getElementById("deleteForm");
//      form.action = `/admin/crud/${entityName}/delete/${id}`;
//      document.getElementById("deleteModal").style.display = "flex";
// }
//
//  function closeDeleteModal() {
//       document.getElementById("deleteModal").style.display = "none";
//  }
//
//// toggle actions
//function toggleActions(button) {
//  const dropdown = button.nextElementSibling;
//  const isVisible = dropdown.style.display === 'block';
//
//  document.querySelectorAll('.actions-menu').forEach(menu => {
//    menu.style.display = 'none';
//  });
//
//  dropdown.style.display = isVisible ? 'none' : 'block';
//}

// Sorting table headers by clicking
document.addEventListener("DOMContentLoaded", () => {
  const headers = document.querySelectorAll(".sortable-header");
  const currentUrl = new URL(window.location.href);

  headers.forEach(header => {
    header.addEventListener("click", (e) => {
      e.preventDefault();

      const field = header.dataset.field;
      const defaultOrder = header.dataset.defaultOrder || 'asc';

      let sortBy = currentUrl.searchParams.get("filter.sortBy") || "";
      let sortOrder = currentUrl.searchParams.get("filter.sortOrder") || "";

      let sortFields = sortBy ? sortBy.split(",") : [];
      let sortOrders = sortOrder ? sortOrder.split(",") : [];

      const index = sortFields.indexOf(field);

      if (index > -1) {
        sortOrders[index] = sortOrders[index] === "asc" ? "desc" : "asc";
      } else {
        sortFields.push(field);
        sortOrders.push(defaultOrder);
      }

      currentUrl.searchParams.set("filter.sortBy", sortFields.join(",&"));
      currentUrl.searchParams.set("filter.sortOrder", sortOrders.join(",&"));

      window.location.href = currentUrl.toString();
    });
  });
});

// Close any open action dropdown when clicking outside of it
window.addEventListener("click", (event) => {
  if (!event.target.closest(".action-dropdown")) {
    document.querySelectorAll(".actions-menu").forEach(menu => {
      menu.style.display = "none";
    });
  }
});

// Show delete confirmation modal (accessible from inline onclick)
window.showDeleteModal = function (id, entityName) {
  const form = document.getElementById("deleteForm");
  if (!form) return;
  form.action = `/admin/crud/${entityName}/delete/${id}`;
  document.getElementById("deleteModal").style.display = "flex";
};

// Close delete confirmation modal
window.closeDeleteModal = function () {
  const modal = document.getElementById("deleteModal");
  if (modal) modal.style.display = "none";
};

// Toggle dropdown action menu
//window.toggleActions = function (button) {
//  const dropdown = button.nextElementSibling;
//  const isVisible = dropdown.style.display === "block";
//
//  document.querySelectorAll(".actions-menu").forEach(menu => {
//    menu.style.display = "none";
//  });
//
//  dropdown.style.display = isVisible ? "none" : "block";
//};

window.toggleActions = function (button) {
  const dropdown = button.nextElementSibling;
  const isVisible = dropdown.style.display === "block";

  // Hide all other open dropdowns
  document.querySelectorAll(".actions-menu").forEach(menu => {
    menu.style.display = "none";
    menu.classList.remove("align-left");
  });

  if (!isVisible) {
    dropdown.style.display = "block";

    // Check if dropdown is overflowing the viewport
    const dropdownRect = dropdown.getBoundingClientRect();
    const overflowRight = dropdownRect.right > window.innerWidth;

    if (overflowRight) {
      dropdown.style.left = 'auto';
      dropdown.style.right = '0';
    } else {
      dropdown.style.right = '0';
    }
  } else {
    dropdown.style.display = "none";
  }
};

