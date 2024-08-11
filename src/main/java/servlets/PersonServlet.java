package servlets;

import repositories.PersonRepository;
import models.Person;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/persons/*")
public class PersonServlet extends HttpServlet {
    private PersonRepository personRepository;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        personRepository = (PersonRepository) getServletContext().getAttribute("personRepository");
        if (personRepository == null) {
            throw new ServletException("PersonRepository not initialized in ServletContext.");
        }
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleGetAll(resp);
        } else {
            handleGetById(pathInfo, resp);
        }
    }

    private void handleGetAll(HttpServletResponse resp) throws IOException {
        List<Person> personList = personRepository.getAllUsers();
        writeResponse(resp, personList);
    }

    private void handleGetById(String pathInfo, HttpServletResponse resp) throws IOException {
        String[] splits = pathInfo.split("/");
        if (splits.length == 2) {
            try {
                long id = Long.parseLong(splits[1]);
                Person person = personRepository.getUserById(id);
                if (person != null) {
                    writeResponse(resp, person);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Person not found");
                }
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
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
        Person person = objectMapper.readValue(req.getReader(), Person.class);
        personRepository.addUser(person);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.split("/").length == 2) {
            try {
                long id = Long.parseLong(pathInfo.split("/")[1]);
                Person person = objectMapper.readValue(req.getReader(), Person.class);
                person.setId(id);
                personRepository.updateUser(person);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
            } catch (Exception e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating person");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }
}
