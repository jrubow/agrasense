import { BrowserRouter, Routes, Route } from "react-router-dom";
import DeployPage from "./pages/DeployPage";
import HomePage from "./pages/HomePage";
import NavBar from "./components/NavBar";
import DevicePage from "./pages/DevicePage"
import MapPage from "./pages/MapPage"
import LandingPage from "./pages/LandingPage"


function App() {
  return (
      <BrowserRouter >
        {/* <NavBar/> */}
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/analytics" element={<HomePage />} />
            <Route path="/device" element={<DevicePage/>}/>
            <Route path="/deploy" element={<DeployPage />} />
            <Route path="/map" element={<MapPage/>}/>
          </Routes>
      </BrowserRouter>
  )
}

export default App;
