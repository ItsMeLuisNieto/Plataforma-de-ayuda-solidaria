import { Outlet, useLocation } from 'react-router-dom'
import { Footer } from './Footer'
import { Navbar } from './Navbar'

export function Layout() {
  const { pathname } = useLocation()
  const esAuth = pathname === '/login' || pathname === '/registro'

  return (
    <div className={esAuth ? 'auth-page login-bg' : undefined}>
      <Navbar />
      <Outlet />
      <Footer />
    </div>
  )
}
