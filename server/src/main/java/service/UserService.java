package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

public class UserService {
  private UserDAO userDAO;
  private AuthDAO authDAO;

  public UserService() {
    userDAO = new MemoryUserDAO();
    authDAO = new MemoryAuthDAO();
  }
  public AuthData register(UserData user) {
    if (userDAO.userExists(user.username())) {
      return null;
    } else {
      userDAO.insertUser(user);
      return authDAO.createAuth(user.username());
    }
  }
  public AuthData login(UserData user) {
    try {
      UserData userData = userDAO.selectUser(user.username(), user.password());
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
    return authDAO.createAuth(user.username());
  }
  public void logout(AuthData auth) {
    try {
      authDAO.deleteAuth(auth.authToken());
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }
}