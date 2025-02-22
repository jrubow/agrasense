import { BrowserRouter, Routes, Route } from "react-router-dom";
import DeployPage from "./pages/DeployPage";
import HomePage from "./pages/HomePage";

function App() {
  return (
    <BrowserRouter>
      <NavBar/>
      <div className="navbar-margin">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/" element={<DeployPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  )
}

export default App;
