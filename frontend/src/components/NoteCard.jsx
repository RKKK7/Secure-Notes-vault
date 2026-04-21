import { useState } from 'react';
import api from '../api/axios';

function NoteCard({ note, onEdit, onDelete }) {
  const [showShare, setShowShare] = useState(false);
  const [shareData, setShareData] = useState(null);
  const [shareForm, setShareForm] = useState({ password: '', expiryMinutes: 10 });
  const [shareLoading, setShareLoading] = useState(false);
  const [shareError, setShareError] = useState('');

  const formatDate = (dateStr) => {
    return new Date(dateStr).toLocaleDateString('en-IN', {
      day: '2-digit', month: 'short', year: 'numeric'
    });
  };

  const handleShareSubmit = async (e) => {
    e.preventDefault();
    setShareLoading(true);
    setShareError('');
    try {
      const res = await api.post('/share/create/' + note.id, shareForm);
      setShareData(res.data.data);
    } catch (err) {
      setShareError(err.response?.data?.message || 'Failed to create share link');
    } finally {
      setShareLoading(false);
    }
  };

  const handleCopy = () => {
    const url = 'http://localhost:5173/share/' + shareData.token;
    navigator.clipboard.writeText(url);
    alert('Link copied to clipboard!');
  };

  const handleOpenLink = () => {
    const url = 'http://localhost:5173/share/' + shareData.token;
    window.open(url, '_blank');
  };

  return (
    <div className="note-card">
      <h3>{note.title}</h3>
      <p>{note.content}</p>

      <div className="note-card-footer">
        <span className="note-card-date">{formatDate(note.createdAt)}</span>
        <div className="note-card-actions">
          <button
            className="btn-secondary btn-small"
            onClick={() => {
              setShowShare(!showShare);
              setShareData(null);
              setShareError('');
            }}
          >
            🔗
          </button>
          <button
            className="btn-secondary btn-small"
            onClick={() => onEdit(note)}
          >
            ✏️
          </button>
          <button
            className="btn-danger btn-small"
            onClick={() => onDelete(note.id)}
          >
            🗑️
          </button>
        </div>
      </div>

      {showShare && (
        <div className="share-section">
          {!shareData ? (
            <div>
              <h4>Generate Share Link</h4>
              {shareError && <div className="error-msg">{shareError}</div>}
              <form onSubmit={handleShareSubmit}>
                <div className="form-group">
                  <label>Password</label>
                  <input
                    type="text"
                    placeholder="e.g. 010203"
                    value={shareForm.password}
                    onChange={e => setShareForm({
                      ...shareForm, password: e.target.value
                    })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Expires in (minutes)</label>
                  <input
                    type="number"
                    min="1"
                    max="1440"
                    value={shareForm.expiryMinutes}
                    onChange={e => setShareForm({
                      ...shareForm, expiryMinutes: parseInt(e.target.value)
                    })}
                    required
                  />
                </div>
                <button
                  type="submit"
                  className="btn-primary"
                  disabled={shareLoading}
                >
                  {shareLoading ? 'Generating...' : 'Generate Link'}
                </button>
              </form>
            </div>
          ) : (
            <div>
              <h4>Share Link Ready ✅</h4>
              <div
                className="share-url-box"
                onClick={handleOpenLink}
                style={{ cursor: 'pointer' }}
              >
                {'http://localhost:5173/share/' + shareData.token}
              </div>
              <p className="share-expiry">
                Expires: {new Date(shareData.expiryTime).toLocaleString()}
              </p>
              <br />
              <div style={{ display: 'flex', gap: '8px' }}>
                <button
                  className="btn-secondary btn-small"
                  onClick={handleOpenLink}
                >
                  🔗 Open Link
                </button>
                <button
                  className="btn-secondary btn-small"
                  onClick={handleCopy}
                >
                  📋 Copy Link
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default NoteCard;