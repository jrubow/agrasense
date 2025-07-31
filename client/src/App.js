// Import React Router components for client-side routing
import { BrowserRouter, Routes, Route } from "react-router-dom";

// Import context providers for global state management
import UserProvider from './context/UserContext'; // Provides user authentication state
import { ArticlesProvider } from "./pages/ArticlesContext"; // Provides article data and dark mode state

// Import page components
import DeployPage from "./pages/DeployPage"; // Device deployment page
import HomePage from "./pages/HomePage"; // Main dashboard page (protected)
import AnalyticsPage from "./pages/AnalyticsPage"; // Data analytics and visualization
import NavBar from "./components/NavBar"; // Navigation component (currently commented out)
import DevicePage from "./pages/DevicePage"; // Device management page
import MapPage from "./pages/MapPage"; // Geographic visualization of devices
import LandingPage from "./pages/LandingPage"; // Public landing page
import LoginPage from "./pages/LoginPage"; // User authentication page
import RegisterPage from "./pages/RegisterPage"; // User registration page
import ProtectedRoute from './components/ProtectedRoute'; // Component for protecting authenticated routes
import Newsroom from "./pages/Newsroom"; // Article listing page
import ArticlePage from "./pages/ArticlePage"; // Individual article view
import AdminNewsroom from "./pages/AdminNewsroom"; // Admin interface for managing articles

/**
 * Main App component that sets up routing and global context providers
 * for the AgraSense agricultural monitoring application
 */
function App() {
  return (
    // Wrap the entire app with context providers for global state
    <UserProvider>
      <ArticlesProvider>
        <BrowserRouter>
          {/* Navigation bar is commented out - likely handled by individual pages */}
          {/* <NavBar/> */}
          <Routes>
            {/* Public routes */}
            <Route path="/" element={<LandingPage />} />
            <Route path="/newsroom" element={<Newsroom />} />
            <Route path="/article/:slug" element={<ArticlePage />} />
            <Route path="/admin/newsroom" element={<AdminNewsroom />} />
            <Route path="/analytics" element={<AnalyticsPage />} />
            
            {/* Protected route - requires authentication */}
            <Route path="/home" element={<ProtectedRoute><HomePage /></ProtectedRoute>} />
            
            {/* Device and monitoring related pages */}
            <Route path="/device" element={<DevicePage />} />
            <Route path="/deploy" element={<DeployPage />} />
            <Route path="/map" element={<MapPage />} />
            
            {/* Authentication routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            
            {/* Legal pages with inline components */}
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
      </ArticlesProvider>
    </UserProvider>
  );
}

export default App;
