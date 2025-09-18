import { useState } from "react";
import api from "../services/api";
import { useNavigate } from "react-router-dom"; // ✅ for redirect

function Login() {
    const [formData, setFormData] = useState({
        username: "",
        password: "",
    });

    const [message, setMessage] = useState("");
    const navigate = useNavigate(); // ✅ React Router hook

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault(); // ✅ stop default GET form action
        try {
            const response = await api.post("/auth/login", formData); // ✅ POST request
            setMessage(response.data.message || "Login successful! 🎉");

            // ✅ Save JWT token for future requests
            localStorage.setItem("token", response.data.token);

            // ✅ Redirect to chat after login
            navigate("/chat");
        } catch (error) {
            setMessage(error.response?.data?.message || "❌ Login failed");
            console.error("Error:", error);
        }
    };

    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleSubmit} method="post">
                <input
                    type="text"
                    name="username"
                    placeholder="Username"
                    value={formData.username}
                    onChange={handleChange}
                /><br />

                <input
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={formData.password}
                    onChange={handleChange}
                /><br />

                <button type="submit">Login</button>
            </form>

            {message && <p>{message}</p>}
        </div>
    );
}

export default Login;
