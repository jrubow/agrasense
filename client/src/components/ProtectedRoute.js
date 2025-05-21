import { Navigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';

const ProtectedRoute = ({ children }) => {
  const { loggedIn } = useUser();

  return loggedIn ? children : <Navigate to="/login" />;
};

export default ProtectedRoute;
