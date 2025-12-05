import React from 'react';
import '../styles/App.css';

function Header() {
  return (
    <header className="header">
      <div className="logo">
        <h1>Task Management System</h1>
        <p>Efficiently manage your projects and tasks</p>
      </div>
      <nav className="nav">
        <ul>
          <li><a href="#dashboard">Dashboard</a></li>
          <li><a href="#tasks">Tasks</a></li>
          <li><a href="#projects">Projects</a></li>
          <li><a href="#team">Team</a></li>
          <li><a href="#reports">Reports</a></li>
        </ul>
      </nav>
      <div className="user-actions">
        <button className="btn btn-primary">New Task</button>
        <button className="btn btn-secondary">Notifications</button>
      </div>
    </header>
  );
}

export default Header;
