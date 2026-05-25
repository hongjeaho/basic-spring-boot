// ES6+ client-side validation for board form
(() => {
    'use strict';

    const form = document.getElementById('boardForm');
    const titleInput = document.getElementById('title');
    const authorInput = document.getElementById('author');
    const contentInput = document.getElementById('content');

    // Validation functions
    const validateRequired = (value, fieldName) => {
        if (!value || value.trim() === '') {
            return `${fieldName}은(는) 필수입니다.`;
        }
        return null;
    };

    const validateMaxLength = (value, maxLength, fieldName) => {
        if (value && value.length > maxLength) {
            return `${fieldName}은(는) ${maxLength}자 이내로 작성해주세요.`;
        }
        return null;
    };

    const showError = (elementId, message) => {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
        }
    };

    const clearErrors = () => {
        showError('titleError', '');
        showError('authorError', '');
        showError('contentError', '');
    };

    const validateForm = () => {
        clearErrors();
        let isValid = true;

        // Validate title
        const titleError = validateRequired(titleInput.value, '제목') ||
                          validateMaxLength(titleInput.value, 200, '제목');
        if (titleError) {
            showError('titleError', titleError);
            isValid = false;
        }

        // Validate author
        const authorError = validateRequired(authorInput.value, '작성자') ||
                           validateMaxLength(authorInput.value, 50, '작성자');
        if (authorError) {
            showError('authorError', authorError);
            isValid = false;
        }

        // Validate content
        const contentError = validateRequired(contentInput.value, '내용');
        if (contentError) {
            showError('contentError', contentError);
            isValid = false;
        }

        return isValid;
    };

    // Form submission handler
    if (form) {
        form.addEventListener('submit', (e) => {
            if (!validateForm()) {
                e.preventDefault();
            }
        });

        // Real-time validation on blur
        titleInput.addEventListener('blur', () => {
            const error = validateRequired(titleInput.value, '제목') ||
                         validateMaxLength(titleInput.value, 200, '제목');
            showError('titleError', error || '');
        });

        authorInput.addEventListener('blur', () => {
            const error = validateRequired(authorInput.value, '작성자') ||
                         validateMaxLength(authorInput.value, 50, '작성자');
            showError('authorError', error || '');
        });

        contentInput.addEventListener('blur', () => {
            const error = validateRequired(contentInput.value, '내용');
            showError('contentError', error || '');
        });
    }
})();
