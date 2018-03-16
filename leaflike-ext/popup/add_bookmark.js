console.log("Running the script");
dev="http://localhost:8000";
prod="https://leaflike.nilenso.com";
leaflike_url = prod;
function redirect(tabs) {
    console.log(tabs[0].url);
    console.log(tabs[0].title);
    window.location = leaflike_url + "/bookmarks/add?url=" +
	tabs[0].url + "&title=" + tabs[0].title +
	"&next=/status";
}

function reportError(error) {
    console.error(`Could not get the active tab: ${error}`);
}

browser.tabs.query({active: true, currentWindow: true})
    .then(redirect)
    .catch(reportError);

console.log("Finished with script");
