import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { questionService } from '../services/api'
import toast from 'react-hot-toast'
import { History, Trash2, ArrowRight, ChevronLeft, ChevronRight } from 'lucide-react'
import './HistoryPage.css'

const PAGE_SIZE = 10

export default function HistoryPage() {
  const [sessions, setSessions]   = useState([])
  const [page, setPage]           = useState(0)
  const [totalPages, setTotal]    = useState(0)
  const [loading, setLoading]     = useState(true)
  const [deleting, setDeleting]   = useState(null)

  const fetchHistory = useCallback(async (p) => {
    setLoading(true)
    try {
      const { data } = await questionService.getHistory(p, PAGE_SIZE)
      // Spring Page or plain array
      if (data.content) {
        setSessions(data.content)
        setTotal(data.totalPages)
      } else {
        setSessions(data)
        setTotal(1)
      }
    } catch { toast.error('Failed to load history') }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchHistory(page) }, [page, fetchHistory])

  const handleDelete = async (sessionId) => {
    if (!window.confirm('Delete this session and all its questions?')) return
    setDeleting(sessionId)
    try {
      await questionService.deleteSession(sessionId)
      toast.success('Session deleted')
      fetchHistory(page)
    } catch { toast.error('Could not delete session') }
    finally { setDeleting(null) }
  }

  const formatDate = (iso) => {
    if (!iso) return ''
    const d = new Date(iso)
    return d.toLocaleDateString('en-IN', { day:'numeric', month:'short', year:'numeric' })
  }

  return (
    <div className="history-page">
      <div className="history-header">
        <h1 className="history-title">History</h1>
        <p className="history-sub">All your past question-generation sessions.</p>
      </div>

      {loading ? (
        <div className="history-list">
          {[1,2,3,4,5].map(i => (
            <div key={i} className="skeleton" style={{ height: 80, borderRadius: 12 }} />
          ))}
        </div>
      ) : sessions.length === 0 ? (
        <div className="empty-state">
          <History size={40} className="empty-state-icon" />
          <p className="empty-state-title">No history yet</p>
          <p className="empty-state-desc">Your generated sessions will appear here.</p>
          <Link to="/generate" className="btn btn-primary" style={{ marginTop: 8 }}>
            Generate now
          </Link>
        </div>
      ) : (
        <>
          <div className="history-list">
            {sessions.map(s => (
              <div key={s.id} className="history-card card">
                <Link to={`/session/${s.id}`} className="history-card-link">
                  <div className="history-card-body">
                    <div className="history-main">
                      <span className="history-role">{s.jobRole}</span>
                      <div className="history-chips">
                        <span className="chip">{s.technology}</span>
                        <span className="chip">{s.experienceLevel}</span>
                        {s.categories?.map(c => (
                          <span key={c} className={`badge badge-${c.toLowerCase()}`}>{c}</span>
                        ))}
                      </div>
                    </div>
                    <div className="history-right">
                      <span className="history-count">{s.questionCount || 0} Qs</span>
                      <span className="history-date">{formatDate(s.createdAt)}</span>
                      <ArrowRight size={16} style={{ color: 'var(--color-secondary)', flexShrink: 0 }} />
                    </div>
                  </div>
                </Link>

                <button
                  className="history-delete"
                  onClick={() => handleDelete(s.id)}
                  disabled={deleting === s.id}
                  aria-label="Delete session"
                >
                  {deleting === s.id
                    ? <span className="spinner" style={{ width: 14, height: 14, borderWidth: 1.5 }} />
                    : <Trash2 size={15} />
                  }
                </button>
              </div>
            ))}
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <button
                className="btn btn-outline btn-sm"
                onClick={() => setPage(p => p - 1)}
                disabled={page === 0}
              >
                <ChevronLeft size={14} /> Prev
              </button>
              <span className="page-info">
                Page {page + 1} of {totalPages}
              </span>
              <button
                className="btn btn-outline btn-sm"
                onClick={() => setPage(p => p + 1)}
                disabled={page >= totalPages - 1}
              >
                Next <ChevronRight size={14} />
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )
}
