import React, { useContext, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../../auth/AuthContext';
import { Collapse } from 'bootstrap';


const Navbar: React.FC = () => {
  const { isAuthenticated, hasRole, user, logout } = useContext(AuthContext);
  const navbarCollapseRef = useRef<HTMLDivElement>(null);
  const togglerRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (navbarCollapseRef.current?.classList.contains('show') && 
          !navbarCollapseRef.current.contains(event.target as Node) && 
          !togglerRef.current?.contains(event.target as Node)) {
        closeMenu();
      }
    };

    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  const closeMenu = () => {
    if (navbarCollapseRef.current) {
      const collapseInstance = new Collapse(navbarCollapseRef.current, {
        toggle: false
      });
      collapseInstance.hide();
    }
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark py-3">
      <div className="container-fluid">
        <Link className="navbar-brand" to="/">Coffeetica</Link>
        <button
          ref={togglerRef}
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav" ref={navbarCollapseRef}>
          <ul className="navbar-nav me-auto">
            <li className="nav-item">
              <Link className="nav-link active" to="/" onClick={() => closeMenu()}>Home</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/coffees" onClick={() => closeMenu()}>Coffees</Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/roasteries" onClick={() => closeMenu()}>Roasteries</Link>
            </li>
            {isAuthenticated && hasRole("Admin") && (
              <li className="nav-item">
                <Link className="nav-link" to="/admin" onClick={() => closeMenu()}>Admin</Link>
              </li>
            )}
          </ul>
          <ul className="navbar-nav">
            {isAuthenticated ? (
              <>
                <li className="nav-item">
                  <span className="nav-link">Hello, {user?.username}</span>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/profile" onClick={() => closeMenu()}>Profile</Link>
                </li>
                <li className="nav-item">
                  <button className="btn btn-outline-light" onClick={logout}>Logout</button>
                </li>
              </>
            ) : (
              <>
                <li className="nav-item">
                  <Link className="btn btn-outline-light me-2" to="/login" onClick={() => closeMenu()}>Login</Link>
                </li>
                <li className="nav-item">
                  <Link className="btn btn-outline-light" to="/register" onClick={() => closeMenu()}>Register</Link>
                </li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};


export default Navbar;