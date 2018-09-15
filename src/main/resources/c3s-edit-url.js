window.onload = function() {
	var elements = document.getElementsByTagName("*");
	for (var i = 0; i < elements.length; i++) {
		if (elements[i].getAttribute("data-c3s-id") !== null) {
			var dataElement = elements[i];

			var editLinkElement = document.createElement("a");
			editLinkElement.setAttribute("href", "/c3s/" + dataElement.getAttribute("data-c3s-id"));
			editLinkElement.setAttribute("target", "_blank");
			editLinkElement.innerHTML = "Edit";

			dataElement.appendChild(editLinkElement);
		}
	}
};