console.log("RUNNING MAIN"); //check if script running
    document.addEventListener("DOMContentLoaded", () => { //when window loads
        document.getElementById("loadingText").style.display = "none"; //hide loading
     });
    document.getElementById("submitButton").addEventListener("click", function(evt){ //onclick after index submission
    let directory = document.getElementById("directory").value; //get value for directory
    document.getElementById("loadingText").style.display = "";       //show loading since we are indexing
    if(directory == "") { //cannot be empty query
        alert("Directory cannot be empty"); 
        } else {
                document.getElementById("submitButton").style.display = "none"; //hide elements
                document.getElementById("dir").style.display = "none";
                $.post("/", {directory: directory}, function(result){ //post to server the directory
                    $(result).prependTo($("#maindiv")); //show results
                    document.getElementById("loadingText").style.display = "none";
                    document.getElementById("searchDiv").style.display="";
                    document.getElementById("search-contents").style.display="none";
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
        document.getElementById("goToSearch").addEventListener("click", function(evt){ //onclick after index submission
            let directory = document.getElementById("directory").value; //get value for directory
            document.getElementById("loadingText").style.display = "";       //show loading since we are indexing
            if(directory == "") { //cannot be empty query
                alert("Directory cannot be empty"); 
                } else {
                        document.getElementById("submitButton").style.display = "none"; //hide elements
                        document.getElementById("dir").style.display = "none";
                        $.post("/", {directory: directory}, function(result){ //post to server the directory
                           // $(result).prependTo($("#maindiv")); //show results
                            document.getElementById("loadingText").style.display = "none";
                            document.getElementById("searchDiv").style.display="";
                            document.getElementById("search-contents").style.display="none";

                        });
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