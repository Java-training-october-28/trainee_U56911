package com.example.demo.sequenced;

import com.example.demo.base.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Sequenced Collections (JEP 431) features.
 * Extends BaseTest to follow project test conventions.
 * Note: This is a pure unit test that doesn't require Spring context or containers,
 * so BaseIntegrationTest is not used.
 */
class SequencedCollectionsTest extends BaseTest {

    @Test
    @DisplayName("SequencedCollection: addFirst, addLast, getFirst, getLast, removeFirst, removeLast")
    void testSequencedCollection() {
        SequencedCollection<String> list = new ArrayList<>(List.of("B", "C", "D"));
        
        list.addFirst("A");
        list.addLast("E");
        
        assertThat(list).containsExactly("A", "B", "C", "D", "E");
        assertThat(list.getFirst()).isEqualTo("A");
        assertThat(list.getLast()).isEqualTo("E");
        
        assertThat(list.removeFirst()).isEqualTo("A");
        assertThat(list.removeLast()).isEqualTo("E");
        assertThat(list).containsExactly("B", "C", "D");
    }

    @Test
    @DisplayName("SequencedCollection: reversed view")
    void testReversedView() {
        SequencedCollection<String> original = new ArrayList<>(List.of("A", "B", "C"));
        SequencedCollection<String> reversed = original.reversed();
        
        assertThat(reversed).containsExactly("C", "B", "A");
        assertThat(reversed.reversed()).isSameAs(original);
        
        // Modification via reversed view affects original
        reversed.addFirst("Z");
        assertThat(original).containsExactly("A", "B", "C", "Z");
        assertThat(reversed).containsExactly("Z", "C", "B", "A");
        
        // Modification via original affects reversed view
        original.addLast("Y");
        assertThat(reversed).containsExactly("Z", "C", "B", "A", "Y");
    }

    @Test
    @DisplayName("SequencedSet: duplicates are ignored")
    void testSequencedSet() {
        SequencedSet<Integer> set = new LinkedHashSet<>(List.of(2, 3, 4));
        
        set.addFirst(1);
        set.addLast(5);
        assertThat(set).containsExactly(1, 2, 3, 4, 5);
        
        // Adding duplicate does not change order
        set.addFirst(2);
        assertThat(set).containsExactly(1, 2, 3, 4, 5);
        
        assertThat(set.getFirst()).isEqualTo(1);
        assertThat(set.getLast()).isEqualTo(5);
        
        set.removeFirst();
        set.removeLast();
        assertThat(set).containsExactly(2, 3, 4);
    }

    @Test
    @DisplayName("SequencedMap: putFirst, putLast, firstEntry, lastEntry, pollFirstEntry, pollLastEntry")
    void testSequencedMap() {
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);
        
        map.putFirst("one", 1);
        map.putLast("five", 5);
        
        assertThat(map.sequencedKeySet()).containsExactly("one", "two", "three", "four", "five");
        assertThat(map.firstEntry().getKey()).isEqualTo("one");
        assertThat(map.firstEntry().getValue()).isEqualTo(1);
        assertThat(map.lastEntry().getKey()).isEqualTo("five");
        assertThat(map.lastEntry().getValue()).isEqualTo(5);
        
        Map.Entry<String, Integer> polledFirst = map.pollFirstEntry();
        assertThat(polledFirst.getKey()).isEqualTo("one");
        assertThat(polledFirst.getValue()).isEqualTo(1);
        
        Map.Entry<String, Integer> polledLast = map.pollLastEntry();
        assertThat(polledLast.getKey()).isEqualTo("five");
        assertThat(polledLast.getValue()).isEqualTo(5);
        
        assertThat(map.sequencedKeySet()).containsExactly("two", "three", "four");
    }

    @Test
    @DisplayName("SequencedMap: reversed view")
    void testSequencedMapReversed() {
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        
        SequencedMap<String, Integer> reversed = map.reversed();
        assertThat(reversed.sequencedKeySet()).containsExactly("C", "B", "A");
        
        // Modification via reversed view
        reversed.putFirst("D", 4);
        assertThat(map.sequencedKeySet()).containsExactly("A", "B", "C", "D");
        assertThat(reversed.sequencedKeySet()).containsExactly("D", "C", "B", "A");
        
        // Modification via original
        map.putLast("E", 5);
        assertThat(reversed.sequencedKeySet()).containsExactly("D", "C", "B", "A", "E");
    }

    @Test
    @DisplayName("Practical example: task queue with sequenced operations")
    void testTaskQueueExample() {
        SequencedCollection<String> taskQueue = new ArrayDeque<>();
        taskQueue.addLast("Task1");
        taskQueue.addLast("Task2");
        taskQueue.addLast("Task3");
        
        assertThat(taskQueue).containsExactly("Task1", "Task2", "Task3");
        
        // Add high priority task
        taskQueue.addFirst("URGENT");
        assertThat(taskQueue.getFirst()).isEqualTo("URGENT");
        
        // Process first task
        String processed = taskQueue.removeFirst();
        assertThat(processed).isEqualTo("URGENT");
        assertThat(taskQueue).containsExactly("Task1", "Task2", "Task3");
        
        // Reversed view
        assertThat(taskQueue.reversed()).containsExactly("Task3", "Task2", "Task1");
    }

    @Test
    @DisplayName("Edge cases: empty collections")
    void testEmptyCollections() {
        SequencedCollection<String> emptyList = new ArrayList<>();
        assertThatThrownBy(() -> emptyList.getFirst())
            .isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> emptyList.getLast())
            .isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> emptyList.removeFirst())
            .isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> emptyList.removeLast())
            .isInstanceOf(NoSuchElementException.class);
        
        // Empty map
        SequencedMap<String, Integer> emptyMap = new LinkedHashMap<>();
        assertThat(emptyMap.firstEntry()).isNull();
        assertThat(emptyMap.lastEntry()).isNull();
        assertThat(emptyMap.pollFirstEntry()).isNull();
        assertThat(emptyMap.pollLastEntry()).isNull();
    }
}
