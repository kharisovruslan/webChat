var messagesLastId = -1;
var idtimer = setInterval(readMessagesList, 8000);
var responseUsers = "";
var responseMessage = "";

var page = 1;
var size = 5;

function readMessagesList() {
    var urlLast = '/messages/lastId';
    var urlList = '/messages/list';
    $.ajax({
        url: urlLast, success: function (lastIdServer) {
            if (lastIdServer !== null) {
                if (messagesLastId != lastIdServer) {
                    messagesLastId = lastIdServer;
                    var filter = $("#filter").val();
                    var url = urlList + '?page=' + page + '&size=' + size;
                    if(filter !== "") {
                        url = url + '&filter=' + filter;
                    }
                    $.ajax({
                        url: url, success: function (messagesFragment) {
                            if (responseMessage != messagesFragment) {
                                $("#messages").html(messagesFragment);
                                responseMessage = messagesFragment;
                            }
                        }
                    });
                }
            }
        }
    });
}

function getParam(name){
   if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
      return decodeURIComponent(name[1]);
}

$( document ).ready(function() {
    if(typeof getParam('page') !== 'undefined') {
        page = getParam('page');
    }
    if(typeof getParam('size') !== 'undefined') {
        size = getParam('size');
    }
    readMessagesList();
});

$('#filter').on('input',function(e){
    messagesLastId = "";
    page = 1;
    readMessagesList();
});

$( "#messageForm" ).submit(function( event ) {
    $("#textHelp").text("");
    $("#receiversHelp").text("");
    if (!$.trim($("#formText").val())) {
        $("#textHelp").text("Please write something");
        event.preventDefault();
    }
    var receivers = $("#receiversCheckBox input:checkbox:checked").map(function(){
      return $(this).val();
    }).get();
    if(jQuery.isEmptyObject(receivers)) {
        $("#receiversHelp").text("Please choose receivers");
        event.preventDefault();
    }
});