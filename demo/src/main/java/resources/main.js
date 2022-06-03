console.log("RUNNING MAIN");
    document.addEventListener("DOMContentLoaded", () => { });

    document.getElementById("submitButton").addEventListener("click", function(evt){
    let directoryValue = document.getElementById("directoryInput").value;
            
    if(directoryValue == "") {
        alert("Directory cannot be empty");
        } else {
                document.getElementById("submitButton").style.display = "none";
                document.getElementById("dir").style.display = "none";
                //document.getElementById("diskIndex").style.display = "none";
                document.getElementById("loadingText").style.display = "";
                $.post("/", {directoryValue: directoryValue}, function(result){
                    $(result).prependTo($("#maindiv"));
                });
                /*
                var httpRequest = new XMLHttpRequest()
                httpRequest.onreadystatechange = function (result) {
                  document.getElementById("maindiv").innerHTML = result;
                }
                httpRequest.open('POST', "http://localhost:4567");
                httpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                httpRequest.send('directoryValue=' + encodeURIComponent(directoryValue));
                */
                //document.getElementById("loadingText").style.display = "none";
            }
        });

    /*
    var httpRequest = new XMLHttpRequest()
httpRequest.onreadystatechange = function (data) {
  result.prependTo(document.getElementById("maindiv"));
}
httpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
httpRequest.open('POST', url);
httpRequest.send('directoryValue=' + encodeURIComponent(directoryValue));
    */