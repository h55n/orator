import { useState, useEffect, useCallback } from 'react'
import { favoriteService } from '../services/api'
import toast from 'react-hot-toast'
import { Star, Search, ChevronLeft, ChevronRight } from 'lucide-react'
import './FavoritesPage.css'

const PAGE_SIZE = 20

export default function FavoritesPage() {
  const [questions, setQuestions] = useState([])
  const [page, setPage]           = useState(0)
  const [totalPages, setTotal]    = useState(0)
  const [loading, setLoading]     = useState(true)
  const [removing, setRemoving]   = useState(null)
  const [search, setSearch]       = useState('')

  const fetchFavs = useCallback(async (p) => {
    setLoading(true)
    try {
      const { data } = await favoriteService.getAll(p, PAGE_SIZE)
      if (data.content) {
        setQuestions(data.content)
        setTotal(data.totalPages)
      } else {
        setQuestions(data)
        setTotal(1)
      }
    } catch { toast.error('Failed to load favorites') }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchFavs(page) }, [page, fetchFavs])

  const handleRemove = async (questionId) => {
    setRemoving(questionId)
    try {
      await favoriteService.remove(questionId)
      setQuestions(qs => qs.filter(q => q.id !== questionId))
      toast.success('Removed from favorites')
    } catch { toast.error('Could not remove') }
    finally { setRemoving(null) }
  }

  const filtered = questions.filter(q =>
    !search || q.question.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div className="fav-page">
      <div className="fav-header">
        <h1 className="fav-title">Favorites</h1>
        <p className="fav-sub">Questions you've saved for quick review.</p>
      </div>

      {!loading && questions.length > 0 && (
        <div className="search-wrap" style={{ maxWidth: 480 }}>
          <Search size={15} className="search-icon" />
          <input
            type="text"
            className="form-input search-input"
            placeholder="Search saved questions…"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
      )}

      {loading ? (
        <div className="fav-list">
          {[1,2,3,4].map(i => (
            <div key={i} className="skeleton" style={{ height: 90, borderRadius: 12 }} />
          ))}
        </div>
      ) : questions.length === 0 ? (
        <div className="empty-state">
          <Star size={40} className="empty-state-icon" />
          <p className="empty-state-title">No favorites yet</p>
          <p className="empty-state-desc">Star any question while reviewing a session to save it here.</p>
        </div>
      ) : filtered.length === 0 ? (
        <div className="empty-state" style={{ padding: 'var(--space-md) 0' }}>
          <Search size={32} className="empty-state-icon" />
          <p className="empty-state-title">No matches</p>
          <p className="empty-state-desc">Try a different search term.</p>
        </div>
      ) : (
        <>
          <div className="fav-list">
            {filtered.map(q => (
              <div key={q.id} className="fav-card card">
                <div className="fav-top">
                  <div className="fav-badges">
                    <span className={`badge badge-${q.difficulty?.toLowerCase()}`}>{q.difficulty}</span>
                    <span className={`badge badge-${q.category?.toLowerCase()}`}>{q.category}</span>
                    {q.technology && <span className="chip" style={{ fontSize: 11 }}>{q.technology}</span>}
                  </div>
                  <button
                    className="fav-remove"
                    onClick={() => handleRemove(q.id)}
                    disabled={removing === q.id}
                    aria-label="Remove from favorites"
                  >
                    {removing === q.id
                      ? <span className="spinner" style={{ width: 14, height: 14, borderWidth: 1.5 }} />
                      : <Star size={15} fill="currentColor" />
                    }
                  </button>
                </div>
                <p className="fav-text">{q.question}</p>
                {q.hint && (
                  <details className="q-hint">
                    <summary>Hint / Key points</summary>
                    <p>{q.hint}</p>
                  </details>
                )}
                {q.jobRole && (
                  <p className="fav-source">
                    From: {q.jobRole} session
                  </p>
                )}
              </div>
            ))}
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <button className="btn btn-outline btn-sm" onClick={() => setPage(p => p - 1)} disabled={page === 0}>
                <ChevronLeft size={14} /> Prev
              </button>
              <span className="page-info">Page {page + 1} of {totalPages}</span>
              <button className="btn btn-outline btn-sm" onClick={() => setPage(p => p + 1)} disabled={page >= totalPages - 1}>
                Next <ChevronRight size={14} />
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )
}
