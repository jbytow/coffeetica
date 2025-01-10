import React from 'react';
import { CoffeeCarousel } from './components/CoffeeCarousel';
import CoffeeHero from './components/CoffeeHero';
import CoffeeWelcomeSection from './components/CoffeeWelcomeSection';

const HomePage: React.FC = () => {
  return (
    <>
      <CoffeeHero />
      <CoffeeCarousel />
      <CoffeeWelcomeSection />
    </>
  );
};

export default HomePage;