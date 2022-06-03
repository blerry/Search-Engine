    // javascript functions

    // search button, submit queries to be outputted in iframe (tables) using jQuery
    $(function() {
        $("#searchButton").on("click", function(evt) {
            let queryValue = document.getElementById("queryInput").value;
            if(queryValue == ""){
                alert("query cannot be empty")
            } 
            else if(queryValue.charAt(0) === ":"){
                specialQuerySearch(queryValue);
            }
            else {
                    $.post("/search", {queryValue: queryValue}, function(result){
                        $(result).prependTo($("#search-contents"));
                    });
             
            }
        })
    })
    // document selecter button, click document title button to print out document contents by sending a post to "/document" using jQuery
    function docClicked(id) {
        $.post("/document", {docValue: id}, function(result){
            $(result).prependTo($("#search-contents"));
        });
    };


    // special queries button, input special query and it will output in the #maindiv
   
    function specialQuerySearch(queryValue) {
            console.log(queryValue);
                $.post("/squery", {queryValue: queryValue}, function(result){
                    $(result).prependTo($("#search-contents"));
                });
    }

