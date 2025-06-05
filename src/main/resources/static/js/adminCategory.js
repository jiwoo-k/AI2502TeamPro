document.addEventListener('DOMContentLoaded', function () {
    const editBtn = document.querySelector('button.btn-warning[formaction="/admin/category/edit"]');

    if (editBtn) {
        editBtn.addEventListener('click', function (e) {
            const selected = document.querySelector('input[name="selectedCategory"]:checked');
            if (!selected) {
                e.preventDefault();
                alert("카테고리를 선택해주세요.");
            }
        });
    }
});