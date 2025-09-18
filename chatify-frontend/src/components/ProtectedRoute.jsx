import { Navigate } from "react-router-dom";

function ProtectedRoute({ children }) {
    const token = localStorage.getItem("token"); // ✅ check if token exists

    if (!token) {
        // ❌ no token = not logged in → redirect to login
        return <Navigate to="/login" replace />;
    }

    // ✅ token exists → allow access
    return children;
}

export default ProtectedRoute;
