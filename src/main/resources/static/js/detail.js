// detail.js (추가해서 문제 파악)
document.addEventListener('DOMContentLoaded', () => {
    console.log("✅ detail.js loaded");

    document.querySelectorAll("form").forEach(form => {
        form.addEventListener("submit", (e) => {
            console.log("📤 Form submitting to:", form.action);
        });
    });

    document.querySelectorAll("a").forEach(a => {
        a.addEventListener("click", () => {
            console.log("🔗 Link clicked:", a.href);
        });
    });
});