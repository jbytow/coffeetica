import React from 'react';
import { CoffeeCarousel } from './components/CoffeeCarousel';
import ExploreTopCoffees from './components/ExploreTopCoffees';

const HomePage: React.FC = () => {
  return (
    <>
      <ExploreTopCoffees />
      <CoffeeCarousel />
    </>
  );
};

export default HomePage;