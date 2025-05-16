import React, { useState } from 'react';
import '../css/pages/loginpage.css';
import {Link, useNavigate} from "react-router-dom";

export default function LoginPage() {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = (e) => {
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
        console.log('Logging in with:', { email, password });

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
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>

                <button className="login-button">Login</button>

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
