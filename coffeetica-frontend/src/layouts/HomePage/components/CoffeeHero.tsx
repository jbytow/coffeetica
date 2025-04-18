import React from 'react';
import { Link } from 'react-router-dom';

const CoffeeHero: React.FC = () => {
  return (
    <div className='p-5 mb-4 bg-dark hero-image'>
      <div className='container-fluid py-5 text-white 
        d-flex justify-content-center align-items-center'>
        <div>
          <h1 className="display-5 fw-bold">Welcome to Coffeetica</h1>
          <p className="col-md-8 fs-4 mx-auto">Discover the finest coffees and their origins.</p>
          <Link type='button'
            className="btn btn-primary btn-lg"
            to="/coffees"
          >
            Explore top coffees
          </Link>
        </div>
      </div>
    </div>
  );
};

export default CoffeeHero;