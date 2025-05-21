import { BrowserRouter, Routes, Route } from "react-router-dom";
import UserProvider from './context/UserContext';
import DeployPage from "./pages/DeployPage";
import HomePage from "./pages/HomePage";
import AnalyticsPage from "./pages/AnalyticsPage"
import NavBar from "./components/NavBar";
import DevicePage from "./pages/DevicePage"
import MapPage from "./pages/MapPage"
import LandingPage from "./pages/LandingPage"
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import ProtectedRoute from './components/ProtectedRoute';


function App() {
  return (
    <UserProvider>
      <BrowserRouter >
        {/* <NavBar/> */}
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/analytics" element={<AnalyticsPage />} />
            <Route path="/home" element={<ProtectedRoute><HomePage /></ProtectedRoute>} />
            <Route path="/device" element={<DevicePage/>}/>
            <Route path="/deploy" element={<DeployPage />} />
            <Route path="/map" element={<MapPage/>}/>
            <Route path="/login" element={<LoginPage/>}/>
            <Route path="/register" element={<RegisterPage/>}/>
            <Route path="/terms"
                    element={
                        <div>
                            <h1>Terms and Conditions</h1>
                        </div>
                    }
            />
            <Route path="/privacy"
                    element={
                        <div>
                            <h1>Privacy Policy</h1>
                        </div>
                    }
          />
          </Routes>
      </BrowserRouter>
    </UserProvider>
  )
}

export default App;
