import { createContext, useState, useEffect, useContext } from "react";

export const UserContext = createContext(null);

const UserProvider = ({ children }) => {
  const storedUser = localStorage.getItem("user");
  const storedLoggedIn = localStorage.getItem("loggedIn");



  const defaultUser = {
    email: "",
    id: "",
    username: "",
    firstName: "",
    lastName: "",
    password: "",
    address: "",
    phoneNumber: 0,
    shareLocation: 0,
    twoFactor: 0
  };

  let parsedUser = defaultUser;
  try {
    parsedUser = storedUser ? JSON.parse(storedUser) : defaultUser;
  } catch (e) {
    console.error("Error parsing user from localStorage:", e);
  }

  const [user, setUser] = useState(parsedUser);
  const [loggedIn, setLoggedIn] = useState(storedLoggedIn === "true");

  useEffect(() => {
    localStorage.setItem("user", JSON.stringify(user));
  }, [user]);

  useEffect(() => {
    localStorage.setItem("loggedIn", loggedIn);
  }, [loggedIn]);

    localStorage.setItem("loggedIn", "true");
localStorage.setItem("user", JSON.stringify({
  email: "test@example.com",
  id: "1",
  username: "testuser",
  firstName: "Test",
  lastName: "User",
  password: "secret",
  address: "123 Test St",
  phoneNumber: 1234567890,
  shareLocation: 1,
  twoFactor: 0
}));

  return (
    <UserContext.Provider value={{ user, setUser, loggedIn, setLoggedIn}}>
      {children}
    </UserContext.Provider>
  );
};

export default UserProvider;
export const useUser = () => useContext(UserContext);