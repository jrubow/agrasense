import { BrowserRouter, Routes, Route } from "react-router-dom";
import DeployPage from "./pages/DeployPage";
import HomePage from "./pages/HomePage";
import NavBar from "./components/NavBar";
import DevicePage from "./pages/DevicePage"
import MapPage from "./pages/MapPage"
import LandingPage from "./pages/LandingPage"
import Newsroom from "./pages/Newsroom"
import ArticlePage from "./pages/ArticlePage"


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
            <Route path="/newsroom" element={<Newsroom/>}/>
             <Route path="/newsroom/:articleSlug" element={<ArticlePage />} />
          </Routes>
      </BrowserRouter>
  )
}

export default App;
