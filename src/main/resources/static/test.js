let stompClient = null;

let connect = () => {
    let socket = new SockJS('http://localhost:8080/ws');
    stompClient = Stomp.over(socket);

    let jwtToken = 'Bearer eyJhbGciOiJIUzM4NCJ9.eyJ0b2tlbl9yb2xlIjoiQUNDRVNTX1RPS0VOIiwic3ViIjoidGVzdHJlY2VpdmVyIiwiaWF0IjoxNzI1OTIzMzM3LCJleHAiOjE3MjU5MjQyMzd9.WOdIeyPTfAmLThRWot59LOht-cMjxlU28Imvni_qr3j5VRnNMfdRV9WGmxJBIM9D';

    const headers = {
        'Authorization': jwtToken
    }

    stompClient.connect(headers, onConnected, onError);
}

let onConnected = options => {
    stompClient.subscribe('/user/queue/notification', onMessageReceived);

}

let onError = () => {
    console.log("ERROR OCCURED");
}

let onMessageReceived = (payload) => {
    console.log(JSON.parse(payload.body));
}