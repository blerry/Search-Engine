    // search button
    document.addEventListener("DOMContentLoaded", () => { //on window load
        document.getElementById("searchDiv").style.display="none"; 
        document.getElementById("searchButton").addEventListener("click", function(evt) {//on click
            document.getElementById("search-contents").innerHTML = "";
            document.getElementById("search-contents").style.display="";//show contents
            let query = document.getElementById("query").value;//get query input
            if(query == ""){ // no empty search
                alert("query cannot be empty")
            } 
            else if(query.charAt(0) === ":"){//special query serach
                specialQuerySearch(query);//function for it
            }
            else {
                    if (document.getElementById("bool-search").checked == true){
                    $.post("/search", {query: query}, function(result){//post a regular search
                        $(result).prependTo($("#search-contents"));//append results
                        console.log(result);
                     });
                    }
                    else if (document.getElementById("ranked-search").checked == true) {
                        $.post("/ranked-search", {query: query}, function(result){
                            $(result).prependTo($("#search-contents"));
                            console.log(result);
                        });   
                    } else{
                        $.post("/ranked-search-test", {query: query}, function(result){
                            $(result).prependTo($("#search-contents"));
                        });
                    }
            }
        })
    });
    // user picks a document
    function docClicked(id) {//upon a clicked title
        $.post("/document", {docId: id}, function(result){//post to server
            $(result).prependTo($("#search-contents"));//append results
        });
    };


    // special query function
    function specialQuerySearch(query) {
            console.log(query);//debug
                $.post("/squery", {query: query}, function(result){//post to server
                    $(result).prependTo($("#search-contents"));//show results
                });
    }

