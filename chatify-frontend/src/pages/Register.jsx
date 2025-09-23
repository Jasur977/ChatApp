import { useState } from "react";
import api from "../services/api";
import "../styles/auth.css";

function Register() {
    const [username, setUsername] = useState("");
    const [displayName, setDisplayName] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await api.post("/auth/register", { username, displayName, password });
            setMessage("✅ Registration successful! You can now login.");
        } catch (err) {
            setMessage("❌ Failed to register. Try again.");
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-box">
                <h2>📝 Register for Chatify</h2>
                {message && <p className="message">{message}</p>}
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                    <input
                        type="text"
                        placeholder="Display Name"
                        value={displayName}
                        onChange={(e) => setDisplayName(e.target.value)}
                        required
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    <button type="submit">Register</button>
                </form>
                <p>
                    Already have an account? <a href="/login">Login</a>
                </p>
            </div>
        </div>
    );
}

export default Register;
