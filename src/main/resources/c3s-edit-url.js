window.onload = function() {
	var elements = document.getElementsByTagName("*");
	var editLinkElements = [];

	for (var i = 0; i < elements.length; i++) {
		var dataElement = elements[i];

		if (dataElement.getAttribute("data-c3s-id") !== null) {
			var editLinkElement = document.createElement("a");
			editLinkElement.href = "/c3s/" + dataElement.getAttribute("data-c3s-id");
			editLinkElement.target = "_blank";
			editLinkElement.classList.add("c3s-edit-link");
			editLinkElement.style.display = "none";

			dataElement.appendChild(editLinkElement);

			editLinkElements.push(editLinkElement);
		}
	}

	var linksVisible = false;
	document.addEventListener("keypress", function(event) {
		if (event.key === 'e') {
			for (var i = 0; i < editLinkElements.length; i++) {
				if (!linksVisible) {
					editLinkElements[i].style.display = "inline";
				} else {
					editLinkElements[i].style.display = "none";
				}
			}
			linksVisible = !linksVisible;
		}
	});
};