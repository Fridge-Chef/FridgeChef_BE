package Fridge_Chef.team.board.domain;

import Fridge_Chef.team.user.domain.User;
import fixture.BoardFixture;
import fixture.UserFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BoardIssueTest {

    @Test
    void create(){
        User user =UserFixture.create("test@tes.com");
        Board board = BoardFixture.create(user);
        assertDoesNotThrow( () -> new BoardIssue(board));
    }
}
