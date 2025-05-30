import React, { useState } from 'react';
import '../css/pages/registerpage.css';
import { Link, useNavigate } from 'react-router-dom';

export default function RegisterPage() {
    const navigate = useNavigate();
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [acceptTerms, setAcceptTerms] = useState(false);
    const [showPassword, setShowPassword] = useState(false);


    const validatePassword = (password) => {
        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
        return regex.test(password);
    };

    const handleRegister = async (e) => {
        e.preventDefault();

        // Frontend validation
        if (!firstName || !lastName || !email || !password || !confirmPassword) {
            setError('Please fill out all fields.');
            return;
        }

        if (password !== confirmPassword) {
            setError('Passwords do not match.');
            return;
        }

        if (!validatePassword(password)) {
            setError(
                'Password must be at least 8 characters and include a number, special character, uppercase and lowercase letter.'
            );
            return;
        }

        if (!acceptTerms) {
            setError('You must accept the Terms and Conditions.');
            return;
        }

        setError('');

        try {
            const response = await fetch('http://localhost:8080/api/account/public/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    first: firstName,
                    last: lastName,
                    email: email,
                    password: password,
                }),
            });

            const text = await response.text();

            if (response.status === 201) {
                alert(text);
                navigate('/login');
            } else {
                setError(text);
            }
        } catch (err) {
            console.error('Registration error:', err);
            setError('Something went wrong. Please try again.');
        }
    };

    return (
        <div className="register-container">
            <form onSubmit={handleRegister} className="register-form">
                <div className="logo-header">
                    <Link to="/">
                        <img src="/favicon.ico" style={{ height: "50px" }} alt="logo" />
                    </Link>
                </div>

                <h2>Register</h2>

                {error && <p className="error-text">{error}</p>}

                <div className="form-group">
                    <input
                        type="text"
                        placeholder="First Name"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="Last Name"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                    />
                    <br />
                    <br />
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <br />
                    <br />
                    <div style={{ position: "relative" }}>
                        <input
                            type={showPassword ? "text" : "password"}
                            placeholder="Create Password"
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

                    <input
                        type="password"
                        placeholder="Confirm Password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                    />
                </div>
                <div className="terms-container">
                    <input
                        type="checkbox"
                        id="acceptTerms"
                        checked={acceptTerms}
                        onChange={(e) => setAcceptTerms(e.target.checked)}
                    />
                    <label htmlFor="acceptTerms">
                        I accept <a href="/terms" target="_blank" rel="noopener noreferrer">Terms and Conditions</a>
                    </label>
                </div>
                <br />
                <button className="register-button">
                    Register
                </button>

                <div className="bottom-buttons">
                    <button
                        type="button"
                        className="bottom-button"
                        onClick={() => navigate('/login')}
                    >
                        Login
                    </button>
                    <button
                        type="button"
                        className="bottom-button"
                        onClick={() => navigate('/privacy')}
                    >
                        Privacy Policy
                    </button>
                </div>
            </form>
        </div>
    );
}