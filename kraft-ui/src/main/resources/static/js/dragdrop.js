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
