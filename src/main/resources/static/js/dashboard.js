document.addEventListener("DOMContentLoaded", () => {
    const zone = document.querySelector("[data-mascot-zone]");
    if (!zone) {
        return;
    }

    const mascot = zone.querySelector(".mascot-character");
    if (!mascot) {
        return;
    }

    const levelUp = zone.dataset.levelUp === "true";
    const success = zone.dataset.success === "true";

    if (levelUp) {
        playOnce(mascot, "is-level-up");
        return;
    }

    if (success) {
        playOnce(mascot, "is-success");
    }
});

function playOnce(element, className) {
    element.classList.add(className);
    element.addEventListener("animationend", () => {
        element.classList.remove(className);
    }, { once: true });
}
