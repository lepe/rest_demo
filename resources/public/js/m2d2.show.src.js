/**
 * @author: A. Lepe
 * @url : https://gitlab.com/lepe/m2d2/
 * @since: May, 2018
 * 
 * This is an extension to use the property "show" to hide/show elements
 * It will keep previous "display" property value and restore it upon "show".
 * If there is not "previous" display property will search for "data-display"
 * attribute or will set the default for the specified element tag.
 */
m2d2.ext({
	show : function(show, node) {
	    var cssDisplay = function() { return getComputedStyle(node, null).display; }
	    var defaultDisplay = function() {
	        var b = document.getElementsByTagName("body")[0];
	        var t = document.createElement("template");
	        var n = document.createElement(node.tagName);
	        t.appendChild(n);
	        b.appendChild(t);
	        var display = getComputedStyle(n, null).display;
	        t.remove();
	        return display;
	    }
		if(show) {
            if(cssDisplay() == "none") {
                if(node.dataset._m2d2_display) {
                    node.style.display = node.dataset._m2d2_display;
                } else {
                    node.style.removeProperty("display");
                    if(cssDisplay() == "none") {
                        var defaultShow = defaultDisplay()
                        node.style.display = node.dataset.display || (defaultShow != "none" ? defaultShow : "block");
                    }
                }
            }
		} else {
		    var stored = node.style.display != "none" ? node.style.display : cssDisplay();
		    if(stored != "none") {
		        node.dataset._m2d2_display = stored;
		    }
			node.style.display = "none"
		}
	}
});
