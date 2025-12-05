import React from 'react';
import '../styles/App.css';

function TaskList({ tasks, onDelete, onToggleStatus }) {
  const getStatusColor = (status) => {
    switch(status) {
      case 'Completed': return 'status-completed';
      case 'In Progress': return 'status-in-progress';
      case 'Pending': return 'status-pending';
      default: return '';
    }
  };

  const getPriorityColor = (priority) => {
    switch(priority) {
      case 'High': return 'priority-high';
      case 'Medium': return 'priority-medium';
      case 'Low': return 'priority-low';
      default: return '';
    }
  };

  return (
    <div className="task-list">
      <h3>Task List</h3>
      {tasks.length === 0 ? (
        <p className="no-tasks">No tasks available</p>
      ) : (
        <ul className="tasks">
          {tasks.map(task => (
            <li key={task.id} className="task-item">
              <div className="task-header">
                <h4>{task.title}</h4>
                <div className="task-meta">
                  <span className={`priority ${getPriorityColor(task.priority)}`}>
                    {task.priority}
                  </span>
                  <span className={`status ${getStatusColor(task.status)}`}>
                    {task.status}
                  </span>
                </div>
              </div>
              <p className="task-description">{task.description}</p>
              <div className="task-actions">
                <button className="btn btn-small">Edit</button>
                <button 
                  className="btn btn-small btn-danger"
                  onClick={() => onDelete && onDelete(task.id)}
                >
                  Delete
                </button>
                <button 
                  className="btn btn-small btn-success"
                  onClick={() => onToggleStatus && onToggleStatus(task.id)}
                >
                  {task.status === 'Completed' ? 'Mark Pending' : 'Mark Complete'}
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default TaskList;
