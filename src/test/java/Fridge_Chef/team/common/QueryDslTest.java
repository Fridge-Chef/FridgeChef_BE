package Fridge_Chef.team.common;

import Fridge_Chef.team.config.JpaTestConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@Transactional
@Import(JpaTestConfig.class)
public class QueryDslTest {
    @Autowired
    protected JPAQueryFactory factory;
}
