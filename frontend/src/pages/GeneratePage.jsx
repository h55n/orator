import { useState, useEffect } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { questionService } from '../services/api'
import toast from 'react-hot-toast'
import { Sparkles, ChevronDown } from 'lucide-react'
import './GeneratePage.css'

const JOB_ROLES = [
  'Java Developer','React Developer','Full Stack Developer','Frontend Developer',
  'Backend Developer','Data Analyst','Data Engineer','DevOps Engineer',
  'Cloud Engineer','Android Developer','iOS Developer','QA Engineer',
  'Machine Learning Engineer','Business Analyst','System Architect'
]

const TECHNOLOGIES = [
  'Java','Spring Boot','React.js','Angular','Vue.js','Node.js',
  'Python','Django','FastAPI','MySQL','PostgreSQL','MongoDB',
  'Redis','Kafka','Docker','Kubernetes','AWS','Azure','GCP',
  'Git','REST APIs','GraphQL','Microservices','TypeScript','C++','Go'
]

const EXPERIENCE = ['Fresher','Intermediate','Experienced']

const CATEGORIES = ['Technical','Coding','SQL','HR','Aptitude']

export default function GeneratePage() {
  const [searchParams] = useSearchParams()
  const navigate       = useNavigate()

  const [form, setForm] = useState({
    jobRole:         searchParams.get('role') || '',
    technology:      '',
    experienceLevel: '',
    categories:      [],
    questionCount:   10,
  })
  const [errors, setErrors]   = useState({})
  const [loading, setLoading] = useState(false)

  const toggle = (field, value) => {
    setForm(f => {
      const arr = f[field]
      return { ...f, [field]: arr.includes(value) ? arr.filter(x => x !== value) : [...arr, value] }
    })
    if (errors[field]) setErrors(e => ({ ...e, [field]: '' }))
  }

  const validate = () => {
    const e = {}
    if (!form.jobRole)         e.jobRole         = 'Select a job role'
    if (!form.technology)      e.technology      = 'Select a technology'
    if (!form.experienceLevel) e.experienceLevel = 'Select experience level'
    if (!form.categories.length) e.categories    = 'Select at least one category'
    return e
  }

  const handleGenerate = async () => {
    const errs = validate()
    if (Object.keys(errs).length) { setErrors(errs); return }
    setLoading(true)
    try {
      const { data } = await questionService.generate(form)
      toast.success(`Generated ${data.questions?.length || form.questionCount} questions!`)
      navigate(`/session/${data.sessionId}`)
    } catch (err) {
      toast.error(err.response?.data?.message || 'Generation failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const set = (field) => (e) => {
    setForm(f => ({ ...f, [field]: e.target.value }))
    if (errors[field]) setErrors(er => ({ ...er, [field]: '' }))
  }

  return (
    <div className="generate-page">
      <div className="generate-header">
        <h1 className="generate-title">Generate questions</h1>
        <p className="generate-sub">Configure what you need — AI generates targeted, categorized questions instantly.</p>
      </div>

      <div className="generate-layout">
        {/* ── Form ── */}
        <div className="generate-form card">

          {/* Job Role */}
          <div className="form-group">
            <label className="form-label">Job role <span className="req">*</span></label>
            <div className="select-wrap">
              <select className={`form-select ${errors.jobRole ? 'error' : ''}`}
                value={form.jobRole} onChange={set('jobRole')}>
                <option value="">Select role…</option>
                {JOB_ROLES.map(r => <option key={r}>{r}</option>)}
              </select>
              <ChevronDown size={14} className="select-icon" />
            </div>
            {errors.jobRole && <span className="form-error">{errors.jobRole}</span>}
          </div>

          {/* Technology */}
          <div className="form-group">
            <label className="form-label">Technology / Skill <span className="req">*</span></label>
            <div className="select-wrap">
              <select className={`form-select ${errors.technology ? 'error' : ''}`}
                value={form.technology} onChange={set('technology')}>
                <option value="">Select technology…</option>
                {TECHNOLOGIES.map(t => <option key={t}>{t}</option>)}
              </select>
              <ChevronDown size={14} className="select-icon" />
            </div>
            {errors.technology && <span className="form-error">{errors.technology}</span>}
          </div>

          {/* Experience Level */}
          <div className="form-group">
            <label className="form-label">Experience level <span className="req">*</span></label>
            <div className="exp-chips">
              {EXPERIENCE.map(level => (
                <button
                  key={level}
                  type="button"
                  className={`exp-chip ${form.experienceLevel === level ? 'active' : ''}`}
                  onClick={() => { setForm(f => ({...f, experienceLevel: level})); setErrors(e=>({...e,experienceLevel:''})) }}
                >
                  {level}
                </button>
              ))}
            </div>
            {errors.experienceLevel && <span className="form-error">{errors.experienceLevel}</span>}
          </div>

          {/* Categories */}
          <div className="form-group">
            <label className="form-label">Question categories <span className="req">*</span></label>
            <p className="form-hint">Select one or more</p>
            <div className="cat-chips">
              {CATEGORIES.map(cat => (
                <button
                  key={cat}
                  type="button"
                  className={`cat-chip ${form.categories.includes(cat) ? 'active' : ''}`}
                  onClick={() => toggle('categories', cat)}
                >
                  {cat}
                </button>
              ))}
            </div>
            {errors.categories && <span className="form-error">{errors.categories}</span>}
          </div>

          {/* Question count */}
          <div className="form-group">
            <label className="form-label">
              Number of questions
              <span className="count-badge">{form.questionCount}</span>
            </label>
            <input
              type="range" min={5} max={30} step={5}
              className="range-input"
              value={form.questionCount}
              onChange={e => setForm(f => ({ ...f, questionCount: Number(e.target.value) }))}
            />
            <div className="range-labels">
              <span>5</span><span>10</span><span>15</span><span>20</span><span>25</span><span>30</span>
            </div>
          </div>

          <button className="btn btn-primary btn-lg generate-btn" onClick={handleGenerate} disabled={loading}>
            {loading
              ? <><span className="spinner" /> Generating…</>
              : <><Sparkles size={16} /> Generate questions</>
            }
          </button>
        </div>

        {/* ── Info panel ── */}
        <div className="generate-info">
          <div className="card-accent info-card">
            <h3 className="info-title">How it works</h3>
            <ol className="info-list">
              <li>Pick your role, tech stack, and level</li>
              <li>Choose which categories you need</li>
              <li>AI generates precise, structured questions</li>
              <li>Save favorites, revisit any time</li>
            </ol>
          </div>

          <div className="card info-card">
            <h3 className="info-title">Question categories</h3>
            <ul className="info-cats">
              {[
                ['Technical',  'Core concepts, architecture, system design'],
                ['Coding',     'DSA, patterns, implementation problems'],
                ['SQL',        'Queries, joins, optimization, design'],
                ['HR',         'Behavioural, situational, culture fit'],
                ['Aptitude',   'Logical, analytical, problem-solving'],
              ].map(([cat, desc]) => (
                <li key={cat} className="info-cat-row">
                  <span className={`badge badge-${cat.toLowerCase()}`}>{cat}</span>
                  <span className="info-cat-desc">{desc}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </div>
  )
}
