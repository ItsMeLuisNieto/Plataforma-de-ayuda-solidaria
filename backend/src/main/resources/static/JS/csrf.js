function csrfHeaders() {
    const token = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content;
    if (!token || !header) {
        return {};
    }
    const headers = {};
    headers[header] = token;
    return headers;
}
