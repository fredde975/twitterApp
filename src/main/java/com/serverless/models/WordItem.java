package com.serverless.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordItem implements Comparable{
    private String word;
    private Integer count;

//    public WordItem(String word, Integer count) {
//        this.word = word;
//        this.count = count;
//    }

//    public String getWord() {
//        return word;
//    }
//
//    public Integer getCount() {
//        return count;
//    }
//
    @Override
    public int compareTo(Object that) {
        return ((WordItem)that).count - this.count;
    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        WordItem item = (WordItem) o;
//        return Objects.equals(word, item.word) &&
//                Objects.equals(count, item.count);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(word, count);
//    }
//
//    @Override
//    public String toString() {
//        return "WordItem{" +
//                "word='" + word + '\'' +
//                ", count=" + count +
//                '}';
//    }
}
