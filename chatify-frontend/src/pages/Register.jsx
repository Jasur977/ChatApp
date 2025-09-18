import { useState } from "react";
import api from "../services/api";

function Register() {
    const [formData, setFormData] = useState({
        username: "",
        password: "",
        displayName: "",
    });

    const [message, setMessage] = useState("");

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await api.post("/auth/register", formData);
            setMessage(response.data.message || "Registered successfully!");
            console.log("✅ Server response:", response.data);
        } catch (error) {
            setMessage(error.response?.data?.message || "❌ Registration failed");
            console.error("Error:", error);
        }
    };

    return (
        <div>
            <h2>Register</h2>
            <form onSubmit={handleSubmit} method="post">
                <input
                    type="text"
                    name="username"
                    placeholder="Username"
                    value={formData.username}
                    onChange={handleChange}
                /><br />

                <input
                    type="text"
                    name="displayName"
                    placeholder="Display Name (optional)"
                    value={formData.displayName}
                    onChange={handleChange}
                /><br />

                <input
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={formData.password}
                    onChange={handleChange}
                /><br />

                <button type="submit">Register</button>
            </form>

            {message && <p>{message}</p>}
        </div>
    );
}

export default Register;
