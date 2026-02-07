package org.post_hub.service;

import org.post_hub.model.Label;
import org.post_hub.repository.LabelRepository;
import org.post_hub.repository.impl.JdbcLabelRepositoryImpl;

import java.util.List;

public class LabelService {
    private final LabelRepository labelRepository;

    public LabelService() {

        this.labelRepository = new JdbcLabelRepositoryImpl();
    }

    public Label createLabel(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be empty");
        }
        Label label = new Label();
        label.setName(name);
        return labelRepository.save(label);
    }

    public Label getById(Long id) {
        return labelRepository.getById(id);
    }

    public List<Label> getAll() {
        return labelRepository.getAll();
    }

    public Label updateLabel(Long id, String newName) {
        Label label = labelRepository.getById(id);
        if (label != null) {
            label.setName(newName);
            return labelRepository.update(label);
        }
        return null;
    }

    public void deleteLabel(Long id) {
        labelRepository.deleteById(id);
    }
}
