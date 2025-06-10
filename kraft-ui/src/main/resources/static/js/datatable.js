document.addEventListener("DOMContentLoaded", () => {
        const headers = document.querySelectorAll(".sortable-header");
        const currentUrl = new URL(window.location.href);

        console.log("contentUrl " + currentUrl);

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
                    // toggle order
                    sortOrders[index] = sortOrders[index] === "asc" ? "desc" : "asc";
                } else {
                    // add new field
                    sortFields.push(field);
                    sortOrders.push(defaultOrder);
                }

                currentUrl.searchParams.set("filter.sortBy", sortFields.join(",&"));
                currentUrl.searchParams.set("filter.sortOrder", sortOrders.join(",&"));

                console.log("currentUrl " + currentUrl);

                window.location.href = currentUrl.toString();
            });
        });
});

// Close dropdowns when clicking outside-->
window.addEventListener('click', function (event) {
  if (!event.target.closest('.action-dropdown')) {
    document.querySelectorAll('.actions-menu').forEach(menu => {
      menu.style.display = 'none';
    });
  }
});

// show confirm delete modal
 function showDeleteModal(id, entityName) {
      const form = document.getElementById("deleteForm");
      form.action = `/admin/crud/${entityName}/delete/${id}`;
      document.getElementById("deleteModal").style.display = "flex";
 }

  function closeDeleteModal() {
       document.getElementById("deleteModal").style.display = "none";
  }

// toggle actions
function toggleActions(button) {
  const dropdown = button.nextElementSibling;
  const isVisible = dropdown.style.display === 'block';

  document.querySelectorAll('.actions-menu').forEach(menu => {
    menu.style.display = 'none';
  });

  dropdown.style.display = isVisible ? 'none' : 'block';
}