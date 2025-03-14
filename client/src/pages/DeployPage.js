import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Form from "../components/Form"

function DeployPage() {
  const navigate = useNavigate();

  const fields = [
    { name: "device_id", placeholder: "Device ID", type: "text" },
    { name: "password", placeholder: "Device Password", type: "password" },
  ];

  const links = [
    { text: "Back to Home", href: "/home" }
  ];

  return (
    <div className="deployPage">
      <Form 
        title="Claim Device"
        fields={fields}
        // additionalFields={additionalFields}
        postRoute="/api/devices/sentinel/claim"
        submitButtonText="Claim!"
        links={links}
        redirect="/"/>
    </div>
  );
}

export default DeployPage;
