import React from 'react';
import '../styles/App.css';

function UserProfile({ user }) {
  const getRoleColor = (role) => {
    switch(role) {
      case 'Admin': return 'role-admin';
      case 'Manager': return 'role-manager';
      case 'Developer': return 'role-developer';
      default: return 'role-user';
    }
  };

  return (
    <div className="user-profile">
      <div className="profile-header">
        <div className="avatar">
          <span>{user.name.charAt(0)}</span>
        </div>
        <div className="profile-info">
          <h3>{user.name}</h3>
          <p className={`user-role ${getRoleColor(user.role)}`}>{user.role}</p>
        </div>
      </div>
      
      <div className="profile-details">
        <div className="detail-item">
          <span className="detail-label">Email:</span>
          <span className="detail-value">{user.email}</span>
        </div>
        <div className="detail-item">
          <span className="detail-label">Member Since:</span>
          <span className="detail-value">{user.joinDate}</span>
        </div>
        <div className="detail-item">
          <span className="detail-label">Status:</span>
          <span className="detail-value status-active">Active</span>
        </div>
      </div>
      
      <div className="profile-stats">
        <div className="stat">
          <span className="stat-number">12</span>
          <span className="stat-label">Projects</span>
        </div>
        <div className="stat">
          <span className="stat-number">47</span>
          <span className="stat-label">Tasks</span>
        </div>
        <div className="stat">
          <span className="stat-number">89%</span>
          <span className="stat-label">Completion</span>
        </div>
      </div>
      
      <div className="profile-actions">
        <button className="btn btn-primary btn-block">Edit Profile</button>
        <button className="btn btn-secondary btn-block">View Activity</button>
      </div>
    </div>
  );
}

export default UserProfile;
