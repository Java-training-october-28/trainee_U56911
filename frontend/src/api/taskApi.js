// Mock API service for tasks
const API_BASE_URL = 'https://api.example.com';

// Simulate network delay
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Mock data
const mockTasks = [
  { id: 1, title: 'Implement authentication', description: 'Add JWT authentication to the backend', status: 'In Progress', priority: 'High' },
  { id: 2, title: 'Design database schema', description: 'Create ER diagram for the project', status: 'Completed', priority: 'Medium' },
  { id: 3, title: 'Write unit tests', description: 'Cover all service layers with tests', status: 'Pending', priority: 'High' },
  { id: 4, title: 'Deploy to production', description: 'Setup CI/CD pipeline', status: 'Pending', priority: 'Medium' },
  { id: 5, title: 'Document API', description: 'Create Swagger documentation', status: 'In Progress', priority: 'Low' },
];

// API functions
export const taskApi = {
  // Fetch all tasks
  getTasks: async () => {
    await delay(800); // Simulate network delay
    return { data: mockTasks, total: mockTasks.length };
  },

  // Fetch single task
  getTask: async (id) => {
    await delay(500);
    const task = mockTasks.find(t => t.id === id);
    if (!task) throw new Error('Task not found');
    return { data: task };
  },

  // Create task
  createTask: async (taskData) => {
    await delay(700);
    const newTask = {
      id: Date.now(),
      ...taskData,
      createdAt: new Date().toISOString(),
    };
    mockTasks.push(newTask);
    return { data: newTask };
  },

  // Update task
  updateTask: async (id, updates) => {
    await delay(600);
    const index = mockTasks.findIndex(t => t.id === id);
    if (index === -1) throw new Error('Task not found');
    mockTasks[index] = { ...mockTasks[index], ...updates, updatedAt: new Date().toISOString() };
    return { data: mockTasks[index] };
  },

  // Delete task
  deleteTask: async (id) => {
    await delay(500);
    const index = mockTasks.findIndex(t => t.id === id);
    if (index === -1) throw new Error('Task not found');
    mockTasks.splice(index, 1);
    return { success: true };
  },

  // Search tasks
  searchTasks: async (query) => {
    await delay(300);
    const filtered = mockTasks.filter(task =>
      task.title.toLowerCase().includes(query.toLowerCase()) ||
      task.description.toLowerCase().includes(query.toLowerCase())
    );
    return { data: filtered, total: filtered.length };
  },
