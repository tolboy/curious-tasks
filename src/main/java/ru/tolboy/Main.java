package ru.tolboy;

import ru.tolboy.deepcopy.CopyUtils;
import ru.tolboy.deepcopy.testentities.Man;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Man originalMan = new Man("Smith", 40, List.of("Book_1", "Book_2", "Book_3"));
        Man copiedMan = CopyUtils.deepCopy(originalMan);
        System.out.println("copiedMan is not the same originalMan: " + (originalMan == copiedMan));
    }
}