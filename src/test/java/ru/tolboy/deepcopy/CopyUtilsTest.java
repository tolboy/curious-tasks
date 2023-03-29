package ru.tolboy.deepcopy;

import org.junit.jupiter.api.Test;
import ru.tolboy.deepcopy.testentities.Man;
import ru.tolboy.deepcopy.testentities.ManOfMen;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ru.tolboy.deepcopy.testentities.ManOfMen.Possibilities.KICK;
import static ru.tolboy.deepcopy.testentities.ManOfMen.Possibilities.PUNCH;

class CopyUtilsTest {

    @Test
    void deepCopyManAllArgs() {
        // Given
        Man originalMan = new Man("Smith", 40, List.of("Book_1", "Book_2", "Book_3"));
        // When
        Man copiedMan = CopyUtils.deepCopy(originalMan);
        // Then
        assertNotNull(copiedMan);
        assertEquals(originalMan.getName(), copiedMan.getName());
        assertEquals(originalMan.getAge(), copiedMan.getAge());
        assertNotSame(originalMan.getFavoriteBooks(), copiedMan.getFavoriteBooks());
        assertArrayEquals(originalMan.getFavoriteBooks().toArray(), copiedMan.getFavoriteBooks().toArray());
    }

    @Test
    void deepCopyManOneArg() {
        // Given
        Man originalMan = new Man("Smith");
        // When
        Man copiedMan = CopyUtils.deepCopy(originalMan);
        // Then
        assertNotNull(copiedMan);
        assertNotSame(originalMan, copiedMan);
        assertEquals(originalMan.getName(), copiedMan.getName());
        assertEquals(originalMan.getAge(), copiedMan.getAge());
    }

    @Test
    void deepCopyMen() {
        // Given
        ManOfMen originalLeftSmith = new ManOfMen();
        ManOfMen originalRightSmith = new ManOfMen();
        ManOfMen originalSmith = new ManOfMen(originalLeftSmith, originalRightSmith);
        originalSmith.setPossibilities(new TreeSet<>(Arrays.asList(PUNCH, KICK)));
        originalLeftSmith.setPossibilities(Set.of(PUNCH) );
        originalRightSmith.setPossibilities(Set.of(PUNCH) );
        // Then
        assertEquals(2, originalSmith.getCopySmithsCounter());
        // When
        ManOfMen copiedSmith = CopyUtils.deepCopy(originalSmith);
        assertNotNull(copiedSmith);
        // Then
        assertNotNull(copiedSmith);
        // every copy uses no-args constructor for creation, so 2 original + 3 copy = 5 overall
        assertEquals(5, copiedSmith.getCopySmithsCounter());
        assertEquals(originalSmith.getAge(), copiedSmith.getAge());
        assertNotSame(originalSmith, copiedSmith);
        assertNotSame(originalSmith.getLeft(), copiedSmith.getLeft());
        assertNotSame(originalSmith.getRight(), copiedSmith.getRight());
        assertNotSame(originalSmith.getAge(), copiedSmith.getAge());
        assertNotSame(originalSmith.getSuspectNames(), copiedSmith.getSuspectNames());

        assertArrayEquals(originalSmith.getPossibilities().toArray(), copiedSmith.getPossibilities().toArray());
        assertArrayEquals(originalSmith.getZionCodes(), copiedSmith.getZionCodes());
        assertArrayEquals(Arrays.stream(originalSmith.getSuspectNames()).toArray(),
                Arrays.stream(copiedSmith.getSuspectNames()).toArray());
    }

    @Test
    void deepCopyArrayList() {
        List<String> originalList = Stream.of("Book_1", "Book_2", "Book_3")
                .collect(Collectors.toCollection(ArrayList::new));
        List<String> copiedList = CopyUtils.deepCopy(originalList);

        assertNotSame(originalList, copiedList);
        assertArrayEquals(originalList.toArray(), copiedList.toArray());
    }

    @Test
    void deepCopyLinkedList() {
        List<String> originalLinkedList = Stream.of("Book_1", "Book_2", "Book_3")
                .collect(Collectors.toCollection(LinkedList::new));
        List<String> copiedLinkedList = CopyUtils.deepCopy(originalLinkedList);

        assertNotSame(originalLinkedList, copiedLinkedList);
        assertArrayEquals(originalLinkedList.toArray(), copiedLinkedList.toArray());
    }

}