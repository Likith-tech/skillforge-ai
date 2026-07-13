/**
 * The backend's GlobalExceptionHandler returns { message, fieldErrors } on
 * failures. This flattens that shape into one string for display in a
 * single alert banner.
 */
export function getErrorMessage(error, fallback = 'Something went wrong. Please try again.') {
  const data = error?.response?.data;

  if (!data) {
    return error?.message || fallback;
  }

  if (data.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
    return Object.values(data.fieldErrors).join(' ');
  }

  return data.message || fallback;
}
