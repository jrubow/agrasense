import React, { useState } from 'react';
import '../css/pages/loginpage.css';
import {Link, useNavigate} from "react-router-dom";

export default function LoginPage() {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [showPassword, setShowPassword] = useState(false);


    const handleLogin = async (e) => {
        e.preventDefault();

        if (!email || !password) {
            setError('Both fields are required');
            return;
        }

        if (!/\S+@\S+\.\S+/.test(email)) {
            setError('Invalid email format');
            return;
        }

        setError('');

        try {
            const response = await fetch('http://localhost:8080/api/account/public/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password }),
            });

            if (response.ok) {
                const token = await response.text();
                localStorage.setItem('token', token);
                navigate('/home');
            } else {
                const errorMsg = await response.text();
                setError(errorMsg || 'Incorrect email or password.');
            }
        } catch (err) {
            setError('Server not reachable.');
            console.error('Login error:', err);
        }
    };


    return (
        <div className="login-container">

            <form onSubmit={handleLogin} className="login-form">
                <div className="logo-header">
                    <Link to="/">
                        <img src="/favicon.ico" style={{ height: "50px" }} />
                    </Link>
                </div>
                <h2>Login</h2>

                {error && <p className="error-text">{error}</p>}

                <div className="form-group">
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <div style={{ position: "relative" }}>
                        <input
                            type={showPassword ? "text" : "password"}
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            style={{ paddingRight: "80px" }}
                        />
                        <button
                            type="button"
                            onClick={() => setShowPassword(!showPassword)}
                            style={{
                                position: "absolute",
                                right: "20px",
                                top: "50%",
                                transform: "translateY(-50%)",
                                background: "none",
                                border: "none",
                                cursor: "pointer",
                                fontSize: "0.9em",
                                textAlign: "center"
                            }}
                        >
                            {showPassword ? "•••" : "abc"}
                        </button>
                    </div>
                </div>

                <button className="login-button" onClick={handleLogin}>Login</button>

                <div className="bottom-buttons">
                    <button
                        type="button"
                        className="bottom-button"
                        onClick={() => navigate('/register')}
                    >
                        Register
                    </button>
                    <button
                        type="button"
                        className="bottom-button"
                        onClick={() => alert('To be implemented soon.')}
                    >
                        Forgot Password
                    </button>
                </div>
            </form>
        </div>
    );
}
