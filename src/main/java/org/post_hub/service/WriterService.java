package org.post_hub.service;

import org.post_hub.model.Writer;
import org.post_hub.repository.WriterRepository;
import org.post_hub.repository.impl.JdbcWriterRepositoryImpl;

import java.util.List;

public class WriterService {

    private final WriterRepository writerRepository;

    public WriterService() {
        this.writerRepository = new JdbcWriterRepositoryImpl();
    }

    public Writer createWriter(String firstName, String lastName) {
        Writer writer = new Writer();
        writer.setFirstName(firstName);
        writer.setLastName(lastName);
        return writerRepository.save(writer);
    }

    public Writer getWriterById(Long id) {
        return writerRepository.getById(id);
    }

    public List<Writer> getAllWriters() {
        return writerRepository.getAll();
    }

    public Writer updateWriter(Long id, String firstName, String lastName) {
        Writer writer = writerRepository.getById(id);
        if (writer != null) {
            writer.setFirstName(firstName);
            writer.setLastName(lastName);
            return writerRepository.update(writer);
        }
        return null;
    }

    public void deleteWriter(Long id) {
        writerRepository.deleteById(id);
    }
}