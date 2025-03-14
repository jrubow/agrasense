import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios"; // Import axios for making HTTP requests
import "../css/components/form.css";

const Form = ({
  title = "Form",
  fields = [],
  additionalFields = [], // Array of extra fields to merge into the payload (e.g., [{ admin_username: user.username }])
  postRoute,
  submitButtonText = "Submit",
  redirect="/home",
  onSubmit, // Optional: custom function to handle form submission
  links,    // Optional: Array of { text, href } objects for extra links (e.g., "Create Account")
  terms     // Optional: Array of { text, href } objects for terms (e.g., "Terms", "Privacy Policy")
}) => {
  // Build initial state for form fields from the provided 'fields' prop.
  const initialState = fields.reduce((acc, field) => {
    acc[field.name] = "";
    return acc;
  }, {});

  // Import Naviage
  const navigate = useNavigate()

  // State to hold form input values, error messages, and password visibility.
  const [formData, setFormData] = useState(initialState);
  const [errorMessage, setErrorMessage] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [success, setSuccess] = useState(null)

  // Update formData when an input changes.
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // Handle form submission.
  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent the default form submission behavior

    // If a custom onSubmit function is provided, use it and exit.
    if (onSubmit) {
      onSubmit(formData);
      return;
    }

    // Create a copy of formData to avoid mutating the state directly.
    const processedData = { ...formData };

    // For any field that is a number, cast its value to an integer.
    fields.forEach((field) => {
      if (field.type === "number") {
        processedData[field.name] = parseInt(processedData[field.name], 10);
      }
    });

    // Merge additionalFields into processedData.
    // Ensure each additional field is an object with a single key-value pair.
    console.log(additionalFields)
    additionalFields.forEach((field) => {
      const [key, value] = Object.entries(field)[0];
      processedData[key] = value;
    });

    // Debug: Log the processed data to verify additional fields are merged.
    console.log("Processed Data:", processedData);

    // If a postRoute is provided, send the processedData using axios.
    if (postRoute) {
      try {
        const res = await axios.post(
          postRoute,
          processedData,
          {
            headers: { "X-API-KEY": "user", "Content-Type": "application/json" }
          }
        );
        console.log( res.data);
        // Optionally, handle the response here (e.g., clear the form or display a success message)
        setSuccess(res.data)
        setTimeout(() => {
            navigate(redirect)
        }, 3000);
      } catch (err) {
        setErrorMessage(err.response?.data);
      }
    }
  };

  return (
    <div className="container">
      <div className="login-card">
        { success == null ? 
        <>
            {/* Logo Section */}
            {/* <div className="logo-container">
            <img src="/.png" alt="Logo" className="logo" />
            </div> */}

            {/* Form Title */}
            <h2 className="title">{title}</h2>

            {/* Display error message if any */}
            {errorMessage && <p className="error-message">{errorMessage}</p>}

            {/* Form element */}
            <form onSubmit={handleSubmit} className="components-form">
            {fields.map((field) => (
                <div
                key={field.name}
                className={field.type === "password" ? "password-container" : ""}
                >
                <input
                    type={
                    field.type === "password"
                        ? showPassword
                        ? "text"
                        : "password"
                        : field.type
                    }
                    placeholder={field.placeholder}
                    name={field.name}
                    value={formData[field.name]}
                    onChange={handleChange}
                    className="input-field"
                />
                {field.type === "password" && (
                    <button
                    type="button"
                    className="show-password-btn"
                    onClick={() => setShowPassword(!showPassword)}
                    >
                    {showPassword ? "Hide" : "Show"}
                    </button>
                )}
                </div>
            ))}
            {/* Submit button */}
            <button type="submit" className="button">
                {submitButtonText}
            </button>
            </form>

            {/* Render additional links if provided */}
            {links && links.length > 0 && (
            <div className="links">
                {links.map((link, index) => (
                <span key={index}>
                    <a href={link.href}>{link.text}</a>
                    {index < links.length - 1 && " | "}
                </span>
                ))}
            </div>
            )}

            {/* Render terms if provided */}
            {terms && terms.length > 0 && (
            <div className="terms">
                {terms.map((term, index) => (
                <span key={index}>
                    <a href={term.href}>{term.text}</a>
                    {index < terms.length - 1 && " | "}
                </span>
                ))}
            </div>
            )}
        </> :
        <>
            <p className="success-message">{success}</p>
            <p className="redirect-message">... Redirecting to {redirect}</p>
        </>
    }
      </div>
    </div>
  );
};

export default Form;
