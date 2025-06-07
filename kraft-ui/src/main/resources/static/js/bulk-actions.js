document.addEventListener('DOMContentLoaded', function () {
  const selectAllCheckbox = document.getElementById('selectAll');
  const rowCheckboxes = document.querySelectorAll('.rowCheckbox');

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
    const bulkActions = document.getElementById("bulkActions");
    const anyChecked = Array.from(rowCheckboxes).some(cb => cb.checked);
    bulkActions?.classList.toggle("hidden", !anyChecked);
  }
});

function performBulkAction(action) {
  const selectedIds = Array.from(document.querySelectorAll('.rowCheckbox:checked')).map(cb => cb.value).join(',');
  const entityName = '[[${entityName}]]'; // Thymeleaf inline

  if (!selectedIds) return alert('Please select at least one item.');
  if (action === 'delete' && !confirm('Are you sure you want to delete the selected items?')) return;

  window.location.href = `/admin/crud/${entityName}/bulk-action?action=${action}&selectedIds=${selectedIds}`;
}
