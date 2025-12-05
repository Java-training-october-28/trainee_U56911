import { create } from 'zustand';
import { persist } from 'zustand/middleware';

// Create a task store with Zustand
const useTaskStore = create(
  persist(
    (set, get) => ({
      // State
      tasks: [],
      filter: 'all', // 'all', 'completed', 'pending', 'in-progress'
      searchQuery: '',
      loading: false,
      error: null,

      // Computed/derived state (using get() for current state)
      get filteredTasks() {
        const { tasks, filter, searchQuery } = get();
        let filtered = tasks;

        // Apply filter
        if (filter === 'completed') {
          filtered = filtered.filter(task => task.status === 'Completed');
        } else if (filter === 'pending') {
          filtered = filtered.filter(task => task.status === 'Pending');
        } else if (filter === 'in-progress') {
          filtered = filtered.filter(task => task.status === 'In Progress');
        }

        // Apply search
        if (searchQuery) {
          const query = searchQuery.toLowerCase();
          filtered = filtered.filter(
            task =>
              task.title.toLowerCase().includes(query) ||
              task.description.toLowerCase().includes(query)
          );
        }

        return filtered;
      },

      get stats() {
        const { tasks } = get();
        return {
          total: tasks.length,
          completed: tasks.filter(t => t.status === 'Completed').length,
          pending: tasks.filter(t => t.status === 'Pending').length,
          inProgress: tasks.filter(t => t.status === 'In Progress').length,
        };
      },

      // Actions
      addTask: (task) => {
        const newTask = {
          ...task,
          id: Date.now(), // Simple ID generation
          createdAt: new Date().toISOString(),
        };
        set((state) => ({
          tasks: [...state.tasks, newTask],
        }));
      },

      updateTask: (id, updates) => {
        set((state) => ({
          tasks: state.tasks.map(task =>
            task.id === id ? { ...task, ...updates, updatedAt: new Date().toISOString() } : task
          ),
        }));
      },

      deleteTask: (id) => {
        set((state) => ({
          tasks: state.tasks.filter(task => task.id !== id),
        }));
      },

      toggleTaskStatus: (id) => {
        set((state) => ({
          tasks: state.tasks.map(task => {
            if (task.id === id) {
              const newStatus = task.status === 'Completed' ? 'Pending' : 'Completed';
              return {
                ...task,
                status: newStatus,
                updatedAt: new Date().toISOString(),
              };
            }
            return task;
          }),
        }));
      },

      setFilter: (filter) => {
        set({ filter });
      },

      setSearchQuery: (query) => {
        set({ searchQuery: query });
      },

      clearCompleted: () => {
        set((state) => ({
          tasks: state.tasks.filter(task => task.status !== 'Completed'),
        }));
      },

      // Async action example (simulated)
      fetchTasks: async () => {
        set({ loading: true, error: null });
        try {
          // Simulate API call
          await new Promise(resolve => setTimeout(resolve, 1000));
          const mockTasks = [
            { id: 1, title: 'Implement authentication', description: 'Add JWT authentication to the backend', status: 'In Progress', priority: 'High' },
            { id: 2, title: 'Design database schema', description: 'Create ER diagram for the project', status: 'Completed', priority: 'Medium' },
            { id: 3, title: 'Write unit tests', description: 'Cover all service layers with tests', status: 'Pending', priority: 'High' },
          ];
          set({ tasks: mockTasks, loading: false });
        } catch (error) {
          set({ error: error.message, loading: false });
        }
      },

      // Reset store
      reset: () => {
        set({
          tasks: [],
          filter: 'all',
          searchQuery: '',
          loading: false,
          error: null,
        });
      },
    }),
    {
      name: 'task-storage', // localStorage key
      getStorage: () => localStorage, // use localStorage
    }
  )
);

export default useTaskStore;
