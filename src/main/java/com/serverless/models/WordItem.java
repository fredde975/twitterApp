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

    @Override
    public int compareTo(Object that) {
        return ((WordItem)that).count - this.count;
    }

}
