package org.rssreader.service.filter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import org.rssreader.service.decorator.BasicArticleComponent;
import org.rssreader.service.decorator.ArticleComponent;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FilterStrategyTest {
    // egy dummy ArticleComponent gyártó helper
    private ArticleComponent makeComp(String title, LocalDateTime dt, String content) throws Exception {
        return new BasicArticleComponent(
                new org.rssreader.models.Article(
                        0,
                        new URI("http://example.com"),
                        title,
                        new URI("http://link"),
                        dt,
                        content
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "Hello World, 2025-05-17T10:00, , 1",
            "Foo Bar, 2025-05-17T12:00, , 1",
            "NoMatch, 2025-05-16T09:00, , 0"
    })
    void titleFilterWorks(String title, String dateTime, String unusedContent, int expectedCount) throws Exception {
        List<ArticleComponent> list = List.of(
                makeComp("Hello World", LocalDateTime.parse("2025-05-17T10:00"), ""),
                makeComp("Foo Bar",   LocalDateTime.parse("2025-05-17T12:00"), "")
        );
        FilterStrategy<ArticleComponent> f = new TitleFilter(title.split(" ")[0]);
        assertEquals(expectedCount, f.filter(list).size());
    }

    @ParameterizedTest
    @MethodSource("keywordData")
    void keywordFilterWorks(String keyword, int expectedCount) throws Exception {
        List<ArticleComponent> list = List.of(
                makeComp("t1", LocalDateTime.now(), "apple banana"),
                makeComp("t2", LocalDateTime.now(), "banana cherry"),
                makeComp("t3", LocalDateTime.now(), "cherry")
        );
        FilterStrategy<ArticleComponent> f = new KeywordFilter(keyword);
        assertEquals(expectedCount, f.filter(list).size());
    }
    static Stream<Arguments> keywordData() {
        return Stream.of(
                Arguments.arguments("banana", 2),
                Arguments.arguments("cherry", 2),
                Arguments.arguments("apple", 1),
                Arguments.arguments("xyz", 0)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "2025-05-17,2",
            "2025-05-16,1",
            "2025-05-15,0"
    })
    void dateFilterWorks(String date, int expected) throws Exception {
        List<ArticleComponent> list = List.of(
                makeComp("A", LocalDateTime.parse("2025-05-17T00:00"), ""),
                makeComp("B", LocalDateTime.parse("2025-05-17T23:59"), ""),
                makeComp("C", LocalDateTime.parse("2025-05-16T12:00"), "")
        );
        FilterStrategy<ArticleComponent> f = new DateFilter(LocalDate.parse(date));
        assertEquals(expected, f.filter(list).size());
    }

    @Test
    void allFilterReturnsAll() throws Exception {
        List<ArticleComponent> list = List.of(
                makeComp("A", LocalDateTime.now(), ""),
                makeComp("B", LocalDateTime.now(), "")
        );
        FilterStrategy<ArticleComponent> f = articles -> articles;
        assertEquals(2, f.filter(list).size());
    }
}
