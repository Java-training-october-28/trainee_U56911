import React from 'react';
import '../styles/App.css';

function ProjectCard({ project, onSelect }) {
  const getProgressColor = (progress) => {
    if (progress >= 75) return 'progress-high';
    if (progress >= 50) return 'progress-medium';
    return 'progress-low';
  };

  return (
    <div className="project-card">
      <div className="project-header">
        <h4>{project.name}</h4>
        <span className="project-id">#{project.id}</span>
      </div>
      <p className="project-description">{project.description}</p>
      
      <div className="project-progress">
        <div className="progress-label">
          <span>Progress</span>
          <span>{project.progress}%</span>
        </div>
        <div className="progress-bar">
          <div 
            className={`progress-fill ${getProgressColor(project.progress)}`}
            style={{ width: `${project.progress}%` }}
          ></div>
        </div>
      </div>
      
      <div className="project-actions">
        <button 
          className="btn btn-small"
          onClick={() => onSelect && onSelect(project)}
        >
          View Details
        </button>
        <button className="btn btn-small btn-primary">Edit</button>
      </div>
      
      <div className="project-meta">
        <span className="meta-item">
          <i className="icon">📅</i> Updated: Today
        </span>
        <span className="meta-item">
          <i className="icon">👥</i> 5 members
        </span>
      </div>
    </div>
  );
}

export default ProjectCard;
