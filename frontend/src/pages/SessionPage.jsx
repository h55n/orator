import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { questionService, favoriteService } from '../services/api'
import toast from 'react-hot-toast'
import { ArrowLeft, Star, Search, Filter, Copy, Check, Printer } from 'lucide-react'
import './SessionPage.css'

const CATEGORIES   = ['All', 'Technical', 'Coding', 'SQL', 'HR', 'Aptitude']
const DIFFICULTIES = ['All', 'Easy', 'Medium', 'Hard']

// ── Skeleton card shown while loading ─────────────────────────
function SkeletonCard() {
  return (
    <div className="question-card card skeleton-card" aria-hidden="true">
      <div className="q-top">
        <div className="q-badges">
          <span className="skeleton-pill" style={{ width: 54 }} />
          <span className="skeleton-pill" style={{ width: 68 }} />
        </div>
        <span className="skeleton-circle" />
      </div>
      <span className="skeleton-line" style={{ width: '12%' }} />
      <span className="skeleton-line" style={{ width: '95%' }} />
      <span className="skeleton-line" style={{ width: '80%' }} />
      <span className="skeleton-line" style={{ width: '60%' }} />
    </div>
  )
}

// ── Copy button with transient "copied" confirmation ──────────
function CopyBtn({ text }) {
  const [copied, setCopied] = useState(false)

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(text)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    } catch {
      toast.error('Could not copy to clipboard')
    }
  }

  return (
    <button
      className={`copy-btn ${copied ? 'copied' : ''}`}
      onClick={handleCopy}
      aria-label={copied ? 'Copied!' : 'Copy question'}
      title={copied ? 'Copied!' : 'Copy question'}
    >
      {copied ? <Check size={14} /> : <Copy size={14} />}
    </button>
  )
}

