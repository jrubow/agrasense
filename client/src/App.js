import { BrowserRouter, Routes, Route } from "react-router-dom";
import DeployPage from "./pages/DeployPage";
import HomePage from "./pages/HomePage";
import NavBar from "./components/NavBar";



function App() {
  return (
    <div className="navbar-margin">
      <BrowserRouter >
        <NavBar/>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/deploy" element={<DeployPage />} />
          </Routes>
      </BrowserRouter>
    </div>
  )
}

export default App;
