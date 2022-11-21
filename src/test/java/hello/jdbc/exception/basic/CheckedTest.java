package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throws() {
        Service service = new Service();
        Assertions.assertThatThrownBy(service::callThrow)
                .as("MyCheckedException 예외 발생")
                .isInstanceOf(MyCheckedException.class);
    }

    /**
     * RuntimeException이 아닌 Exception을 상속 받은 예외는 Checked Exception
     */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * Checked Exception은 예외를 받아서 처리하거나 던져야함
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        /**
         * 예외를 던짐
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }

}
