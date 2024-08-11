package servlets;

import models.User;
import javax.servlet.annotation.WebServlet;

@WebServlet("/api/users/*")
public class UserServlet extends AbstractUserServlet<User> {
    @Override
    protected String getRepositoryKey() {
        return "userRepository";
    }

    @Override
    protected Class<User> getUserClass() {
        return User.class;
    }
}
