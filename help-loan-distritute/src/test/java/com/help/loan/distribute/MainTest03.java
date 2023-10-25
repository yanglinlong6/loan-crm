package com.help.loan.distribute;

import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yanglinlong
 * @date 2023/10/25 16:36
 */
public class MainTest03 {
    public static List<Integer> getEquallySpacedValues(List<Integer> list, int interval) {
        List<Integer> result = new ArrayList<>();
        int index = 0;
        while (index < list.size()) {
            result.add(list.get(index));
            index += interval;
        }
        return result;
    }

    public static void main(String[] args) {
        List<Integer> numbers = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        int interval = 1;
        List<Integer> equallySpacedValues = getEquallySpacedValues(numbers, interval);
        System.out.println("Equally spaced values: " + equallySpacedValues);
    }
}
