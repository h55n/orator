import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { useAuth } from '../../hooks/useAuth'
import {
  LayoutDashboard, Sparkles, History, Star,
  LogOut, Menu, X, ChevronRight
} from 'lucide-react'
import './AppLayout.css'

const NAV = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/generate',  icon: Sparkles,        label: 'Generate'  },
  { to: '/history',   icon: History,          label: 'History'   },
  { to: '/favorites', icon: Star,             label: 'Favorites' },
]

export default function AppLayout() {
  const { user, logout }    = useAuth()
  const navigate             = useNavigate()
  const [open, setOpen]      = useState(false)

  const handleLogout = () => { logout(); navigate('/login') }

  return (
    <div className="app-layout">
      {/* ── Mobile overlay ── */}
      {open && <div className="sidebar-overlay" onClick={() => setOpen(false)} />}

      {/* ── Sidebar ── */}
      <aside className={`sidebar ${open ? 'open' : ''}`}>
        <div className="sidebar-header">
          <div className="logo">
            <span className="logo-icon">⬡</span>
            <span className="logo-text">InterviewGen</span>
          </div>
          <button className="sidebar-close" onClick={() => setOpen(false)} aria-label="Close menu">
            <X size={18} />
          </button>
        </div>

        <nav className="sidebar-nav">
          {NAV.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
              onClick={() => setOpen(false)}
            >
              <Icon size={16} />
              <span>{label}</span>
              <ChevronRight size={14} className="nav-arrow" />
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="user-row">
            <div className="user-avatar">{user?.name?.[0]?.toUpperCase() || 'U'}</div>
            <div className="user-info">
              <p className="user-name">{user?.name || 'User'}</p>
              <p className="user-email">{user?.email || ''}</p>
            </div>
          </div>
          <button className="logout-btn" onClick={handleLogout} aria-label="Log out">
            <LogOut size={16} />
          </button>
        </div>
      </aside>

      {/* ── Main content ── */}
      <div className="main-wrapper">
        {/* Mobile topbar */}
        <header className="mobile-topbar">
          <button className="menu-toggle" onClick={() => setOpen(true)} aria-label="Open menu">
            <Menu size={20} />
          </button>
          <span className="logo-text">InterviewGen</span>
          <div className="user-avatar sm">{user?.name?.[0]?.toUpperCase() || 'U'}</div>
        </header>

        <main className="main-content">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
