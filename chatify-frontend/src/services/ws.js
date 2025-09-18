import SockJS from "sockjs-client";
import { over } from "stompjs";

let stompClient = null;

export const connectWebSocket = (onMessageReceived) => {
    const socket = new SockJS("http://localhost:8080/ws"); // ✅ correct



    stompClient = over(socket);

    stompClient.connect({}, () => {
        console.log("✅ Connected to WebSocket");

        // Subscribe to private messages
        stompClient.subscribe("/user/queue/messages", (msg) => {
            const body = JSON.parse(msg.body);
            console.log("📩 Private message received:", body);
            onMessageReceived(body);
        });

        // Example: subscribe to a group chat
        stompClient.subscribe("/topic/group/1", (msg) => {
            const body = JSON.parse(msg.body);
            console.log("👥 Group message received:", body);
            onMessageReceived(body);
        });
    });
};

export const sendDirectMessage = (message) => {
    if (stompClient) {
        stompClient.send("/app/direct", {}, JSON.stringify(message));
    }
};

export const sendGroupMessage = (message) => {
    if (stompClient) {
        stompClient.send("/app/group", {}, JSON.stringify(message));
    }
};
