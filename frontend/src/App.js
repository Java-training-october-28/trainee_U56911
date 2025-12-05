import React, { useState, useEffect, useReducer, useCallback, useMemo, useRef, useContext, createContext } from 'react';
import Header from './components/Header';
import TaskList from './components/TaskList';
import TaskForm from './components/TaskForm';
import ProjectCard from './components/ProjectCard';
import UserProfile from './components/UserProfile';
import useTaskStore from './store/taskStore';
import './styles/App.css';

// Create a Theme Context for useContext example
const ThemeContext = createContext({
  theme: 'light',
  toggleTheme: () => {},
});

// Custom hook example
const useLocalStorage = (key, initialValue) => {
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error(error);
      return initialValue;
    }
  });

  const setValue = useCallback((value) => {
    try {
      const valueToStore = value instanceof Function ? value(storedValue) : value;
      setStoredValue(valueToStore);
      window.localStorage.setItem(key, JSON.stringify(valueToStore));
    } catch (error) {
      console.error(error);
    }
  }, [key, storedValue]);

  return [storedValue, setValue];
};

// useReducer example
const notificationReducer = (state, action) => {
  switch (action.type) {
    case 'ADD_NOTIFICATION':
      return [...state, { id: Date.now(), message: action.message, type: action.notificationType }];
    case 'REMOVE_NOTIFICATION':
      return state.filter(notification => notification.id !== action.id);
    case 'CLEAR_ALL':
      return [];
    default:
      return state;
  }
};

