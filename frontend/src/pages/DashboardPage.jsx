import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { questionService, favoriteService } from '../services/api'
import { Sparkles, History, Star, ArrowRight, TrendingUp } from 'lucide-react'
import './DashboardPage.css'

const QUICK_ROLES = ['Java Developer','React Developer','Full Stack Developer','Data Analyst','DevOps Engineer']

export default function DashboardPage() {
  const { user } = useAuth()
  const [stats, setStats]     = useState({ sessions: 0, questions: 0, favorites: 0 })
  const [recent, setRecent]   = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      questionService.getHistory(0, 3),
      favoriteService.getAll(0, 1),
    ]).then(([histRes, favRes]) => {
      const sessions = histRes.data
      setRecent(sessions.content || sessions.slice?.(0,3) || [])
      const totalQ = (sessions.content || sessions).reduce((s, x) => s + (x.questionCount || 0), 0)
      setStats({
        sessions: histRes.data.totalElements || (sessions.content?.length ?? sessions.length),
        questions: totalQ,
        favorites: favRes.data.totalElements || 0,
      })
    }).catch(() => {}).finally(() => setLoading(false))
  }, [])

  const hour = new Date().getHours()
  const greeting = hour < 12 ? 'Good morning' : hour < 17 ? 'Good afternoon' : 'Good evening'

  return (
    <div className="dashboard">
      {/* ── Hero ── */}
      <div className="dash-hero">
        <div>
          <h1 className="dash-greeting">{greeting}, {user?.name?.split(' ')[0] || 'there'}</h1>
          <p className="dash-sub">Generate interview questions tailored to any role, tech, and experience level.</p>
        </div>
        <Link to="/generate" className="btn btn-primary btn-lg dash-cta">
          <Sparkles size={16} />
          Generate questions
        </Link>
      </div>

      {/* ── Stats ── */}
      <div className="dash-stats">
        {[
          { label: 'Sessions', value: loading ? '—' : stats.sessions, icon: TrendingUp },
          { label: 'Questions generated', value: loading ? '—' : stats.questions, icon: Sparkles },
          { label: 'Favorites saved', value: loading ? '—' : stats.favorites, icon: Star },
        ].map(({ label, value, icon: Icon }) => (
          <div key={label} className="stat-card card">
            <Icon size={16} className="stat-icon" />
            <span className="stat-value">{value}</span>
            <span className="stat-label">{label}</span>
          </div>
        ))}
      </div>

      {/* ── Quick start ── */}
      <div className="dash-section">
        <h2 className="dash-section-title">Quick start</h2>
        <div className="quick-roles">
          {QUICK_ROLES.map(role => (
            <Link
              key={role}
              to={`/generate?role=${encodeURIComponent(role)}`}
              className="chip chip-link"
            >
              {role}
              <ArrowRight size={12} />
            </Link>
          ))}
        </div>
      </div>

      {/* ── Recent sessions ── */}
      <div className="dash-section">
        <div className="dash-section-header">
          <h2 className="dash-section-title">Recent sessions</h2>
          <Link to="/history" className="btn btn-ghost" style={{display:'flex',alignItems:'center',gap:4}}>
            View all <ArrowRight size={14} />
          </Link>
        </div>

        {loading ? (
          <div className="recent-list">
            {[1,2,3].map(i => <div key={i} className="skeleton" style={{height:72,borderRadius:12}} />)}
          </div>
        ) : recent.length === 0 ? (
          <div className="empty-state" style={{padding:'var(--space-md) 0'}}>
            <History size={36} className="empty-state-icon" />
            <p className="empty-state-title">No sessions yet</p>
            <p className="empty-state-desc">Generate your first set of interview questions to get started.</p>
            <Link to="/generate" className="btn btn-primary" style={{marginTop:8}}>
              <Sparkles size={14} /> Generate now
            </Link>
          </div>
        ) : (
          <div className="recent-list">
            {recent.map(s => (
              <Link key={s.id} to={`/session/${s.id}`} className="recent-card card">
                <div className="recent-card-left">
                  <span className="recent-role">{s.jobRole}</span>
                  <span className="recent-meta">
                    {s.technology} · {s.experienceLevel} · {s.questionCount || 0} questions
                  </span>
                </div>
                <ArrowRight size={16} className="recent-arrow" />
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
