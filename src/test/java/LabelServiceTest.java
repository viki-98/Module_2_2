import org.post_hub.model.Label;
import org.post_hub.repository.LabelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.post_hub.service.LabelService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {

    @Mock
    private LabelRepository labelRepository;

    @InjectMocks
    private LabelService labelService;

    @Test
    void createLabel_ShouldReturnSavedLabel() {
        String name = "Java";
        Label expectedLabel = new Label(1L, name);

        when(labelRepository.save(any(Label.class))).thenReturn(expectedLabel);

        Label result = labelService.createLabel(name);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java", result.getName());
        verify(labelRepository, times(1)).save(any(Label.class));
    }

    @Test
    void createLabel_WithEmptyName_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            labelService.createLabel("");
        });
        verify(labelRepository, never()).save(any());
    }
}