import React from 'react';
import { CoffeeCarousel } from './components/CoffeeCarousel';
import CoffeeHero from './components/CoffeeHero';

const HomePage: React.FC = () => {
  return (
    <>
      <CoffeeHero />
      <CoffeeCarousel />
    </>
  );
};

export default HomePage;