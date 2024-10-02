package Fridge_Chef.team.board.repository;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.repository.model.BookType;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BookCommentRequest;
import Fridge_Chef.team.board.rest.request.BookRecipeRequest;
import Fridge_Chef.team.board.rest.response.BookBoardResponse;
import Fridge_Chef.team.board.rest.response.BookCommentResponse;
import Fridge_Chef.team.comment.domain.Comment;
import Fridge_Chef.team.comment.domain.QComment;
import Fridge_Chef.team.user.domain.UserId;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static Fridge_Chef.team.board.domain.QBoard.board;
import static Fridge_Chef.team.board.domain.QBoardUserEvent.boardUserEvent;

@Repository
@RequiredArgsConstructor
public class BookDslRepository {
    private final JPAQueryFactory factory;

    public Page<BookBoardResponse> findByBoard(PageRequest pageable, UserId userId, BookRecipeRequest request) {
        JPAQuery<Board> query = factory.selectFrom(board)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (request.getBookType().equals(BookType.MYRECIPE)) {
            query.where(board.user.userId.eq(userId));
        } else {
            query.where(boardUserEvent.user.userId.eq(userId)
                    .and(boardUserEvent.hit.eq(1)));
        }

        applySort(query, request.getSortType());

        List<BookBoardResponse> results = query.fetch()
                .stream()
                .map(BookBoardResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, results.size());
    }

    public Page<BookCommentResponse> findByComment(PageRequest pageable, UserId userId, BookCommentRequest request) {
        QComment comment = QComment.comment1;

        JPAQuery<Comment> query = factory.selectFrom(comment)
                .where(comment.board.user.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        applySort(query, request.getSortType());

        List<BookCommentResponse> results = query.fetch()
                .stream()
                .map(BookCommentResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, results.size());
    }

    private <T> void applySort(JPAQuery<T> query, SortType sortType) {
        switch (sortType) {
            case RATING -> query.orderBy(board.totalStar.desc());
            case HIT -> query.orderBy(board.hit.desc());
            case CLICKS -> query.orderBy(board.count.desc());
            default -> query.orderBy(board.createTime.desc());
        }
    }
}
