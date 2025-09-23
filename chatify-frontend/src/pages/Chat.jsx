import { useEffect, useState, useRef } from "react";
import api from "../services/api"; // axios instance with JWT interceptor
import { connectWebSocket, subscribeToGroup } from "../services/ws"; // WebSocket client
import "../styles/Chat.css";

function Chat() {
    const [user, setUser] = useState(null);
    const [users, setUsers] = useState([]);
    const [friends, setFriends] = useState([]);
    const [groups, setGroups] = useState([]);       // ✅ my groups
    const [allGroups, setAllGroups] = useState([]); // ✅ all groups
    const [loading, setLoading] = useState(true);

    const [tab, setTab] = useState("friends");
    const [selectedFriend, setSelectedFriend] = useState(null);
    const [selectedGroup, setSelectedGroup] = useState(null);

    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");
    const messagesEndRef = useRef(null);

    // ✅ Auto-scroll when new messages appear
    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    }, [messages]);

    // ✅ Fetch logged-in user & init WebSocket ONCE
    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await api.get("/users/me");
                setUser(response.data);

                connectWebSocket((msg) => {
                    // Direct messages
                    if (
                        selectedFriend &&
                        ((msg.sender?.id === selectedFriend.id && msg.recipient?.id === response.data.id) ||
                            (msg.sender?.id === response.data.id && msg.recipient?.id === selectedFriend.id))
                    ) {
                        setMessages((prev) => [...prev, msg]);
                    }

                    // Group messages
                    if (selectedGroup && msg.groupChat?.id === selectedGroup.id) {
                        setMessages((prev) => [...prev, msg]);
                    }
                });
            } catch (error) {
                console.error("❌ Failed to fetch current user:", error);
            }
        };
        fetchUser();
    }, []); // ✅ only once

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

    // ✅ Fetch my groups
    const fetchGroups = async () => {
        try {
            const response = await api.get("/groupchats/mine");
            setGroups(response.data);
        } catch (error) {
            console.error("❌ Failed to fetch groups:", error);
        }
    };

    // ✅ Fetch all groups
    const fetchAllGroups = async () => {
        try {
            const response = await api.get("/groupchats/all");
            setAllGroups(response.data);
        } catch (error) {
            console.error("❌ Failed to fetch all groups:", error);
        }
    };

    useEffect(() => {
        if (user) {
            fetchFriends();
            fetchGroups();
            fetchAllGroups();
        }
    }, [user]);

    // ✅ Fetch friends
    const fetchFriends = async () => {
        try {
            const response = await api.get("/users/friends");
            setFriends(response.data);
        } catch (error) {
            console.error("❌ Failed to fetch friends:", error);
        }
    };

    // ✅ Add friend
    const handleAddFriend = async (friendId) => {
        try {
            await api.post(`/users/friends/${friendId}`);
            fetchFriends();
        } catch (error) {
            console.error("❌ Failed to add friend:", error);
        }
    };

    // ✅ Remove friend
    const handleRemoveFriend = async (friendId) => {
        try {
            await api.delete(`/users/friends/${friendId}`);
            fetchFriends();
        } catch (error) {
            console.error("❌ Failed to remove friend:", error);
        }
    };

    // ✅ Create group
    const handleCreateGroup = async () => {
        const name = prompt("Enter group name:");
        if (!name) return;
        try {
            await api.post(`/groupchats/create`, { name });
            fetchGroups();
            fetchAllGroups();
        } catch (error) {
            console.error("❌ Failed to create group:", error);
        }
    };

    // ✅ Join group
    const handleJoinGroup = async (groupId) => {
        try {
            await api.post(`/groupchats/${groupId}/join`);
            fetchGroups();
            fetchAllGroups();
        } catch (error) {
            console.error("❌ Failed to join group:", error);
        }
    };

    // ✅ Select friend
    const selectFriend = async (friend) => {
        setSelectedGroup(null);
        setSelectedFriend(friend);
        try {
            const res = await api.get(`/messages/direct/${user.id}/${friend.id}`);
            setMessages(res.data);
        } catch (err) {
            console.error("❌ Failed to load conversation", err);
        }
    };

    // ✅ Select group
    const selectGroup = async (group) => {
        setSelectedFriend(null);
        setSelectedGroup(group);
        setMessages([]);

        try {
            const res = await api.get(`/messages/group/${group.id}`);
            setMessages(res.data);

            subscribeToGroup(group.id, (msg) => {
                if (msg.groupChat?.id === group.id) {
                    setMessages((prev) => [...prev, msg]);
                }
            });
        } catch (err) {
            console.error("❌ Failed to load group messages", err);
        }
    };

    // ✅ Send message
    const handleSend = async () => {
        if (!input.trim() || !user) return;

        try {
            let res;
            if (selectedFriend) {
                res = await api.post(`/messages/direct/send`, {
                    recipientId: selectedFriend.id,
                    content: input,
                });
            } else if (selectedGroup) {
                res = await api.post(`/messages/group/${selectedGroup.id}/send`, {
                    content: input,
                });
            }

            if (res?.data) {
                setMessages((prev) => [...prev, res.data]);
            }

            setInput("");
        } catch (err) {
            console.error("❌ Failed to send message", err);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        window.location.href = "/login";
    };

    const listToShow =
        tab === "friends"
            ? friends
            : tab === "users"
                ? users.filter((u) => u.id !== user?.id)
                : allGroups;

    return (
        <div className="chat-container">
            {/* Sidebar */}
            <div className="sidebar">
                <h3>Chatify</h3>

                <div className="tabs">
                    <button className={tab === "friends" ? "active" : ""} onClick={() => setTab("friends")}>Friends</button>
                    <button className={tab === "users" ? "active" : ""} onClick={() => setTab("users")}>All Users</button>
                    <button className={tab === "groups" ? "active" : ""} onClick={() => setTab("groups")}>Groups</button>
                </div>

                {tab === "groups" && (
                    <button onClick={handleCreateGroup}>➕ Create Group</button>
                )}

                {loading ? (
                    <p>Loading...</p>
                ) : (
                    <ul>
                        {listToShow.map((item) => {
                            if (tab === "groups") {
                                const isMember = groups.some((g) => g.id === item.id);
                                return (
                                    <li
                                        key={item.id}
                                        className={selectedGroup?.id === item.id ? "selected" : ""}
                                    >
                                        <span onClick={() => selectGroup(item)}>{item.name}</span>
                                        {!isMember && <button onClick={() => handleJoinGroup(item.id)}>Join</button>}
                                    </li>
                                );
                            } else {
                                const isFriend = friends.some((f) => f.id === item.id);
                                return (
                                    <li
                                        key={item.id}
                                        className={selectedFriend?.id === item.id ? "selected" : ""}
                                    >
                                        <span onClick={() => selectFriend(item)}>
                                            {item.displayName} (@{item.username})
                                        </span>
                                        {tab === "users" &&
                                            (isFriend ? (
                                                <button onClick={() => handleRemoveFriend(item.id)}>❌</button>
                                            ) : (
                                                <button onClick={() => handleAddFriend(item.id)}>➕</button>
                                            ))}
                                    </li>
                                );
                            }
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

                    {selectedFriend && <h4>💬 Chatting with {selectedFriend.displayName} (@{selectedFriend.username})</h4>}
                    {selectedGroup && <h4>👥 Group: {selectedGroup.name}</h4>}

                    <button onClick={handleLogout} className="logout-btn">Logout</button>
                </div>

                {selectedFriend || selectedGroup ? (
                    <>
                        <div className="messages">
                            {messages.map((m, i) => (
                                <div
                                    key={i}
                                    className={`message-bubble ${m.sender?.id === user?.id ? "sent" : "received"}`}
                                >
                                    {/* Show name only for groups */}
                                    {selectedGroup && <strong>{m.sender?.displayName}:</strong>} {m.content}
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
                    <p className="no-chat">👈 Select a friend or group to start chatting</p>
                )}
            </div>
        </div>
    );
}

export default Chat;
