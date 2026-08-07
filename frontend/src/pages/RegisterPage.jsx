import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import toast from 'react-hot-toast'
import './AuthPages.css'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate     = useNavigate()
  const [form, setForm]     = useState({ name: '', email: '', password: '', confirm: '' })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)

  const validate = () => {
    const e = {}
    if (!form.name || form.name.trim().length < 2) e.name = 'Name must be at least 2 characters'
    if (!form.email || !/\S+@\S+\.\S+/.test(form.email)) e.email = 'Valid email is required'
    if (!form.password || form.password.length < 8) e.password = 'Password must be at least 8 characters'
    if (form.password !== form.confirm) e.confirm = 'Passwords do not match'
    return e
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const errs = validate()
    if (Object.keys(errs).length) { setErrors(errs); return }
    setLoading(true)
    try {
      await register({ name: form.name, email: form.email, password: form.password })
      navigate('/dashboard')
      toast.success('Account created! Welcome.')
    } catch (err) {
      const msg = err.response?.data?.message || 'Registration failed. Try again.'
      toast.error(msg)
    } finally {
      setLoading(false)
    }
  }

  const set = (field) => (e) => {
    setForm(f => ({ ...f, [field]: e.target.value }))
    if (errors[field]) setErrors(er => ({ ...er, [field]: '' }))
  }

  return (
    <div className="auth-shell">
      <div className="auth-brand">
        <span className="auth-logo-icon">⬡</span>
        <span className="auth-logo-text">InterviewGen</span>
      </div>

      <div className="auth-card">
        <div className="auth-card-header">
          <h1 className="auth-title">Create account</h1>
          <p className="auth-subtitle">Start generating interview questions for free</p>
        </div>

        <form onSubmit={handleSubmit} noValidate>
          <div className="auth-fields">
            <div className="form-group">
              <label className="form-label" htmlFor="name">Full name</label>
              <input id="name" type="text" className={`form-input ${errors.name ? 'error' : ''}`}
                placeholder="Rahul Sharma" value={form.name} onChange={set('name')} autoComplete="name" />
              {errors.name && <span className="form-error">{errors.name}</span>}
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="email">Email</label>
              <input id="email" type="email" className={`form-input ${errors.email ? 'error' : ''}`}
                placeholder="you@example.com" value={form.email} onChange={set('email')} autoComplete="email" />
              {errors.email && <span className="form-error">{errors.email}</span>}
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="password">Password</label>
              <input id="password" type="password" className={`form-input ${errors.password ? 'error' : ''}`}
                placeholder="Min. 8 characters" value={form.password} onChange={set('password')} autoComplete="new-password" />
              {errors.password && <span className="form-error">{errors.password}</span>}
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="confirm">Confirm password</label>
              <input id="confirm" type="password" className={`form-input ${errors.confirm ? 'error' : ''}`}
                placeholder="••••••••" value={form.confirm} onChange={set('confirm')} autoComplete="new-password" />
              {errors.confirm && <span className="form-error">{errors.confirm}</span>}
            </div>
          </div>

          <button type="submit" className="btn btn-primary btn-lg auth-submit" disabled={loading}>
            {loading ? <span className="spinner" /> : 'Create account'}
          </button>
        </form>

        <p className="auth-footer">
          Already have an account?{' '}
          <Link to="/login" className="auth-link">Sign in</Link>
        </p>
      </div>

      <p className="auth-tagline">No credit card required — always free.</p>
    </div>
  )
}
