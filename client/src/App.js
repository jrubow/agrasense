import { BrowserRouter, Routes, Route } from "react-router-dom";
import DeployPage from "./pages/DeployPage";
import HomePage from "./pages/HomePage";
import NavBar from "./components/NavBar";
import DevicePage from "./pages/DevicePage"
import MapPage from "./pages/MapPage"


function App() {
  return (
    <div className="navbar-margin">
      <BrowserRouter >
        <NavBar/>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/device" element={<DevicePage/>}/>
            <Route path="/deploy" element={<DeployPage />} />
            <Route path="/map" element={<MapPage/>}/>
          </Routes>
      </BrowserRouter>
    </div>
  )
}

export default App;
