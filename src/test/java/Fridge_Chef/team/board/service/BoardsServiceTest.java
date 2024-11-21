package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardHistory;
import Fridge_Chef.team.board.repository.BoardHistoryRepository;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.service.response.BoardMyRecipeResponse;
import Fridge_Chef.team.common.ServiceLayerTest;
import Fridge_Chef.team.user.domain.User;
import fixture.BoardFixture;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BoardsServiceTest extends ServiceLayerTest {
    @InjectMocks
    private BoardService boardService;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardHistoryRepository boardHistoryRepository;

    private User user;
    private Board board;

    @BeforeEach
    void setup(){
        user = UserFixture.create("test@test.com");
        board = BoardFixture.create(user);
    }

    @Test
    @DisplayName("금일 새로운 클릭 카운트 새로 등록")
    void findBoardIdNewHistoryCountSuccess() {
        when(boardRepository.findById(any())).thenReturn(Optional.of(board));
        when(boardHistoryRepository.save(any(BoardHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BoardMyRecipeResponse response = boardService.findMyRecipeId(1L);

        verify(boardRepository, times(1)).findById(1L);
        verify(boardHistoryRepository, times(1)).save(any(BoardHistory.class));
        assertNotNull(response);
        assertEquals(1, board.getCount());
    }

    @Test
    @DisplayName("금일 기존 클릭 카운트 증가")
    void findBoardIdHistoryCountUpSuccess() {
        BoardHistory existingHistory = new BoardHistory(board, 5);
        existingHistory.updateCreateDate(LocalDateTime.now());
        board.addHistory(existingHistory);

        when(boardRepository.findById(any())).thenReturn(Optional.of(board));
        BoardMyRecipeResponse response = boardService.findMyRecipeId(1L);

        verify(boardRepository, times(1)).findById(1L);
        verify(boardHistoryRepository, never()).save(existingHistory);
        assertNotNull(response);
        assertEquals(6, existingHistory.getCount());
        assertEquals(1, board.getCount());
    }
}
