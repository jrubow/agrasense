import React, { useState } from 'react';
import axios from 'axios';
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
            const response = await axios.post('http://localhost:8080/api/account/public/login', {
                email,
                password,
            });

            const token = response.data.token;
            if (token) {
                localStorage.setItem('token', token);
                navigate('/home');
            } else {
                setError('Token not received.');
            }
        } catch (err) {
            if (err.response && err.response.data) {
                setError(
                    typeof err.response.data === 'string'
                        ? err.response.data : err.response.data.message || 'Incorrect email or password.'
                );
            } else {
                setError('Incorrect email or password.');
            }
            console.error('Login error:', err);
        }

        // TODO: Call /api/account/details with the token to retrieve account parameters.

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
