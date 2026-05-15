document.addEventListener("DOMContentLoaded", () => {
    const categorySelect = document.querySelector("[data-category-select]");
    const subCategorySelect = document.querySelector("[data-sub-category-select]");

    if (!categorySelect || !subCategorySelect) {
        return;
    }

    const options = Array.from(subCategorySelect.options);
    const selectedOptionOnLoad = subCategorySelect.selectedOptions[0];
    if (!categorySelect.value && selectedOptionOnLoad?.dataset.categoryId) {
        categorySelect.value = selectedOptionOnLoad.dataset.categoryId;
    }

    const syncSubCategories = () => {
        const categoryId = categorySelect.value;
        let firstVisibleOption = null;

        options.forEach((option) => {
            const visible = !option.dataset.categoryId || option.dataset.categoryId === categoryId;
            option.hidden = !visible;
            option.disabled = !visible;

            if (visible && option.dataset.categoryId && firstVisibleOption === null) {
                firstVisibleOption = option;
            }
        });

        const selectedOption = subCategorySelect.selectedOptions[0];
        if (!selectedOption || selectedOption.hidden) {
            subCategorySelect.value = firstVisibleOption ? firstVisibleOption.value : "";
        }
    };

    categorySelect.addEventListener("change", syncSubCategories);
    syncSubCategories();
});
