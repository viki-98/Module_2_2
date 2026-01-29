import org.post_hub.model.Writer;
import org.post_hub.repository.WriterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.post_hub.service.WriterService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WriterServiceTest {

    @Mock
    private WriterRepository writerRepository;

    @InjectMocks
    private WriterService writerService;

    @Test
    void createWriter_ShouldReturnSavedWriter() {
        String firstName = "Stephen";
        String lastName = "King";

        Writer expectedWriter = new Writer();
        expectedWriter.setId(1L);
        expectedWriter.setFirstName(firstName);
        expectedWriter.setLastName(lastName);

        when(writerRepository.save(any(Writer.class))).thenReturn(expectedWriter);

        Writer result = writerService.createWriter(firstName, lastName);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Stephen", result.getFirstName());
        verify(writerRepository, times(1)).save(any(Writer.class));
    }

    @Test
    void getAllWriters_ShouldReturnList() {
        when(writerRepository.getAll()).thenReturn(List.of(new Writer(), new Writer()));

        List<Writer> result = writerService.getAllWriters();

        assertEquals(2, result.size());
        verify(writerRepository, times(1)).getAll();
    }
}