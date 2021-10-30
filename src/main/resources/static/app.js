// Used for testing, cannot be used for prod
var stompClient = null;
let gameId = null;
let username = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#games").html("");
}

function connect() {
    username = $("#name").val();
    var socket = new SockJS('/new-player');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/game/' + username, function (game) {
            gameId = JSON.parse(game.body).gameId;
            showUser(JSON.parse(game.body).gameId);
            resubscribeToGame();
        });

    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/create", {}, JSON.stringify({'username': $("#name").val()}));
}

function resubscribeToGame() {
    stompClient.unsubscribe();
    stompClient.subscribe("/game/join/" + gameId, function() {
            console.log("Joined the game with Id " + gameId);
    });
}

function joinGame() {
    if (gameId !== null) {
        stompClient.send("/app/join/" + gameId, {}, JSON.stringify({'username': $("#name").val()}));
    }
}

function showUser(message) {
    $("#games").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#join" ).click(function() { joinGame(); });
});