import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../css/pages/accountpage.css';
import {Link, useNavigate} from "react-router-dom";
import { useUser } from '../context/UserContext';

export default function UserInfoPage() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [error, setError] = useState('');

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');

    const [showDeleteOverlay, setShowDeleteOverlay] = useState(false);
    const [showChangeNameOverlay, setShowChangeNameOverlay] = useState(false);
    const [password, setPassword] = useState("");
    const [confirmDelete, setConfirmDelete] = useState(false);

    const [showChangePasswordOverlay, setShowChangePasswordOverlay] = useState(false);
    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');


    useEffect(() => {
        const fetchUser = async () => {
            const token = localStorage.getItem('token');
            if (!token) {
                setError('No token found in localStorage.');
                return;
            }

            try {
                const response = await axios.get('/api/account/details', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setUser(response.data);
            } catch (err) {
                console.error(err);
                setError('Failed to fetch user data.');
            }
        };
        fetchUser();
    }, []);

    useEffect(() => {
        if (user) {
            setFirstName(user.first || '');
            setLastName(user.last || '');
        }
    }, [user]);

    if (error) return <div>{error}</div>;
    if (!user) return <div>Loading user info...</div>;


    const handleDelete = async () => {
        if (!confirmDelete) {
            alert("You must confirm the deletion.");
            return;
        }
        const token = localStorage.getItem('token');
        if (!token) {
            setError('No token found in localStorage.');
            return;
        }
        try {
            const response = await axios.delete("/api/account/delete", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                data: password
            });
            navigate("/login");
        } catch (error) {
            if (error.response) {
                alert(error.response.data);
            } else {
                console.error("Request failed:", error.message);
            }
        }

    };

    const fetchUser = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            setError('No token found in localStorage.');
            return;
        }

        try {
            const response = await axios.get('/api/account/details', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setUser(response.data);
        } catch (err) {
            console.error(err);
            setError('Failed to fetch user data.');
        }
        };


    const handleNameChange = async () => {
        try {
            const response = await axios.post("/api/account/update", {
                type: "name",
                first: firstName,
                last: lastName
            }, {
                headers: {
                    Authorization: `Bearer ${localStorage.token}`,
                    "Content-Type": "application/json"
                }
            });

            console.log(response.data);
            alert("Name updated!");
            handleCloseChangeNameOverlay();
            fetchUser();
        } catch (error) {
            console.error("Failed to update name", error);
            alert(error.response?.data || "An error occurred.");
        }
    };

    const handlePasswordChange = async () => {
        if (!currentPassword || !newPassword || !confirmPassword) {
            alert("Please fill out all fields.");
            return;
        }

        if (newPassword !== confirmPassword) {
            alert("New passwords do not match.");
            return;
        }

        try {
            const response = await axios.post("/api/account/update", {
                type: "password",
                currPassword: currentPassword,
                newPassword: newPassword
            }, {
                headers: {
                    Authorization: `Bearer ${localStorage.token}`,
                    "Content-Type": "application/json"
                }
            });

            alert("Password updated successfully.");
            handleCloseChangePasswordOverlay();
        } catch (error) {
            if (error.response?.data) {
                alert(`Error: ${error.response.data}`);
            } else {
                alert("An error occurred while updating the password.");
            }
        }
    };


    const handleOpenChangeNameOverlay = () => {
        setFirstName(user.first);
        setLastName(user.last);
        setShowChangeNameOverlay(true);
    };

    const handleCloseChangeNameOverlay = () => {
        setFirstName(user.first);
        setLastName(user.last);
        setShowChangeNameOverlay(false);
    };

    const handleOpenChangePasswordOverlay = () => {
        setCurrentPassword('');
        setNewPassword('');
        setConfirmPassword('');
        setShowChangePasswordOverlay(true);
    };

    const handleCloseChangePasswordOverlay = () => {
        setCurrentPassword('');
        setNewPassword('');
        setConfirmPassword('');
        setShowChangePasswordOverlay(false);
    };




    return (
        <div className="settings-container">
            <div className="settings-card">
                <div className="settings-header">
                    <h1>My Account</h1>
                    <button
                        type="button"
                        className="account-button"
                        onClick={() => navigate('/home')}
                    >
                        Home
                    </button>
                </div>
                <h2>Name</h2>
                <div className="setting-section">
                    <p>Display name for this account: <strong>{user.first} {user.last}</strong></p>
                    <button className="account-button" onClick={() => setShowChangeNameOverlay(true)}>
                        Edit Name
                    </button>
                    {showChangeNameOverlay && (
                        <div className="modal-backdrop" onClick={handleCloseChangeNameOverlay}>
                            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                                <h2>Edit Name</h2>
                                <input
                                    type="text"
                                    placeholder="First Name"
                                    className="modal-input"
                                    value={firstName}
                                    onChange={(e) => setFirstName(e.target.value)}
                                />
                                <input
                                    type="text"
                                    placeholder="Last Name"
                                    className="modal-input"
                                    value={lastName}
                                    onChange={(e) => setLastName(e.target.value)}
                                />
                                <button className="account-button" onClick={handleNameChange}>
                                    Save Changes
                                </button>
                                <button className="account-button" onClick={handleCloseChangeNameOverlay}>
                                    Cancel
                                </button>
                            </div>
                        </div>
                    )}

                </div>
                <h2>Email</h2>
                <div className="setting-section">
                    <p>Account login and communication: <strong>{user.email}</strong></p>
                    <button
                        type="button"
                        className="account-button"
                        onClick={() => alert("This feature is not yet implemented.")}
                    >
                        Change Email*
                    </button>
                </div>
                <h2>Security</h2>
                <div className="setting-section">
                    <p>
                        Two-factor authentication via email*
                        <input
                            type="checkbox"
                            checked={user.twoFactor}
                            readOnly
                            style={{ marginLeft: '10px' }}
                        />
                    </p>
                    <>
                        {!user.twoFactor && (
                            <button
                                type="button"
                                className="account-button"
                                onClick={() => alert("This feature is not yet implemented.")}
                            >
                                Set up 2FA*
                            </button>
                        )}
                        <button
                            type="button"
                            className="account-button"
                            onClick={handleOpenChangePasswordOverlay}
                        >
                            Change Password
                        </button>
                        {showChangePasswordOverlay && (
                            <div className="modal-backdrop" onClick={handleCloseChangePasswordOverlay}>
                                <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                                    <h2>Change Password</h2>
                                    <input
                                        type="password"
                                        placeholder="Current Password"
                                        className="modal-input"
                                        value={currentPassword}
                                        onChange={(e) => setCurrentPassword(e.target.value)}
                                    />
                                    <input
                                        type="password"
                                        placeholder="New Password"
                                        className="modal-input"
                                        value={newPassword}
                                        onChange={(e) => setNewPassword(e.target.value)}
                                    />
                                    <input
                                        type="password"
                                        placeholder="Confirm New Password"
                                        className="modal-input"
                                        value={confirmPassword}
                                        onChange={(e) => setConfirmPassword(e.target.value)}
                                    />
                                    <button className="account-button" onClick={handlePasswordChange}>
                                        Save Changes
                                    </button>
                                    <button className="account-button" onClick={handleCloseChangePasswordOverlay}>
                                        Cancel
                                    </button>
                                </div>
                            </div>
                        )}

                    </>

                </div>
                <h2>Other</h2>
                    <div className="setting-section">
                    <button
                        type="button"
                        className="account-button"
                        onClick={() => alert("This feature is not yet implemented.")}
                    >
                        Request Admin Access*
                    </button>
                        <>
                            <button className="delete-account-button" onClick={() => setShowDeleteOverlay(true)}>
                                Delete Account
                            </button>

                            {showDeleteOverlay && (
                                <div className="modal-backdrop" onClick={() => setShowDeleteOverlay(false)}>
                                    <div
                                        className="modal-content"
                                        onClick={(e) => e.stopPropagation()}
                                    >
                                        <h2>Confirm Account Deletion</h2>
                                        <input
                                            type="password"
                                            placeholder="Password"
                                            className="modal-input"
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                        />
                                        <label className="modal-checkbox">
                                            <input
                                                type="checkbox"
                                                checked={confirmDelete}
                                                onChange={(e) => setConfirmDelete(e.target.checked)}
                                            />
                                            I understand that account deletion is permanent
                                        </label>
                                            <button className="delete-account-button" onClick={handleDelete}>
                                                Delete Account
                                            </button>
                                            <button className="account-button" onClick={() => setShowDeleteOverlay(false)}>
                                                Cancel
                                            </button>

                                    </div>
                                </div>
                            )}
                        </>
                </div>
                <p></p>
                <button
                    type="button"
                    className="account-button"
                    id="logout-button"
                    onClick={() => navigate('/login')}
                >
                    Log Out
                </button>
            </div>
        </div>
    );
}
