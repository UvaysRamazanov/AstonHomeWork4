package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Author;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repositories.AuthorRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/authors/*")
public class AuthorServlet extends AbstractUserServlet<Author> {
    private static final Logger logger = LoggerFactory.getLogger(AuthorServlet.class);
    private ObjectMapper objectMapper;
    private AuthorRepository authorRepository;

    @Override
    protected String getRepositoryKey() {
        return "authorRepository";
    }

    @Override
    protected Class<Author> getUserClass() {
        return Author.class;
    }

    @Override
    public void init() {
        objectMapper = new ObjectMapper(); // Инициализация ObjectMapper
        authorRepository = (AuthorRepository) getServletContext().getAttribute(getRepositoryKey());
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
        List<Author> authorsList = authorRepository.getAllUsers();
        writeResponse(resp, authorsList);
    }

    private void handleGetById(String pathInfo, HttpServletResponse resp) throws IOException {
        String[] splits = pathInfo.split("/");
        if (splits.length == 2) {
            try {
                long id = Long.parseLong(splits[1]);
                Author author = authorRepository.getUserById(id);
                if (author != null) {
                    writeResponse(resp, author);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Author not found");
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid author ID format: {}", splits[1], e);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
            } catch (Exception e) {
                logger.error("Error retrieving author with ID: {}", splits[1], e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }

    private void writeResponse(HttpServletResponse resp, Object data) throws IOException {
        String json = objectMapper.writeValueAsString(data);
        resp.getWriter().write(json);
    }
}
