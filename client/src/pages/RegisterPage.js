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

    const validatePassword = (password) => {
        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
        return regex.test(password);
    };

    const handleRegister = (e) => {
        e.preventDefault();

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
        console.log('Registering user:', { firstName, lastName, email, password });
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
                    <input
                        type="password"
                        placeholder="Create Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
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