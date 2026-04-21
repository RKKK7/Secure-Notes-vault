import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import NoteCard from '../components/NoteCard';
import NoteModal from '../components/NoteModal';

function Dashboard() {
  const navigate = useNavigate();
  const username = localStorage.getItem('username');
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingNote, setEditingNote] = useState(null);

  const fetchNotes = async () => {
    try {
      const res = await api.get('/notes');
      setNotes(res.data.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotes();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    navigate('/login');
  };

  const handleCreate = () => {
    setEditingNote(null);
    setShowModal(true);
  };

  const handleEdit = (note) => {
    setEditingNote(note);
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this note?')) return;
    try {
      await api.delete(`/notes/${id}`);
      setNotes(notes.filter(n => n.id !== id));
    } catch (err) {
      console.error(err);
    }
  };

  const handleSaved = (savedNote, isEdit) => {
    if (isEdit) {
      setNotes(notes.map(n => n.id === savedNote.id ? savedNote : n));
    } else {
      setNotes([savedNote, ...notes]);
    }
    setShowModal(false);
  };

  return (
    <>
      <nav className="navbar">
        <div className="navbar-brand">
          Secure<span>Vault</span>
        </div>
        <div className="navbar-right">
          <span className="navbar-user">👤 {username}</span>
          <button className="btn-secondary btn-small" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </nav>

      <div className="dashboard-container">
        <div className="dashboard-header">
          <h2>My Notes</h2>
          <button className="btn-primary btn-small" onClick={handleCreate}>
            + New Note
          </button>
        </div>

        {loading ? (
          <div className="loading">Loading notes...</div>
        ) : notes.length === 0 ? (
          <div className="empty-state">
            <p>No notes yet. Create your first secure note!</p>
            <button className="btn-primary btn-small" onClick={handleCreate}>
              + Create Note
            </button>
          </div>
        ) : (
          <div className="notes-grid">
            {notes.map(note => (
              <NoteCard
                key={note.id}
                note={note}
                onEdit={handleEdit}
                onDelete={handleDelete}
              />
            ))}
          </div>
        )}
      </div>

      {showModal && (
        <NoteModal
          note={editingNote}
          onClose={() => setShowModal(false)}
          onSaved={handleSaved}
        />
      )}
    </>
  );
}

export default Dashboard;