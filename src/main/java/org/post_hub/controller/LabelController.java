package org.post_hub.controller;

import org.post_hub.model.Label;
import org.post_hub.service.LabelService;
import java.util.List;

public class LabelController {
    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    public Label createLabel(String name) {
        return labelService.createLabel(name);
    }

    public Label getById(Long id) {
        return labelService.getById(id);
    }

    public List<Label> getAll() {
        return labelService.getAll();
    }

    public Label updateLabel(Long id, String name) {
        return labelService.updateLabel(id, name);
    }

    public void deleteLabel(Long id) {
        labelService.deleteLabel(id);
    }
}