// ── Main component ────────────────────────────────────────────
export default function SessionPage() {
  const { id }   = useParams()
  const navigate = useNavigate()

  const [session,    setSession]    = useState(null)
  const [loading,    setLoading]    = useState(true)
  const [favorites,  setFavorites]  = useState(new Set())
  const [favLoading, setFavLoading] = useState(new Set())
  const [catFilter,  setCatFilter]  = useState('All')
  const [diffFilter, setDiffFilter] = useState('All')
  const [search,     setSearch]     = useState('')

  useEffect(() => {
    questionService.getSessionById(id)
      .then(({ data }) => {
        setSession(data)
        const favSet = new Set(data.questions?.filter(q => q.isFavorite).map(q => q.id))
        setFavorites(favSet)
      })
      .catch(() => { toast.error('Session not found'); navigate('/history') })
      .finally(() => setLoading(false))
  }, [id])

  const toggleFav = async (questionId) => {
    if (favLoading.has(questionId)) return
    setFavLoading(s => new Set([...s, questionId]))
    try {
      if (favorites.has(questionId)) {
        await favoriteService.remove(questionId)
        setFavorites(s => { const n = new Set(s); n.delete(questionId); return n })
        toast.success('Removed from favorites')
      } else {
        await favoriteService.add(questionId)
        setFavorites(s => new Set([...s, questionId]))
        toast.success('Saved to favorites')
      }
    } catch { toast.error('Could not update favorite') }
    finally { setFavLoading(s => { const n = new Set(s); n.delete(questionId); return n }) }
  }

  const handlePrint = () => {
    window.print()
  }

  const filtered = session?.questions?.filter(q => {
    const catOk  = catFilter  === 'All' || q.category  === catFilter
    const diffOk = diffFilter === 'All' || q.difficulty === diffFilter
    const srchOk = !search || q.question.toLowerCase().includes(search.toLowerCase())
    return catOk && diffOk && srchOk
  }) ?? []

  // ── Skeleton loading state ────────────────────────────────
  if (loading) return (
    <div className="session-page">
      <div className="session-header">
        <button className="btn btn-ghost session-back" onClick={() => navigate(-1)}>
          <ArrowLeft size={16} /> Back
        </button>
        <div className="session-meta">
          <span className="skeleton-line" style={{ width: 200, height: 28, borderRadius: 6 }} />
          <div className="session-tags" style={{ marginTop: 8 }}>
            <span className="skeleton-pill" style={{ width: 80 }} />
            <span className="skeleton-pill" style={{ width: 96 }} />
          </div>
        </div>
      </div>
      <div className="questions-list" style={{ marginTop: 8 }}>
        {Array.from({ length: 6 }).map((_, i) => <SkeletonCard key={i} />)}
      </div>
    </div>
  )

  return (
    <div className="session-page">
      {/* Header */}
      <div className="session-header">
        <button className="btn btn-ghost session-back" onClick={() => navigate(-1)}>
          <ArrowLeft size={16} /> Back
        </button>
        <div className="session-header-row">
          <div className="session-meta">
            <h1 className="session-title">{session?.jobRole}</h1>
            <div className="session-tags">
              <span className="chip">{session?.technology}</span>
              <span className="chip">{session?.experienceLevel}</span>
              <span className="chip">{filtered.length} / {session?.questions?.length} shown</span>
            </div>
          </div>
          <button
            className="btn btn-ghost print-btn"
            onClick={handlePrint}
            aria-label="Export to PDF"
            title="Print / Save as PDF"
          >
            <Printer size={15} />
            <span className="print-btn-label">Export PDF</span>
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="session-filters">
        <div className="search-wrap">
          <Search size={15} className="search-icon" />
          <input
            type="text"
            className="form-input search-input"
            placeholder="Search questions…"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>

        <div className="filter-row">
          <Filter size={14} style={{ color: 'var(--color-secondary)', flexShrink: 0 }} />
          <div className="filter-chips">
            {CATEGORIES.map(c => (
              <button key={c} className={`chip ${catFilter === c ? 'active' : ''}`} onClick={() => setCatFilter(c)}>{c}</button>
            ))}
          </div>
          <div className="filter-divider" />
          <div className="filter-chips">
            {DIFFICULTIES.map(d => (
              <button key={d} className={`chip ${diffFilter === d ? 'active' : ''}`} onClick={() => setDiffFilter(d)}>{d}</button>
            ))}
          </div>
        </div>
      </div>

      {/* Questions */}
      {filtered.length === 0 ? (
        <div className="empty-state">
          <Search size={36} className="empty-state-icon" />
          <p className="empty-state-title">No questions match</p>
          <p className="empty-state-desc">Try adjusting your filters or search term.</p>
        </div>
      ) : (
        <div className="questions-list">
          {filtered.map((q, idx) => (
            <div key={q.id} className="question-card card">
              <div className="q-top">
                <div className="q-badges">
                  <span className={`badge badge-${q.difficulty?.toLowerCase()}`}>{q.difficulty}</span>
                  <span className={`badge badge-${q.category?.toLowerCase()}`}>{q.category}</span>
                </div>
                <div className="q-actions">
                  <CopyBtn text={q.question} />
                  <button
                    className={`fav-btn ${favorites.has(q.id) ? 'active' : ''}`}
                    onClick={() => toggleFav(q.id)}
                    disabled={favLoading.has(q.id)}
                    aria-label={favorites.has(q.id) ? 'Remove from favorites' : 'Save to favorites'}
                  >
                    {favLoading.has(q.id)
                      ? <span className="spinner" style={{ width: 14, height: 14, borderWidth: 1.5 }} />
                      : <Star size={15} fill={favorites.has(q.id) ? 'currentColor' : 'none'} />
                    }
                  </button>
                </div>
              </div>
              <p className="q-number">Q{idx + 1}</p>
              <p className="q-text">{q.question}</p>
              {q.hint && (
                <details className="q-hint">
                  <summary>Hint / Key points</summary>
                  <p>{q.hint}</p>
                </details>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
