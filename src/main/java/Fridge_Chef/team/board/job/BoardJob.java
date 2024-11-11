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

    @Transactional
    @Scheduled(cron = "0 0/10 * * * ?")
    void recipeIssueUp() {
        log.info("추천 레시피 스케줄러 run()");
        List<Board> boards = boardRepository.findAll();
        int userSize = userRepository.findAll().size();

        // 오늘의 시작과 끝 시각 계산
        LocalDateTime startOfDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);


        for (Board board : boards) {
            if (board.getCount() <= 10) {
                return;
            }
            if (!(board.getCount() >= (userSize / 2) &&
                    (board.getTotalStar() >= 3.5))) {
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

            if (countDifference == 0) {
                return;
            }

            boolean shouldSave = isShouldSave(userSize,countDifference);
            if (shouldSave &&! boardIssueRepository.existsByBoardAndCreateTimeBetween(board,startOfDay,endOfDay)) {
                log.info(" 이슈 등록 "+ board.getTitle() +","+board.getId());
                boardIssueRepository.save(new BoardIssue(board));
            }
        }
    }

    private boolean isShouldSave(int userSize,int size){
        if (userSize <= 10) {
            return true;
        } else if (userSize <= 100 && size >= 10) {
            return true;
        } else if (userSize <= 1000 && size >= 100) {
            return true;
        } else if (userSize <= 10000 && size >= 100) {
            return true;
        } else if (size >= (userSize % 10) / 2) {
            return true;
        }
        return false;
    }

}
