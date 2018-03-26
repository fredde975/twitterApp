package com.serverless.models;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

public class WordItemTest {

    @Test
    public void compareTwoItems() {
        WordItem item1 = new WordItem("justin", 0);
        WordItem item2 = new WordItem("bieber", 1);

        assertThat(item1.compareTo(item2)).isGreaterThan(0);
    }

    @Test
    public void compareToShouldResultInDecendingOrder() {
        WordItem item1 = new WordItem("justin", 0);
        WordItem item2 = new WordItem("bieber", 2);
        WordItem item3 = new WordItem("sweden", 0);

        List<WordItem> items = Arrays.asList(item1, item2, item3);
        List<WordItem> sortedItems = items.stream()
                .sorted()
                .collect(Collectors.toList());

        assertThat(sortedItems.size()).isEqualTo(3);
        assertThat(sortedItems.get(0).getCount()).isGreaterThan(sortedItems.get(1).getCount());
        assertThat(sortedItems.get(1).getCount()).isEqualTo(sortedItems.get(2).getCount());

    }
}