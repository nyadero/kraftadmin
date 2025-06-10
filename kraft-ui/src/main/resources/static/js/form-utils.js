
//
//function filterSelectOptions(selectId) {
//  console.log("Searching " + selectId);
//
//  const input = document.getElementById(selectId + '-search');
//  if (!input) {
//    console.warn("Input element not found: " + selectId + '-search');
//    return;
//  }
//
//  const filter = input.value.toLowerCase();
//  const select = document.getElementById(selectId);
//  const options = select.getElementsByTagName('option');
//
//  for (let i = 0; i < options.length; i++) {
//    const option = options[i];
//    if (option.disabled) continue;
//
//    const text = option.textContent || option.innerText;
//    option.style.display = text.toLowerCase().includes(filter) ? '' : 'none';
//  }
//}


function filterSelectOptions(selectId) {
  console.log("Searching " + selectId);

  const input = document.getElementById(selectId + '-search');
  if (!input) {
    console.warn("Input element not found: " + selectId + '-search');
    return;
  }

  const filter = input.value.toLowerCase();
  const select = document.getElementById(selectId);
  const options = Array.from(select.getElementsByTagName('option'));

  let visibleCount = 0;

  options.forEach(option => {
    if (option.disabled && option.selected) return; // skip default
    const text = option.textContent || option.innerText;
    const match = text.toLowerCase().includes(filter);
    option.style.display = match ? '' : 'none';
    if (match) visibleCount++;
  });

  // Remove any existing 'no-data' option
  const existingNoData = select.querySelector('option.no-data');
  if (existingNoData) {
    select.removeChild(existingNoData);
  }

  // If none are visible, add a 'No data found' disabled option
  if (visibleCount === 0) {
    const noDataOption = document.createElement('option');
    noDataOption.disabled = true;
    noDataOption.className = 'no-data';
    noDataOption.textContent = 'No data found';
    select.appendChild(noDataOption);
  }
}


function confirmDeleteSingle(id, entityName) {
  if (confirm("Are you sure you want to delete this item?")) {
    window.location.href = `/admin/${entityName}/delete/${id}`;
  }
}

function filterSelectOptions(selectId) {
  console.log("Searching " + selectId);

  const input = document.getElementById(selectId + '-search');
  if (!input) {
    console.warn("Input element not found: " + selectId + '-search');
    return;
  }

  const filter = input.value.toLowerCase();
  const select = document.getElementById(selectId);
  const options = Array.from(select.getElementsByTagName('option'));

  let visibleCount = 0;

  options.forEach(option => {
    if (option.disabled && option.selected) return; // skip default
    const text = option.textContent || option.innerText;
    const match = text.toLowerCase().includes(filter);
    option.style.display = match ? '' : 'none';
    if (match) visibleCount++;
  });

  // Remove any existing 'no-data' option
  const existingNoData = select.querySelector('option.no-data');
  if (existingNoData) {
    select.removeChild(existingNoData);
  }

  // If none are visible, add a 'No data found' disabled option
  if (visibleCount === 0) {
    const noDataOption = document.createElement('option');
    noDataOption.disabled = true;
    noDataOption.className = 'no-data';
    noDataOption.textContent = 'No data found';
    select.appendChild(noDataOption);
  }
}


 // Initialize Tagify on all inputs with class 'tagify-input'
    document.querySelectorAll('.tagify-input').forEach(input => {
        new Tagify(input, {
            whitelist: input.dataset.suggestions ? input.dataset.suggestions.split(",") : [],
            dropdown: {
                enabled: 1,
                maxItems: 20,
                classname: "tagify-dropdown",
                closeOnSelect: false
            }
        });
    });

