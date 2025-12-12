package com.example.demo.sequenced;

import java.util.*;

/**
 * Demonstration of Sequenced Collections (JEP 431) introduced in Java 21.
 * Shows usage of SequencedCollection, SequencedSet, SequencedMap interfaces
 * and their new methods.
 */
public class SequencedCollectionsDemo {

    public static void main(String[] args) {
        System.out.println("=== Sequenced Collections Demo (JEP 431) ===\n");

        demoSequencedCollection();
        demoSequencedSet();
        demoSequencedMap();
        demoReversedViews();
        demoPracticalExample();
    }

    /**
     * Demonstrates SequencedCollection methods on ArrayList.
     */
    private static void demoSequencedCollection() {
        System.out.println("1. SequencedCollection (ArrayList):");
        SequencedCollection<String> list = new ArrayList<>(List.of("B", "C", "D"));
        System.out.println("   Initial list: " + list);

        // Add elements at first and last positions
        list.addFirst("A");
        list.addLast("E");
        System.out.println("   After addFirst('A') and addLast('E'): " + list);

        // Get first and last elements
        System.out.println("   getFirst(): " + list.getFirst());
        System.out.println("   getLast(): " + list.getLast());

        // Remove first and last elements
        System.out.println("   removeFirst(): " + list.removeFirst());
        System.out.println("   removeLast(): " + list.removeLast());
        System.out.println("   After removals: " + list);

        // Get reversed view
        SequencedCollection<String> reversed = list.reversed();
        System.out.println("   Reversed view: " + reversed);
        System.out.println("   Original list (unchanged): " + list);

        // Modifications through reversed view affect original
        reversed.addFirst("Z");
        System.out.println("   After reversed.addFirst('Z'): " + list);
        System.out.println();
    }

    /**
     * Demonstrates SequencedSet methods on LinkedHashSet.
     */
    private static void demoSequencedSet() {
        System.out.println("2. SequencedSet (LinkedHashSet):");
        SequencedSet<Integer> set = new LinkedHashSet<>(List.of(2, 3, 4));
        System.out.println("   Initial set: " + set);

        // Add first and last (ignored if duplicate)
        set.addFirst(1);
        set.addLast(5);
        System.out.println("   After addFirst(1) and addLast(5): " + set);

        // Try adding duplicate
        set.addFirst(2); // Already present, no change
        System.out.println("   After addFirst(2) (duplicate): " + set);

        System.out.println("   getFirst(): " + set.getFirst());
        System.out.println("   getLast(): " + set.getLast());

        // Remove first and last
        System.out.println("   removeFirst(): " + set.removeFirst());
        System.out.println("   removeLast(): " + set.removeLast());
        System.out.println("   After removals: " + set);

        // Reversed view
        System.out.println("   Reversed view: " + set.reversed());
        System.out.println();
    }

    /**
     * Demonstrates SequencedMap methods on LinkedHashMap.
     */
    private static void demoSequencedMap() {
        System.out.println("3. SequencedMap (LinkedHashMap):");
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);
        System.out.println("   Initial map: " + map);

        // Add at beginning and end
        map.putFirst("one", 1);
        map.putLast("five", 5);
        System.out.println("   After putFirst('one',1) and putLast('five',5): " + map);

        // Get first and last entries
        Map.Entry<String, Integer> firstEntry = map.firstEntry();
        Map.Entry<String, Integer> lastEntry = map.lastEntry();
        System.out.println("   firstEntry(): " + firstEntry.getKey() + "=" + firstEntry.getValue());
        System.out.println("   lastEntry(): " + lastEntry.getKey() + "=" + lastEntry.getValue());

        // Poll first and last (remove and return)
        Map.Entry<String, Integer> polledFirst = map.pollFirstEntry();
        Map.Entry<String, Integer> polledLast = map.pollLastEntry();
        System.out.println("   pollFirstEntry(): " + polledFirst);
        System.out.println("   pollLastEntry(): " + polledLast);
        System.out.println("   After polling: " + map);

        // Reversed view
        SequencedMap<String, Integer> reversedMap = map.reversed();
        System.out.println("   Reversed map view: " + reversedMap);
        System.out.println();
    }

    /**
     * Demonstrates reversed views and their bidirectional modifications.
     */
    private static void demoReversedViews() {
        System.out.println("4. Reversed Views and Bidirectional Modifications:");
        SequencedCollection<String> original = new ArrayList<>(List.of("A", "B", "C", "D"));
        SequencedCollection<String> reversed = original.reversed();

        System.out.println("   Original: " + original);
        System.out.println("   Reversed: " + reversed);

        // Modification via reversed view
        reversed.addFirst("Z");
        System.out.println("   After reversed.addFirst('Z'):");
        System.out.println("     Original: " + original);
        System.out.println("     Reversed: " + reversed);

        // Modification via original
        original.addLast("E");
        System.out.println("   After original.addLast('E'):");
        System.out.println("     Original: " + original);
        System.out.println("     Reversed: " + reversed);

        // Reversed of reversed is original
        System.out.println("   reversed.reversed() == original: " + (reversed.reversed() == original));
        System.out.println();
    }

    /**
     * Practical example: Managing a task queue with sequenced operations.
     */
    private static void demoPracticalExample() {
        System.out.println("5. Practical Example: Task Queue Management");
        
        // Simulate a task queue (FIFO with ability to prioritize)
        SequencedCollection<String> taskQueue = new ArrayDeque<>();
        taskQueue.addLast("Task1: Process order");
        taskQueue.addLast("Task2: Send notification");
        taskQueue.addLast("Task3: Generate report");
        System.out.println("   Initial task queue (FIFO): " + taskQueue);

        // High priority task added to front
        taskQueue.addFirst("URGENT: System alert");
        System.out.println("   After adding high priority task: " + taskQueue);

        // Process first task
        String currentTask = taskQueue.removeFirst();
        System.out.println("   Processing: " + currentTask);
        System.out.println("   Remaining queue: " + taskQueue);

        // View tasks in reverse order (recently added first)
        System.out.println("   Tasks in reverse order: " + taskQueue.reversed());

        // Using SequencedMap for task priorities
        SequencedMap<String, Integer> taskPriorities = new LinkedHashMap<>();
        taskPriorities.put("Cleanup", 3);
        taskPriorities.put("Backup", 1);
        taskPriorities.put("Update", 2);
        System.out.println("\n   Task priorities (insertion order): " + taskPriorities);

        // Reorder by priority (lower number = higher priority)
        taskPriorities.putFirst("Backup", 1);
        System.out.println("   After prioritizing Backup: " + taskPriorities);

        // Get highest priority task
        Map.Entry<String, Integer> highestPriority = taskPriorities.firstEntry();
        System.out.println("   Highest priority task: " + highestPriority.getKey() + 
                         " (priority=" + highestPriority.getValue() + ")");
    }
}
