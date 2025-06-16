document.addEventListener('DOMContentLoaded', function () {
  const editors = document.querySelectorAll('.quill-editor');

  editors.forEach(editorDiv => {
    const fieldName = editorDiv.getAttribute('data-target');
    const input = document.getElementById(`${fieldName}-input`);

    const quill = new Quill(`#${editorDiv.id}`, { theme: 'snow' });

    if (input && input.value) {
      quill.root.innerHTML = input.value;
    }

    quill.on('text-change', function () {
      if (input) input.value = quill.root.innerHTML;
    });
  });
});
