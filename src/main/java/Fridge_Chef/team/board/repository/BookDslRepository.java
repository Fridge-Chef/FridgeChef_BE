package Fridge_Chef.team.board.repository;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.repository.model.BookType;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BookCommentRequest;
import Fridge_Chef.team.board.rest.request.BookRecipeRequest;
import Fridge_Chef.team.board.rest.response.BookBoardResponse;
import Fridge_Chef.team.comment.domain.Comment;
import Fridge_Chef.team.comment.domain.QComment;
import Fridge_Chef.team.comment.rest.response.CommentResponse;
import Fridge_Chef.team.user.domain.UserId;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import static Fridge_Chef.team.board.domain.QBoard.board;
import static Fridge_Chef.team.board.domain.QBoardUserEvent.boardUserEvent;
import static Fridge_Chef.team.comment.domain.QComment.comment;
import static Fridge_Chef.team.comment.domain.QCommentUserEvent.commentUserEvent;

@Repository
@RequiredArgsConstructor
public class BookDslRepository {
    private final JPAQueryFactory factory;

    public Page<BookBoardResponse> findByBoard(PageRequest pageable, UserId userId, BookRecipeRequest request) {
        JPAQuery<Board> query = factory.selectFrom(board)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .leftJoin(board.boardUserEvent, boardUserEvent);

        if (request.getBookType().equals(BookType.MYRECIPE)) {
            query.where(board.user.userId.eq(userId));
        } else if (request.getBookType().equals(BookType.COMMENT) && request.getSortType().equals(SortType.MONTHLY_RECIPE)) {
            LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);

            query.where(board.boardIssues.any().createTime.between(startOfMonth, endOfMonth));
        } else if (request.getBookType().equals(BookType.COMMENT) && request.getSortType().equals(SortType.WEEKLY_RECIPE)) {
            LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
            LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(23, 59, 59);

            query.where(board.boardIssues.any().createTime.between(startOfWeek, endOfWeek));
        } else if (request.getBookType().equals(BookType.LIKE)) {
            query.where(boardUserEvent.user.userId.eq(userId)
                    .and(boardUserEvent.hit.eq(1)));
        }
        applyBoardSort(query, request.getSortType());

        List<BookBoardResponse> results = query.fetch()
                .stream()
                .map(BookBoardResponse::new)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, results.size());
    }

    public Page<CommentResponse> findByComment(PageRequest pageable, UserId userId, BookCommentRequest request) {
        QComment comment = QComment.comment;

        JPAQuery<Comment> query = factory.selectFrom(comment)
                .leftJoin(comment.commentUserEvent, commentUserEvent)
                .where(comment.users.userId.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        applyCommentSort(query, request.getSortType());

        List<CommentResponse> results = query.fetch()
                .stream()
                .map(comments -> CommentResponse.fromMyEntity(comments, userId))
                .collect(Collectors.toList());

        int size = factory.selectFrom(comment)
                .leftJoin(comment.commentUserEvent, commentUserEvent)
                .where(comment.users.userId.eq(userId)).fetch().size();

        return new PageImpl<>(results, pageable, size);
    }

    private void applyBoardSort(JPAQuery<Board> query, SortType sortType) {
        switch (sortType) {
            case RATING -> query.orderBy(board.totalStar.desc());
            case HIT -> query.orderBy(board.hit.desc());
            case CLICKS -> query.orderBy(board.count.desc());
            default -> query.orderBy(board.createTime.desc());
        }
    }

    private void applyCommentSort(JPAQuery<Comment> query, SortType sortType) {
        switch (sortType) {
            case RATING -> query.orderBy(comment.star.desc());
            case HIT -> query.orderBy(comment.totalHit.desc());
            default -> query.orderBy(comment.createTime.desc());
        }
    }
}
