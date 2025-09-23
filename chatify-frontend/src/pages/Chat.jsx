import { useEffect, useState, useRef } from "react";
import api from "../services/api";
import { connectWebSocket, sendDirectMessage } from "../services/ws";
import "../styles/Chat.css";

function Chat() {
    const [user, setUser] = useState(null);
    const [users, setUsers] = useState([]);
    const [friends, setFriends] = useState([]);
    const [loading, setLoading] = useState(true);

    const [tab, setTab] = useState("friends"); // "friends" | "users"
    const [selectedFriend, setSelectedFriend] = useState(null);
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");
    const messagesEndRef = useRef(null);

    // ✅ Scroll to bottom when new messages arrive
    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    }, [messages]);

    // ✅ Fetch logged-in user
    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await api.get("/users/me");
                setUser(response.data);

                // Connect WebSocket once user is known
                connectWebSocket((msg) => {
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

    // ✅ Fetch my friends
    const fetchFriends = async () => {
        if (!user) return;
        try {
            const response = await api.get(`/users/${user.id}/friends`);
            setFriends(response.data);
        } catch (error) {
            console.error("❌ Failed to fetch friends:", error);
        }
    };

    useEffect(() => {
        if (user) fetchFriends();
    }, [user]);

    // ✅ Add friend
    const handleAddFriend = async (friendId) => {
        try {
            await api.post(`/users/${user.id}/add-friend/${friendId}`);
            fetchFriends();
        } catch (error) {
            console.error("❌ Failed to add friend:", error);
        }
    };

    // ✅ Remove friend
    const handleRemoveFriend = async (friendId) => {
        try {
            await api.delete(`/users/${user.id}/remove-friend/${friendId}`);
            fetchFriends();
        } catch (error) {
            console.error("❌ Failed to remove friend:", error);
        }
    };

    // ✅ Send message
    const handleSend = () => {
        if (!input.trim() || !selectedFriend || !user) return;
        sendDirectMessage(selectedFriend.id, input);
        setInput("");
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        window.location.href = "/login";
    };

    const listToShow =
        tab === "friends"
            ? friends
            : users.filter((u) => u.id !== user?.id);

    return (
        <div className="chat-container">
            {/* Sidebar */}
            <div className="sidebar">
                <h3>Chatify</h3>

                <div className="tabs">
                    <button
                        className={tab === "friends" ? "active" : ""}
                        onClick={() => setTab("friends")}
                    >
                        Friends
                    </button>
                    <button
                        className={tab === "users" ? "active" : ""}
                        onClick={() => setTab("users")}
                    >
                        All Users
                    </button>
                </div>

                {loading ? (
                    <p>Loading...</p>
                ) : (
                    <ul>
                        {listToShow.map((u) => {
                            const isFriend = friends.some((f) => f.id === u.id);
                            return (
                                <li
                                    key={u.id}
                                    className={selectedFriend?.id === u.id ? "selected" : ""}
                                >
                                    <span onClick={() => setSelectedFriend(u)}>
                                        {u.displayName} (@{u.username})
                                    </span>
                                    {tab === "users" && (
                                        isFriend ? (
                                            <button onClick={() => handleRemoveFriend(u.id)}>
                                                ❌
                                            </button>
                                        ) : (
                                            <button onClick={() => handleAddFriend(u.id)}>
                                                ➕
                                            </button>
                                        )
                                    )}
                                </li>
                            );
                        })}
                    </ul>
                )}
            </div>

            {/* Chat area */}
            <div className="chat-area">
                <div className="chat-header">
                    {user ? (
                        <p>
                            Logged in as <strong>{user.displayName}</strong> (@{user.username})
                        </p>
                    ) : (
                        <p>Loading user...</p>
                    )}
                    <button onClick={handleLogout} className="logout-btn">
                        Logout
                    </button>
                </div>

                {selectedFriend ? (
                    <>
                        <div className="messages">
                            {messages
                                .filter(
                                    (m) =>
                                        (m.sender?.id === user?.id &&
                                            m.recipient?.id === selectedFriend.id) ||
                                        (m.sender?.id === selectedFriend.id &&
                                            m.recipient?.id === user?.id)
                                )
                                .map((m, i) => (
                                    <div
                                        key={i}
                                        className={`message-bubble ${
                                            m.sender?.id === user?.id ? "sent" : "received"
                                        }`}
                                    >
                                        {m.content}
                                    </div>
                                ))}
                            <div ref={messagesEndRef} />
                        </div>

                        <div className="input-area">
                            <input
                                type="text"
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                                placeholder="Type a message..."
                            />
                            <button onClick={handleSend}>Send</button>
                        </div>
                    </>
                ) : (
                    <p className="no-chat">👈 Select a friend to start chatting</p>
                )}
            </div>
        </div>
    );
}

export default Chat;
