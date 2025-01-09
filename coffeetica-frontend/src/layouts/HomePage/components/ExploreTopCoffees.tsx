import React from 'react';
import { Link } from 'react-router-dom';

const ExploreTopCoffees: React.FC = () => {
  return (
    <div className='p-5 mb-4 bg-dark header-image'>
      <div className='container-fluid py-5 text-white 
        d-flex justify-content-center align-items-center'>
        <div>
          <h1 className="display-5 fw-bold">Welcome to Coffeetica</h1>
          <p className="col-md-8 fs-4 mx-auto">Discover the finest coffees and their origins.</p>
          <Link type='button'
            className="btn main-color btn-lg text-white"
            to="/coffees"
          >
            Explore top coffees
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ExploreTopCoffees;