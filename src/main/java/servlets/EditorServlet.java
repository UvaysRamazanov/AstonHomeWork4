package servlets;

import models.Editor;
import repositories.EditorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/editors/*")
public class EditorServlet extends HttpServlet {
    private EditorRepository editorRepository;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        editorRepository = (EditorRepository) getServletContext().getAttribute("editorRepository");
        if (editorRepository == null) {
            throw new ServletException("EditorRepository not initialized in ServletContext.");
        }
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json; charset=UTF-8");

        if (pathInfo == null || pathInfo.equals("/")) {
            List<Editor> editors = editorRepository.getAllEditors();
            String json = objectMapper.writeValueAsString(editors);
            resp.getWriter().write(json);
        } else {
            String[] splits = pathInfo.split("/");
            if (splits.length == 2) {
                long id = Long.parseLong(splits[1]);
                Editor editor = editorRepository.getEditor(id);
                if (editor != null) {
                    String json = objectMapper.writeValueAsString(editor);
                    resp.getWriter().write(json);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Editor not found");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Editor editor = objectMapper.readValue(req.getReader(), Editor.class);
        editorRepository.addEditor(editor);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.split("/").length == 2) {
            long id = Long.parseLong(pathInfo.split("/")[1]);
            Editor editor = objectMapper.readValue(req.getReader(), Editor.class);
            editorRepository.updateEditor(id, editor);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }

}
