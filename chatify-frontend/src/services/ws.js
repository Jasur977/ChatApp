import SockJS from "sockjs-client";
import { over } from "stompjs";

let stompClient = null;

/**
 * Connect to WebSocket server and set up subscriptions
 */
export const connectWebSocket = (onMessageReceived) => {
    const socket = new SockJS("http://localhost:8080/ws");
    stompClient = over(socket);

    const token = localStorage.getItem("token");

    stompClient.connect(
        { Authorization: `Bearer ${token}` }, // ✅ pass JWT in headers
        () => {
            console.log("✅ Connected to WebSocket");

            // Subscribe to private messages
            stompClient.subscribe("/user/queue/messages", (msg) => {
                try {
                    const body = JSON.parse(msg.body);
                    console.log("📩 Private message received:", body);
                    onMessageReceived(body);
                } catch (err) {
                    console.error("❌ Error parsing private message:", err);
                }
            });

            // Example: Subscribe to group chat #1
            stompClient.subscribe("/topic/group/1", (msg) => {
                try {
                    const body = JSON.parse(msg.body);
                    console.log("👥 Group message received:", body);
                    onMessageReceived(body);
                } catch (err) {
                    console.error("❌ Error parsing group message:", err);
                }
            });
        },
        (error) => {
            console.error("❌ WebSocket error:", error);
        }
    );
};

/**
 * ✅ Send direct message (matches SendDirectMessageRequest DTO)
 * @param {number} recipientId - ID of the recipient user
 * @param {string} content - Message text
 */
export const sendDirectMessage = (recipientId, content) => {
    if (!stompClient) {
        console.error("⚠️ STOMP client not connected");
        return;
    }
    const payload = {
        recipientId: Number(recipientId), // ensure it's a number
        content: String(content)          // ensure it's a string
    };
    console.log("📤 Sending direct message payload:", payload);
    stompClient.send("/app/direct", {}, JSON.stringify(payload));
};

/**
 * ✅ Send group message (matches SendGroupMessageRequest DTO)
 * @param {number} groupId - ID of the group chat
 * @param {string} content - Message text
 */
export const sendGroupMessage = (groupId, content) => {
    if (!stompClient) {
        console.error("⚠️ STOMP client not connected");
        return;
    }
    const payload = {
        groupId: Number(groupId),   // ensure it's a number
        content: String(content)    // ensure it's a string
    };
    console.log("📤 Sending group message payload:", payload);
    stompClient.send("/app/group", {}, JSON.stringify(payload));
};
