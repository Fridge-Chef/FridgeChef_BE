package Fridge_Chef.team.board.rest.response;

import Fridge_Chef.team.comment.domain.Comment;

public class BookCommentResponse {

    private long boardId;
    private long commentId;
    private String name;
    private double star;
    private String context;
    public BookCommentResponse(Comment entity) {
        this.boardId=entity.getBoard().getId();
        this.commentId=entity.getId();
        this.name = entity.getUser().getUsername();
        this.star=entity.getStar();
        this.context=entity.getComment();
    }
}
