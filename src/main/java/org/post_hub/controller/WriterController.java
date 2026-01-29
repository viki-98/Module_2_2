package org.post_hub.controller;

import org.post_hub.model.Writer;
import org.post_hub.service.WriterService;
import java.util.List;

public class WriterController {
    private final WriterService writerService;

    public WriterController(WriterService writerService) {
        this.writerService = writerService;
    }

    public Writer createWriter(String firstName, String lastName) {
        return writerService.createWriter(firstName, lastName);
    }

    public Writer getById(Long id) {
        return writerService.getWriterById(id);
    }

    public List<Writer> getAll() {
        return writerService.getAllWriters();
    }

    public Writer updateWriter(Long id, String firstName, String lastName) {
        return writerService.updateWriter(id, firstName, lastName);
    }

    public void deleteWriter(Long id) {
        writerService.deleteWriter(id);
    }
}