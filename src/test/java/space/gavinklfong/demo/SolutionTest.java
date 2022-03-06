package space.gavinklfong.demo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SolutionTest {

    @ParameterizedTest
    @MethodSource("provideArguments")
    public void testLengthOfLastWord(String inputString, int expectedLength) {

        int length = Solution.getLengthOfLastWord(inputString);

        assertThat(length).isEqualTo(expectedLength);
    }

    public static Stream<Arguments> provideArguments() {
        return Stream.of(
                Arguments.arguments("Hello World", 5),
                Arguments.arguments("Hello World  ", 5),
                Arguments.arguments("Hello    World  ", 5),
                Arguments.arguments("    World  ", 5),
                Arguments.arguments("HelloWorld", 10),
                Arguments.arguments("  ", 0),
                Arguments.arguments("", 0),
                Arguments.arguments(null, 0),
                Arguments.arguments("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse posuere nisl at tortor ornare", 6)
        );
    }

}
