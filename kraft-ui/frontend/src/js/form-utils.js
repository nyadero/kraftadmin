import TomSelect from 'tom-select';

document.addEventListener("DOMContentLoaded", function () {
    // Single select
           document.querySelectorAll('.tom-select').forEach(el => {
               new TomSelect(el, {
                   allowEmptyOption: true
               });
           });

           // Multi-select
           document.querySelectorAll('.tom-multiselect').forEach(el => {
               new TomSelect(el, {
                   plugins: ['remove_button'],
                   persist: false,
                   create: false
               });
           });

           // Tag input
           document.querySelectorAll('.tom-tags').forEach(el => {
               new TomSelect(el, {
                   delimiter: ',',
                   persist: false,
                   create: true
               });
           });


//            // Single select
//               document.querySelectorAll('.tom-select').forEach(el => {
//                   new TomSelect(el, {
//                       allowEmptyOption: true,
//                       searchField: ['text'],
//                       maxOptions: 50,
//                       render: {
//                           option: function(data, escape) {
//                               return '<div>' + escape(data.text) + '</div>';
//                           },
//                           item: function(data, escape) {
//                               return '<div>' + escape(data.text) + '</div>';
//                           }
//                       }
//                   });
//               });
//
//               // Multi-select
//               document.querySelectorAll('.tom-multiselect').forEach(el => {
//                   new TomSelect(el, {
//                       plugins: ['remove_button'],
//                       persist: false,
//                       create: false,
//                       hideSelected: true,
//                       closeAfterSelect: true,
//                       searchField: ['text'],
//                       maxOptions: 50,
//                       render: {
//                           option: function(data, escape) {
//                               return '<div>' + escape(data.text) + '</div>';
//                           },
//                           item: function(data, escape) {
//                               return '<div>' + escape(data.text) + '</div>';
//                           }
//                       }
//                   });
//               });
//
//               // Tag input
//               document.querySelectorAll('.tom-tags').forEach(el => {
//                   new TomSelect(el, {
//                       plugins: ['remove_button'],
//                       delimiter: ',',
//                       persist: false,
//                       create: true,
//                       hideSelected: true,
//                       closeAfterSelect: true,
//                       searchField: ['text'],
//                       maxOptions: 50,
//                       render: {
//                           option: function(data, escape) {
//                               return '<div>' + escape(data.text) + '</div>';
//                           },
//                           item: function(data, escape) {
//                               return '<div>' + escape(data.text) + '</div>';
//                           }
//                       }
//                   });
//               });

    const subtypeSelector = document.querySelector("select[name='subtype']");
    const allSubtypeGroups = document.querySelectorAll(".subtype-group");

    function toggleSubtypeFields(selected) {
        allSubtypeGroups.forEach(group => {
            if (group.classList.contains(`subtype-group-${selected}`)) {
                group.classList.remove("hidden");
            } else {
                group.classList.add("hidden");
            }
        });
    }

    subtypeSelector?.addEventListener("change", function () {
        toggleSubtypeFields(this.value);
    });

    // Initial load (e.g., edit mode)
    if (subtypeSelector?.value) {
        toggleSubtypeFields(subtypeSelector.value);
    }
});

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

function filterSelectOptions1(selectId) {
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

 function updatePhonePrefix(selectElement, inputId) {
    const input = document.getElementById(inputId);
    const newCode = selectElement.value;

    // Remove existing dial code (if any)
    const existingValue = input.value;

    // If the input starts with a country code, strip it
    const cleaned = existingValue.replace(/^\+\d+/, '').trim();

    // Update input with selected country code
    input.value = newCode + cleaned;
  }

