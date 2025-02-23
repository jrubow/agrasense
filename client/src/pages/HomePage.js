import React from "react";
import { Line } from "react-chartjs-2";
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from "chart.js";

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

const chartData = (label) => ({
  labels: ["Jan", "Feb", "Mar", "Apr", "May"],
  datasets: [
    {
      label,
      data: Array.from({ length: 5 }, () => Math.floor(Math.random() * 100)),
      borderColor: "rgba(75,192,192,1)",
      backgroundColor: "rgba(75,192,192,0.2)",
    },
  ],
});

const options = {
  responsive: true,
  maintainAspectRatio: false,
};

function HomePage() {
  return (
    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "20px", padding: "20px", height: "100vh" }}>
      <div style={{ height: "300px" }}>
        <h3> Average Temperature</h3>
        <Line data={chartData("Temperature")} options={options} />
      </div>
      <div style={{ height: "300px" }}>
        <h3>Average Humidity</h3>
        <Line data={chartData("Humidity")} options={options} />
      </div>
      <div style={{ height: "300px" }}>
        <h3>Average Light</h3>
        <Line data={chartData("Light")} options={options} />
      </div>
      <div style={{ height: "300px" }}>
        <h3>Average Soil Moisture</h3>
        <Line data={chartData("Soil")} options={options} />
      </div>
    </div>
  );
}

export default HomePage;
