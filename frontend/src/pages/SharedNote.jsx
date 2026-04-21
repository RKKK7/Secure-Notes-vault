import { useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api/axios';

function SharedNote() {
  const { token } = useParams();
  const [password, setPassword] = useState('');
  const [note, setNote] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleAccess = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const res = await api.post(`/share/${token}/verify`, { password });
      setNote(res.data.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Access failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="shared-note-container">
      <div className="shared-note-card">
        <h1>Secure<span style={{ color: 'var(--accent)' }}>Vault</span></h1>
        <p className="subtitle">Someone shared a secure note with you</p>

        {!note ? (
          <>
            {error && <div className="error-msg">{error}</div>}
            <form onSubmit={handleAccess}>
              <div className="form-group">
                <label>Enter Password to Access Note</label>
                <input
                  type="password"
                  placeholder="Enter the shared password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
              <button
                type="submit"
                className="btn-primary"
                disabled={loading}
              >
                {loading ? 'Verifying...' : 'Access Note'}
              </button>
            </form>
          </>
        ) : (
          <>
            <span className="readonly-badge">🔒 Read Only</span>
            <div className="note-title-display">{note.title}</div>
            <div className="note-content-box">{note.content}</div>
          </>
        )}
      </div>
    </div>
  );
}

export default SharedNote;