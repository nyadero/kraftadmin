document.addEventListener('DOMContentLoaded', function () {
  const selectAllCheckbox = document.getElementById('selectAll');
  const rowCheckboxes = document.querySelectorAll('.rowCheckbox');
  const closeBulkActions = document.getElementById("closeBulkAction");
  const bulkActions = document.getElementById("bulkActions");

  const updateRowStyle = (checkbox) => {
    const row = checkbox.closest('tr');
    checkbox.checked ? row.classList.add('selected') : row.classList.remove('selected');
  };

  selectAllCheckbox?.addEventListener('change', function () {
    rowCheckboxes.forEach(cb => {
      cb.checked = this.checked;
      updateRowStyle(cb);
    });
    updateBulkActionsVisibility();
  });

  rowCheckboxes.forEach(cb => {
    cb.addEventListener('change', function () {
      updateRowStyle(this);
      selectAllCheckbox.checked = Array.from(rowCheckboxes).every(cb => cb.checked);
      updateBulkActionsVisibility();
    });
    if (cb.checked) updateRowStyle(cb);
  });

  function updateBulkActionsVisibility() {
    const anyChecked = Array.from(rowCheckboxes).some(cb => cb.checked);
    bulkActions?.classList.toggle("hidden", !anyChecked);
  }

  closeBulkActions.addEventListener("click", function () {
    console.log("Hiding bulk actions and deselecting rows");

    // Deselect all checkboxes
    selectAllCheckbox.checked = false;
    rowCheckboxes.forEach(cb => {
      cb.checked = false;
      updateRowStyle(cb);
    });

    // Hide the bulkActions component
    bulkActions?.classList.add("hidden");
  });
});

function performBulkAction(action) {
  const selectedIds = Array.from(document.querySelectorAll('.rowCheckbox:checked')).map(cb => cb.value).join(',');
  const entityName = document.getElementById("bulkAction").dataset.entityName;
  console.log("entityName " + action);

  if (!selectedIds) return alert('Please select at least one item.');
  if (action === 'Delete' && !confirm('Are you sure you want to delete the selected items?')) return;

  window.location.href = `/admin/crud/${entityName}/bulk-action?action=${action}&selectedIds=${selectedIds}`;
}

window.performBulkAction = performBulkAction;
