import { useEffect, useState } from "react";
import api from "../services/api";
import { connectWebSocket, sendDirectMessage } from "../services/ws";

function Chat() {
    const [user, setUser] = useState(null);
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [systemMessage, setSystemMessage] = useState("");

    const [selectedFriend, setSelectedFriend] = useState(null);
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");

    // ✅ Fetch logged-in user
    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await api.get("/users/me");
                setUser(response.data);

                // Connect WebSocket once user is known
                connectWebSocket((msg) => {
                    // Only keep messages relevant to the current user
                    if (
                        msg.sender?.id === response.data.id ||
                        msg.recipient?.id === response.data.id
                    ) {
                        setMessages((prev) => [...prev, msg]);
                    }
                });
            } catch (error) {
                console.error("❌ Failed to fetch current user:", error);
            }
        };
        fetchUser();
    }, []);

    // ✅ Fetch all users
    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await api.get("/users");
                setUsers(response.data);
            } catch (error) {
                console.error("❌ Failed to fetch users:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchUsers();
    }, []);

    // ✅ Send message to selected friend
    const handleSend = () => {
        if (!input.trim() || !selectedFriend || !user) return;

        const message = {
            sender: { id: user.id, username: user.username },
            recipient: { id: selectedFriend.id, username: selectedFriend.username },
            content: input,
        };

        sendDirectMessage(message);
        setMessages((prev) => [...prev, message]); // optimistic update
        setInput("");
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        window.location.href = "/login";
    };

    return (
        <div style={{ display: "flex", height: "100vh" }}>
            {/* Sidebar */}
            <div style={{ width: "250px", background: "#f5f5f5", padding: "1rem" }}>
                <h3>Friends / Users</h3>
                {loading ? (
                    <p>Loading users...</p>
                ) : (
                    <ul>
                        {users
                            .filter((u) => user && u.id !== user.id) // hide self
                            .map((u) => (
                                <li
                                    key={u.id}
                                    style={{
                                        marginBottom: "0.5rem",
                                        cursor: "pointer",
                                        fontWeight: selectedFriend?.id === u.id ? "bold" : "normal",
                                    }}
                                    onClick={() => setSelectedFriend(u)}
                                >
                                    {u.displayName} (@{u.username})
                                </li>
                            ))}
                    </ul>
                )}
            </div>

            {/* Main Chat Area */}
            <div style={{ flex: 1, padding: "1rem" }}>
                <h2>Welcome to Chatify 🎉</h2>
                {user ? (
                    <p>
                        Logged in as <strong>{user.displayName}</strong> (@{user.username})
                    </p>
                ) : (
                    <p>Loading user info...</p>
                )}

                {systemMessage && <p>{systemMessage}</p>}

                <button onClick={handleLogout}>Logout</button>

                {selectedFriend ? (
                    <div style={{ marginTop: "2rem" }}>
                        <h3>Chatting with {selectedFriend.displayName}</h3>
                        <div
                            style={{
                                border: "1px solid #ccc",
                                height: "300px",
                                overflowY: "scroll",
                                marginBottom: "1rem",
                                padding: "0.5rem",
                            }}
                        >
                            {messages
                                .filter(
                                    (m) =>
                                        (m.sender?.id === user?.id &&
                                            m.recipient?.id === selectedFriend.id) ||
                                        (m.sender?.id === selectedFriend.id &&
                                            m.recipient?.id === user?.id)
                                )
                                .map((m, i) => (
                                    <p key={i}>
                                        <strong>{m.sender?.username}:</strong> {m.content}
                                    </p>
                                ))}
                        </div>

                        <input
                            type="text"
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            placeholder="Type a message..."
                        />
                        <button onClick={handleSend}>Send</button>
                    </div>
                ) : (
                    <p style={{ marginTop: "2rem" }}>👈 Select a friend to start chatting</p>
                )}
            </div>
        </div>
    );
}

export default Chat;
