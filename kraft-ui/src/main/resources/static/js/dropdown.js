function toggleActions(button) {
  const dropdown = button.nextElementSibling;
  const isVisible = dropdown.style.display === 'block';

  document.querySelectorAll('.actions-menu').forEach(menu => {
    menu.style.display = 'none';
  });

  dropdown.style.display = isVisible ? 'none' : 'block';
}

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
      form.action = `/admin/${entityName}/delete/${id}`;
      document.getElementById("deleteModal").style.display = "flex";
 }

  function closeDeleteModal() {
       document.getElementById("deleteModal").style.display = "none";
  }

