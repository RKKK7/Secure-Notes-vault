import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

function AdminDashboard() {
  const navigate = useNavigate();
  const username = localStorage.getItem('username');
  const [activeTab, setActiveTab] = useState('users');
  const [users, setUsers] = useState([]);
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [usersRes, notesRes] = await Promise.all([
        api.get('/admin/users'),
        api.get('/admin/notes'),
      ]);
      setUsers(usersRes.data.data);
      setNotes(notesRes.data.data);
    } catch (err) {
      setError('Failed to fetch admin data');
    } finally {
      setLoading(false);
    }
  };

  const showSuccess = (msg) => {
    setSuccessMsg(msg);
    setTimeout(() => setSuccessMsg(''), 3000);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    navigate('/login');
  };

  const handleDeleteUser = async (id) => {
    if (!window.confirm('Delete this user and all their notes?')) return;
    try {
      await api.delete('/admin/users/' + id);
      setUsers(users.filter(u => u.id !== id));
      showSuccess('User deleted successfully');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete user');
    }
  };

  const handleDeleteNote = async (id) => {
    if (!window.confirm('Delete this note?')) return;
    try {
      await api.delete('/admin/notes/' + id);
      setNotes(notes.filter(n => n.id !== id));
      showSuccess('Note deleted successfully');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete note');
    }
  };

  const handlePromote = async (id) => {
    if (!window.confirm('Promote this user to ADMIN?')) return;
    try {
      const res = await api.put('/admin/users/' + id + '/promote');
      setUsers(users.map(u => u.id === id ? res.data.data : u));
      showSuccess('User promoted to ADMIN');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to promote user');
    }
  };

  const handleDemote = async (id) => {
    if (!window.confirm('Demote this admin to USER?')) return;
    try {
      const res = await api.put('/admin/users/' + id + '/demote');
      setUsers(users.map(u => u.id === id ? res.data.data : u));
      showSuccess('User demoted to USER');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to demote user');
    }
  };

  const formatDate = (dateStr) => {
    return new Date(dateStr).toLocaleDateString('en-IN', {
      day: '2-digit', month: 'short', year: 'numeric'
    });
  };

  return (
    <>
      {/* Navbar */}
      <nav className="navbar">
        <div className="navbar-brand">
          Secure<span>Vault</span>
          <span className="admin-badge">ADMIN</span>
        </div>
        <div className="navbar-right">
          <span className="navbar-user">👤 {username}</span>
          <button className="btn-secondary btn-small" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </nav>

      <div className="admin-container">
        <div className="admin-header">
          <h2>Admin Panel</h2>
          <button className="btn-secondary btn-small" onClick={fetchData}>
            🔄 Refresh
          </button>
        </div>

        {/* Messages */}
        {error && (
          <div className="error-msg" style={{ marginBottom: '16px' }}>
            {error}
            <button
              onClick={() => setError('')}
              style={{ float: 'right', background: 'none', border: 'none', color: 'inherit', cursor: 'pointer' }}
            >
              ✕
            </button>
          </div>
        )}
        {successMsg && (
          <div className="success-msg" style={{ marginBottom: '16px' }}>
            {successMsg}
          </div>
        )}

        {/* Stats Row */}
        <div className="admin-stats">
          <div className="stat-card">
            <div className="stat-number">{users.length}</div>
            <div className="stat-label">Total Users</div>
          </div>
          <div className="stat-card">
            <div className="stat-number">{users.filter(u => u.role === 'ADMIN').length}</div>
            <div className="stat-label">Admins</div>
          </div>
          <div className="stat-card">
            <div className="stat-number">{notes.length}</div>
            <div className="stat-label">Total Notes</div>
          </div>
        </div>

        {/* Tabs */}
        <div className="admin-tabs">
          <button
            className={'tab-btn' + (activeTab === 'users' ? ' active' : '')}
            onClick={() => setActiveTab('users')}
          >
            👥 Users ({users.length})
          </button>
          <button
            className={'tab-btn' + (activeTab === 'notes' ? ' active' : '')}
            onClick={() => setActiveTab('notes')}
          >
            📝 Notes ({notes.length})
          </button>
        </div>

        {/* Tab Content */}
        {loading ? (
          <div className="loading">Loading admin data...</div>
        ) : (
          <div className="admin-table-wrapper">

            {/* Users Tab */}
            {activeTab === 'users' && (
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Role</th>
                    <th>Notes</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {users.length === 0 ? (
                    <tr>
                      <td colSpan="5" style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>
                        No users found
                      </td>
                    </tr>
                  ) : (
                    users.map(user => (
                      <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.username}</td>
                        <td>
                          <span className={'role-badge ' + (user.role === 'ADMIN' ? 'role-admin' : 'role-user')}>
                            {user.role}
                          </span>
                        </td>
                        <td>{user.noteCount}</td>
                        <td>
                          <div className="action-btns">
                            {user.role === 'USER' ? (
                              <button
                                className="btn-secondary btn-small"
                                onClick={() => handlePromote(user.id)}
                              >
                                ⬆️ Promote
                              </button>
                            ) : (
                              <button
                                className="btn-secondary btn-small"
                                onClick={() => handleDemote(user.id)}
                                disabled={user.username === username}
                              >
                                ⬇️ Demote
                              </button>
                            )}
                            <button
                              className="btn-danger btn-small"
                              onClick={() => handleDeleteUser(user.id)}
                              disabled={user.role === 'ADMIN'}
                              title={user.role === 'ADMIN' ? 'Cannot delete admin' : ''}
                            >
                              🗑️ Delete
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            )}

            {/* Notes Tab */}
            {activeTab === 'notes' && (
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Owner</th>
                    <th>Preview</th>
                    <th>Created</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {notes.length === 0 ? (
                    <tr>
                      <td colSpan="6" style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>
                        No notes found
                      </td>
                    </tr>
                  ) : (
                    notes.map(note => (
                      <tr key={note.id}>
                        <td>{note.id}</td>
                        <td>{note.title}</td>
                        <td>
                          <span className="owner-tag">{note.owner}</span>
                        </td>
                        <td className="note-preview">
                          {note.content.length > 40
                            ? note.content.substring(0, 40) + '...'
                            : note.content}
                        </td>
                        <td>{formatDate(note.createdAt)}</td>
                        <td>
                          <button
                            className="btn-danger btn-small"
                            onClick={() => handleDeleteNote(note.id)}
                          >
                            🗑️ Delete
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            )}
          </div>
        )}
      </div>
    </>
  );
}

export default AdminDashboard;
