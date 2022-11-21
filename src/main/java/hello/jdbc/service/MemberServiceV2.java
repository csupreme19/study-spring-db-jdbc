package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection conn = dataSource.getConnection();

        try {
            // 트랜잭션 시작
            conn.setAutoCommit(false);

            // 비즈니스 로직
            bizLogic(conn, fromId, toId, money);

            // 성공시 커밋
            conn.commit();
        } catch (Exception e) {
            // 실패시 롤백
            conn.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(conn);
        }

    }

    private void bizLogic(Connection conn, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(conn, fromId);
        Member toMember = memberRepository.findById(conn, toId);

        memberRepository.update(conn, fromId, fromMember.getMoney() - money);
        validate(toMember);
        memberRepository.update(conn, toId, toMember.getMoney() + money);
    }

    private static void release(Connection conn) {
        if(conn != null) {
            try {
                // 커넥션 풀을 고려한 원복
                conn.setAutoCommit(true);
                conn.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private static void validate(Member toMember) throws SQLException {
        if(toMember.getMemberId().equals("ex")) {
            throw new SQLException("이체 실패");
        }
    }

}
