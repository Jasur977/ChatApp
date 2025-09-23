import SockJS from "sockjs-client";
import { over } from "stompjs";

let stompClient = null;

/**
 * Connect to WebSocket server and set up subscriptions
 */
export const connectWebSocket = (onMessageReceived, onNotification) => {
    const socket = new SockJS("http://localhost:8080/ws");
    stompClient = over(socket);

    const token = localStorage.getItem("token");

    stompClient.connect(
        { Authorization: `Bearer ${token}` }, // ✅ pass JWT in headers
        () => {
            console.log("✅ Connected to WebSocket");

            // Subscribe to private messages (direct chats)
            stompClient.subscribe("/user/queue/messages", (msg) => {
                try {
                    const body = JSON.parse(msg.body);
                    console.log("📩 Private message received:", body);
                    onMessageReceived(body);

                    // 🔔 Notify if user isn’t currently in this chat
                    if (onNotification) onNotification(body);
                } catch (err) {
                    console.error("❌ Error parsing private message:", err);
                }
            });
        },
        (error) => {
            console.error("❌ WebSocket error:", error);
        }
    );
};

/**
 * ✅ Dynamically subscribe to a group topic
 */
export const subscribeToGroup = (groupId, onMessageReceived) => {
    if (!stompClient) {
        console.error("⚠️ STOMP client not connected");
        return;
    }

    stompClient.subscribe(`/topic/group/${groupId}`, (msg) => {
        try {
            const body = JSON.parse(msg.body);
            console.log(`👥 Group #${groupId} message received:`, body);
            onMessageReceived(body);
        } catch (err) {
            console.error("❌ Error parsing group message:", err);
        }
    });
};

/**
 * ✅ Send direct message (matches SendDirectMessageRequest DTO)
 */
export const sendDirectMessage = (recipientId, content) => {
    if (!stompClient) {
        console.error("⚠️ STOMP client not connected");
        return;
    }
    const payload = {
        recipientId: Number(recipientId),
        content: String(content)
    };
    console.log("📤 Sending direct message payload:", payload);
    stompClient.send("/app/direct", {}, JSON.stringify(payload));
};

/**
 * ✅ Send group message (matches SendGroupMessageRequest DTO)
 */
export const sendGroupMessage = (groupId, content) => {
    if (!stompClient) {
        console.error("⚠️ STOMP client not connected");
        return;
    }
    const payload = {
        groupId: Number(groupId),
        content: String(content)
    };
    console.log(`📤 Sending message to group #${groupId}:`, payload);
    stompClient.send("/app/group", {}, JSON.stringify(payload));
};
