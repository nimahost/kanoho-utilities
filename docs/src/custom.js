let debounce;

window.addEventListener("scroll", () => {
	if (!debounce)
		requestAnimationFrame(() => {
			const header = document.querySelector("header");
			if (window.scrollY > 0) header.classList.add("scrolled");
			else header.classList.remove("scrolled");
			debounce = false;
		});

	debounce = true;
});
