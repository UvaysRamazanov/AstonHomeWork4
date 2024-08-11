package servlets;

import models.User;
import repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public abstract class AbstractUserServlet<T extends User> extends HttpServlet {
    protected UserRepository<T> userRepository;
    protected ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        userRepository = (UserRepository<T>) getServletContext().getAttribute(getRepositoryKey());
        if (userRepository == null) {
            throw new ServletException(getRepositoryKey() + " not initialized in ServletContext.");
        }
        objectMapper = new ObjectMapper();
    }

    protected abstract String getRepositoryKey();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json; charset=UTF-8");

        if (pathInfo == null || pathInfo.equals("/")) {
            List<T> users = userRepository.getAllUsers();
            writeResponse(resp, users);
        } else {
            handleGetById(pathInfo, resp);
        }
    }

    private void handleGetById(String pathInfo, HttpServletResponse resp) throws IOException {
        String[] splits = pathInfo.split("/");
        if (splits.length == 2) {
            long id = Long.parseLong(splits[1]);
            T user = userRepository.getUserById(id);
            if (user != null) {
                writeResponse(resp, user);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }

    private void writeResponse(HttpServletResponse resp, Object data) throws IOException {
        String json = objectMapper.writeValueAsString(data);
        resp.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        T user = objectMapper.readValue(req.getReader(), getUserClass());
        userRepository.addUser(user);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.split("/").length == 2) {
            long id = Long.parseLong(pathInfo.split("/")[1]);
            T user = objectMapper.readValue(req.getReader(), getUserClass());
            user.setId(id);
            userRepository.updateUser(user);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.split("/").length == 2) {
            long id = Long.parseLong(pathInfo.split("/")[1]);
            userRepository.deleteUser(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }

    protected abstract Class<T> getUserClass();
}
