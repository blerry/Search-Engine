    // search button
    document.addEventListener("DOMContentLoaded", () => { //on window load
        document.getElementById("searchDiv").style.display="none"; 
        document.getElementById("searchButton").addEventListener("click", function(evt) {//on click
            document.getElementById("search-contents").style.display="";//show contents
            let query = document.getElementById("query").value;//get query input
            if(query == ""){ // no empty search
                alert("query cannot be empty")
            } 
            else if(query.charAt(0) === ":"){//special query serach
                specialQuerySearch(query);//function for it
            }
            else {
                    $.post("/search", {query: query}, function(result){//post a regular search
                        $(result).prependTo($("#search-contents"));//append results
                    });
             
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
    function specialQuerySearch(squery) {
            console.log(squery);//debug
                $.post("/squery", {squery: squery}, function(result){//post to server
                    $(result).prependTo($("#search-contents"));//show results
                });
    }

