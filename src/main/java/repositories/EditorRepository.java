package repositories;

import models.Editor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EditorRepository {
    private final ConcurrentHashMap<Long, Editor> editors = new ConcurrentHashMap<>();
    private long currentId = 1;

    public List<Editor> getAllEditors() {
        return new ArrayList<>(editors.values());
    }

    public Editor getEditor(long id) {
        return editors.get(id);
    }

    public void addEditor(Editor editor) {
        if (editor == null) {
            throw new IllegalArgumentException("Editor cannot be null");
        }
        editor.setId(currentId++);
        editors.put(editor.getId(), editor);
    }

    public void updateEditor(long id, Editor editor) {
        if (editor == null) {
            throw new IllegalArgumentException("Editor cannot be null");
        }
        if (!editors.containsKey(id)) {
            throw new IllegalArgumentException("Editor with ID " + id + " does not exist");
        }
        editor.setId(id);
        editors.put(id, editor);
    }

    public void deleteEditor(long id) {
        if (!editors.containsKey(id)) {
            throw new IllegalArgumentException("Editor with ID " + id + " does not exist");
        }
        editors.remove(id);
    }
}
