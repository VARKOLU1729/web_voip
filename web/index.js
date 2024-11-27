
function Start() {
    return new Promise((resolve, reject) => {
        webphone_api.onLoaded = function () {
            console.log("webphone_api loaded successfully");
            webphone_api.setparameter('serveraddress', "sip-bgn-int.ttsl.tel:49868", false);
            webphone_api.setparameter('username', "0605405970003", false);
            webphone_api.setparameter('password', "NFnjgudZXs", false);
            webphone_api.setparameter('destination', "6301450563", false);
            webphone_api.start();
            resolve("Initialization successful");
        };
        webphone_api.onerror = function (err) {
            console.error("webphone_api failed to load", err);
            reject(err);
        };
    });
}

function Call() {
    return new Promise((resolve, reject) => {
        try {
//            webphone_api.setparameter('destination', "6301450563", false);
//            webphone_api.setparameter('serveraddress', "sip-bgn-int.ttsl.tel:49868", false);
//            webphone_api.setparameter('username', "0605405970002", false);
//            webphone_api.setparameter('password', "$eWPQD!Ypy", false);
//            webphone_api.setparameter('destination', "6301450563", false);
            webphone_api.call("6301450563"); // Ensure destnr is defined and correct
            resolve("Call initiated");
        } catch (error) {
            console.error("Error in Call:", error);
            reject(error);
        }
    });
}

function AcceptCall()
{
    return new Promise((resolve, reject) => {
            try {
                webphone_api.accept();
                resolve("Call initiated");
            } catch (error) {
                console.error("Error in Call:", error);
                reject(error);
            }
        });
}
