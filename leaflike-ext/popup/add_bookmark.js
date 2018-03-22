console.log("Running the script");
dev="http://localhost:8000";
prod="https://leaflike.nilenso.com";
leaflike_url = prod;

function redirect(tabs) {
    console.log(tabs[0].url);
    console.log(tabs[0].title);
    redirect_url = leaflike_url + "/bookmarks/add?url=" +
        tabs[0].url + "&title=" + tabs[0].title +
	"&next=/status";

    frame=document.getElementById('frame');
    frame.setAttribute('src', redirect_url);
}

function reportError(error) {
    console.error(`Could not get the active tab: ${error}`);
}

chrome.tabs.query({active: true, currentWindow: true}, redirect);

console.log("Finished with script");
