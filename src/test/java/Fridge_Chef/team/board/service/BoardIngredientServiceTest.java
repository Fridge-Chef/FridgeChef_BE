package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.DescriptionRepository;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.common.ServiceLayerTest;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.domain.ImageType;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import fixture.BoardFixture;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.mockito.Mockito.*;

public class BoardIngredientServiceTest extends ServiceLayerTest {
    @InjectMocks
    private BoardIngredientService boardIngredientService;
    @Mock
    private ImageService imageService;
    @Mock
    private DescriptionRepository descriptionRepository;
    @Mock
    private BoardRepository boardRepository;
    private User user;
    private Board board;

    @BeforeEach
    void setup() {
        user = UserFixture.create("test@test.com");
        board = BoardFixture.create(user);
    }

    @Test
    @DisplayName("업로드시 이미지가 널일 경우 처리")
    void uploadImageInNullCheck() {
        MockMultipartFile file = new MockMultipartFile("file", "file".getBytes());
        BoardByRecipeUpdateRequest request = new BoardByRecipeUpdateRequest(board.getId(), "update title", "update intro", null, 1L, false, "1분", "보통", "양념,",
                List.of(),
                List.of());
        boardIngredientService.uploadInstructionImages(user.getUserId(), request,List.of());

        verify(imageService, never()).imageUpload(user.getUserId(), file);
    }

    @Test
    @DisplayName("업로드시 이미지가 있는 경우 처리")
    void uploadImage() {
        MockMultipartFile file = new MockMultipartFile("file", "file".getBytes());
        BoardByRecipeUpdateRequest request = new BoardByRecipeUpdateRequest(board.getId(), "update title", "update intro", file, 1L, true, "1분", "보통", "양념,",
                List.of(),
                List.of(
                        new BoardByRecipeUpdateRequest.Instructions(1L, "", file, true),
                        new BoardByRecipeUpdateRequest.Instructions(2L, "", file, true)
                ));

        when(imageService.imageUpload(any(UserId.class), any(MockMultipartFile.class)))
                .thenReturn(new Image("url", ImageType.ORACLE_CLOUD));
        when(descriptionRepository.save(any(Description.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        boardIngredientService.uploadInstructionImages(user.getUserId(), request,
                List.of(new Description(1L, null, "1", null), new Description(3L, null, "2", null)));

        verify(imageService, times(2)).imageUpload(user.getUserId(), file);
        verify(descriptionRepository, times(1)).save(any(Description.class));
    }
}
