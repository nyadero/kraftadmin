function handleDragOver(e) {
  e.preventDefault();
  e.stopPropagation();
}

function handleFileDrop(e, container) {
  e.preventDefault();
  e.stopPropagation();
  const input = container.querySelector('input[type="file"]');
  input.files = e.dataTransfer.files;
  previewFiles({ target: input });
}

function previewFiles(event) {
  const input = event.target;
  const previewId = 'preview-' + input.id;
  const previewContainer = document.getElementById(previewId);
  if (!previewContainer) {
    console.warn(`No preview container found for ID: ${previewId}`);
    return;
  }
  previewContainer.innerHTML = '';

  const files = input.files;
  Array.from(files).forEach(file => {
    const reader = new FileReader();

    if (file.type.startsWith('image/')) {
      reader.onload = function (e) {
        const img = document.createElement('img');
        img.src = e.target.result;
        img.className = 'w-full h-auto max-h-48 object-cover rounded shadow';
        previewContainer.appendChild(img);
      };
      reader.readAsDataURL(file);
    } else {
      const fileName = document.createElement('div');
      fileName.textContent = file.name;
      fileName.className = 'text-sm text-gray-700';
      previewContainer.appendChild(fileName);
    }
  });
}

 function validateFile(event, input) {
      console.log("validating files " + input)
      const file = input.files[0];
      if (!file) return;

      const forbiddenExtensions = ['.exe', '.bat', '.sh', '.cmd', '.msi', '.com'];
      const fileName = file.name.toLowerCase();

      const isExecutable = forbiddenExtensions.some(ext => fileName.endsWith(ext));

      if (isExecutable) {
          alert("Executable files are not allowed.");
          input.value = ''; // Clear the input
          return;
      }

      previewFiles(event);
  }

  function previewFile(event, input) {
      const file = input.files[0];
      if (!file) return;

      const previewContainer = document.getElementById(`preview-${input.name}`);
      if (!previewContainer) return;

      previewContainer.innerHTML = ""; // Clear previous preview

      const fileType = file.type;
      const reader = new FileReader();

      reader.onload = function (e) {
          if (fileType.startsWith("image/")) {
              const img = document.createElement("img");
              img.src = e.target.result;
              img.alt = file.name;
              img.classList.add("max-w-xs", "max-h-48", "mt-2", "rounded");
              previewContainer.appendChild(img);
          } else {
              const fileInfo = document.createElement("div");
              fileInfo.textContent = `Selected file: ${file.name}`;
              fileInfo.classList.add("text-sm", "text-gray-600", "mt-2");
              previewContainer.appendChild(fileInfo);
          }
      };

      reader.readAsDataURL(file);
  }

window.handleDragOver = handleDragOver;
window.handleFileDrop = handleFileDrop;
window.previewFiles = previewFiles;
window.validateFile = validateFile;
window.previewFile = previewFile;