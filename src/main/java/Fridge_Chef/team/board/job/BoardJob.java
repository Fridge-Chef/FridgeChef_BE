package Fridge_Chef.team.board.job;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardHistory;
import Fridge_Chef.team.board.domain.BoardIssue;
import Fridge_Chef.team.board.repository.BoardIssueRepository;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Component
public class BoardJob {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardIssueRepository boardIssueRepository;

    public BoardJob(BoardRepository boardRepository, UserRepository userRepository, BoardIssueRepository boardIssueRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.boardIssueRepository = boardIssueRepository;
    }

    /**
     * 추천 레시피 등록 조건
     * 1. 저번주 보다 이번주 조회수가 증가하였는가
     * 2. 저번주 보다 이번주 별점이 증가하였는가
     * ====
     * - 회원 가입수 비례해서 추가
     */
    @Transactional
    @Scheduled(cron = "0 0/10 * * * ?")
    // 10분마다 실행
    void recipeIssueUp() {
        log.info("추천 레시피 스케줄러 ");
        List<Board> boards = boardRepository.findAll();
        int userSize = userRepository.findAll().size();
        for (Board board : boards) {

            // 조회수가 10 미만이면 탈락
            if (board.getCount() <= 10) {
                return;
            }

            // 현재 가입된 유저수 반 이상 클릭시
            // 별점 3.5 이상 등급
            if (!(
                    board.getCount() >= (userSize / 2) &&
                            board.getTotalStar() >= 3.5)) {
                return;
            }

            var thisWeekStart = LocalDateTime.now()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .truncatedTo(ChronoUnit.DAYS);

            var lastWeekStart = thisWeekStart.minusWeeks(1);
            var lastWeekEnd = thisWeekStart.minusNanos(1);

            int thisWeekCount = board.getHistorys().stream()
                    .filter(history -> {
                        var createTime = history.getCreateTime();
                        return createTime.isAfter(thisWeekStart) && createTime.isBefore(LocalDateTime.now());
                    })
                    .mapToInt(BoardHistory::getCount)
                    .sum();

            int lastWeekCount = board.getHistorys().stream()
                    .filter(history -> {
                        var createTime = history.getCreateTime();
                        return createTime.isAfter(lastWeekStart) && createTime.isBefore(lastWeekEnd);
                    })
                    .mapToInt(BoardHistory::getCount)
                    .sum();

            int countDifference = thisWeekCount - lastWeekCount;
            if (countDifference >= userSize / 2) {
                boardIssueRepository.save(new BoardIssue(board));
            }
        }
    }

    private LocalDateTime getStartOfWeek(LocalDateTime now) {
        return now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
    }
}
