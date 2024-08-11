package servlets;

import repositories.BookRepository;
import models.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/books/*")
public class BookServlet extends HttpServlet {
    private BookRepository bookRepository;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        bookRepository = (BookRepository) getServletContext().getAttribute("bookRepository");
        if (bookRepository == null) {
            throw new ServletException("BookRepository not initialized in ServletContext.");
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
        List<Book> bookList = bookRepository.getAllBooks();
        writeResponse(resp, bookList);
    }

    private void handleGetById(String pathInfo, HttpServletResponse resp) throws IOException {
        String[] splits = pathInfo.split("/");
        if (splits.length == 2) {
            try {
                long id = Long.parseLong(splits[1]);
                Book book = bookRepository.getBook(id);
                if (book != null) {
                    writeResponse(resp, book);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
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
        Book book = objectMapper.readValue(req.getReader(), Book.class);
        bookRepository.addBook(book);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.split("/").length == 2) {
            try {
                long id = Long.parseLong(pathInfo.split("/")[1]);
                Book book = objectMapper.readValue(req.getReader(), Book.class);
                book.setId(id);
                bookRepository.updateBook(book);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.split("/").length == 2) {
            try {
                long id = Long.parseLong(pathInfo.split("/")[1]);
                bookRepository.deleteBook(id);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }
}