function App() {
  // Zustand store
  const { 
    tasks, 
    filteredTasks, 
    stats, 
    loading, 
    error,
    addTask, 
    deleteTask, 
    toggleTaskStatus,
    fetchTasks,
    filter,
    setFilter,
    searchQuery,
    setSearchQuery,
    clearCompleted
  } = useTaskStore();

  // useState examples
  const [darkMode, setDarkMode] = useLocalStorage('darkMode', false);
  const [notifications, dispatchNotifications] = useReducer(notificationReducer, []);
  const [selectedProject, setSelectedProject] = useState(null);
  const [counter, setCounter] = useState(0);
  const [inputValue, setInputValue] = useState('');

  // useRef examples
  const taskInputRef = useRef(null);
  const previousTaskCountRef = useRef(0);
  const renderCountRef = useRef(0);

  // useContext
  const themeContextValue = useMemo(() => ({
    theme: darkMode ? 'dark' : 'light',
    toggleTheme: () => setDarkMode(prev => !prev),
  }), [darkMode, setDarkMode]);

  // useEffect examples
  useEffect(() => {
    // Fetch tasks on component mount
    fetchTasks();
    
    // Focus on task input on mount
    if (taskInputRef.current) {
      taskInputRef.current.focus();
    }
  }, [fetchTasks]);

  useEffect(() => {
    // Update previous task count
    previousTaskCountRef.current = tasks.length;
    
    // Increment render count
    renderCountRef.current += 1;
    
    // Show notification when tasks change
    if (tasks.length > previousTaskCountRef.current) {
      dispatchNotifications({
        type: 'ADD_NOTIFICATION',
        message: 'New task added!',
        notificationType: 'success'
      });
    }
  }, [tasks.length]);

  useEffect(() => {
    // Auto-remove notifications after 5 seconds
    const timer = setTimeout(() => {
      if (notifications.length > 0) {
        dispatchNotifications({ type: 'REMOVE_NOTIFICATION', id: notifications[0].id });
      }
    }, 5000);
    return () => clearTimeout(timer);
  }, [notifications]);

  // useCallback examples
  const handleAddTask = useCallback((taskData) => {
    addTask(taskData);
    setInputValue('');
  }, [addTask]);

  const handleDeleteTask = useCallback((id) => {
    deleteTask(id);
    dispatchNotifications({
      type: 'ADD_NOTIFICATION',
      message: 'Task deleted!',
      notificationType: 'warning'
    });
  }, [deleteTask]);

  const handleToggleStatus = useCallback((id) => {
    toggleTaskStatus(id);
  }, [toggleTaskStatus]);

  // useMemo examples
  const highPriorityTasks = useMemo(() => {
    return tasks.filter(task => task.priority === 'High');
  }, [tasks]);

  const completedTasksPercentage = useMemo(() => {
    return tasks.length > 0 ? Math.round((stats.completed / tasks.length) * 100) : 0;
  }, [tasks.length, stats.completed]);

  const sampleProjects = useMemo(() => [
    { id: 1, name: 'Task Management System', description: 'Full-stack application for managing tasks', progress: completedTasksPercentage },
    { id: 2, name: 'User Authentication Module', description: 'Secure login and registration system', progress: 90 },
    { id: 3, name: 'API Documentation', description: 'Swagger documentation for all endpoints', progress: 60 },
  ], [completedTasksPercentage]);

  const sampleUser = useMemo(() => ({
    name: 'John Doe',
    email: 'john.doe@example.com',
    role: 'Admin',
    joinDate: '2024-01-15'
  }), []);

  // Event handlers
  const handleCounterIncrement = useCallback(() => {
    setCounter(prev => prev + 1);
  }, []);

  const handleCounterDecrement = useCallback(() => {
    setCounter(prev => prev - 1);
  }, []);

  const handleInputChange = useCallback((e) => {
    setInputValue(e.target.value);
  }, []);

  const handleSearchChange = useCallback((e) => {
    setSearchQuery(e.target.value);
  }, [setSearchQuery]);

  const handleFilterChange = useCallback((newFilter) => {
    setFilter(newFilter);
  }, [setFilter]);

  if (loading) {
    return <div className="loading">Loading tasks...</div>;
  }

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  return (
    <ThemeContext.Provider value={themeContextValue}>
      <div className={`App ${darkMode ? 'dark-mode' : ''}`}>
        <Header />
        
        {/* Notifications */}
        <div className="notifications">
          {notifications.map(notification => (
            <div key={notification.id} className={`notification ${notification.type}`}>
              {notification.message}
              <button 
                onClick={() => dispatchNotifications({ type: 'REMOVE_NOTIFICATION', id: notification.id })}
                className="notification-close"
              >
                ×
              </button>
            </div>
          ))}
        </div>

        <div className="container">
          <div className="main-content">
            {/* Demo of hooks */}
            <section className="hooks-demo">
              <h2>React Hooks Demo</h2>
              <div className="hooks-container">
                <div className="hook-item">
                  <h3>useState & useRef</h3>
                  <p>Counter: {counter}</p>
                  <p>Previous task count: {previousTaskCountRef.current}</p>
                  <p>Component renders: {renderCountRef.current}</p>
                  <div className="button-group">
                    <button onClick={handleCounterIncrement} className="btn btn-small">Increment</button>
                    <button onClick={handleCounterDecrement} className="btn btn-small">Decrement</button>
                  </div>
                  <input
                    ref={taskInputRef}
                    type="text"
                    value={inputValue}
                    onChange={handleInputChange}
                    placeholder="Type something..."
                    className="hook-input"
                  />
                </div>
                
                <div className="hook-item">
                  <h3>useMemo & useCallback</h3>
                  <p>High priority tasks: {highPriorityTasks.length}</p>
                  <p>Completion rate: {completedTasksPercentage}%</p>
                  <button onClick={() => handleAddTask({
                    title: `New Task ${tasks.length + 1}`,
                    description: 'Auto-generated task',
                    status: 'Pending',
                    priority: 'Medium'
                  })} className="btn btn-small">
                    Add Sample Task
                  </button>
                </div>
                
                <div className="hook-item">
                  <h3>useReducer</h3>
                  <p>Notifications: {notifications.length}</p>
                  <button 
                    onClick={() => dispatchNotifications({ 
                      type: 'ADD_NOTIFICATION', 
                      message: 'Test notification!',
                      notificationType: 'info'
                    })}
                    className="btn btn-small"
                  >
                    Add Notification
                  </button>
                  <button 
                    onClick={() => dispatchNotifications({ type: 'CLEAR_ALL' })}
                    className="btn btn-small btn-danger"
                  >
                    Clear All
                  </button>
                </div>
              </div>
            </section>

            <section className="tasks-section">
              <div className="section-header">
                <h2>Task Management</h2>
                <div className="task-controls">
                  <div className="search-box">
                    <input
                      type="text"
                      placeholder="Search tasks..."
                      value={searchQuery}
                      onChange={handleSearchChange}
                    />
                  </div>
                  <div className="filter-buttons">
                    {['all', 'pending', 'in-progress', 'completed'].map(f => (
                      <button
                        key={f}
                        className={`filter-btn ${filter === f ? 'active' : ''}`}
                        onClick={() => handleFilterChange(f)}
                      >
                        {f.charAt(0).toUpperCase() + f.slice(1)}
                      </button>
                    ))}
                  </div>
                  <button onClick={clearCompleted} className="btn btn-danger">
                    Clear Completed
                  </button>
                </div>
              </div>
              <div className="tasks-container">
                <TaskForm onAddTask={handleAddTask} />
                <TaskList 
                  tasks={filteredTasks} 
                  onDelete={handleDeleteTask}
                  onToggleStatus={handleToggleStatus}
                />
              </div>
            </section>
            
            <section className="projects-section">
              <h2>Projects</h2>
              <div className="projects-grid">
                {sampleProjects.map(project => (
                  <ProjectCard 
                    key={project.id} 
                    project={project}
                    onSelect={setSelectedProject}
                  />
                ))}
              </div>
            </section>
          </div>
          
          <aside className="sidebar">
            <UserProfile user={sampleUser} />
            <div className="stats">
              <h3>Task Statistics</h3>
              <p>Total Tasks: {stats.total}</p>
              <p>Completed: {stats.completed}</p>
              <p>In Progress: {stats.inProgress}</p>
              <p>Pending: {stats.pending}</p>
              <div className="progress-bar">
                <div 
                  className="progress-fill progress-high"
                  style={{ width: `${completedTasksPercentage}%` }}
                ></div>
              </div>
              <p>Completion: {completedTasksPercentage}%</p>
            </div>
            
            <div className="theme-toggle">
              <button 
                onClick={() => setDarkMode(!darkMode)}
                className="btn btn-block"
              >
                {darkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
              </button>
            </div>
          </aside>
        </div>
      </div>
    </ThemeContext.Provider>
  );
}

export default App;
