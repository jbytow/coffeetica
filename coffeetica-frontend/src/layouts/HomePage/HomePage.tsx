import React from 'react';
import HeroSection from './components/HeroSection';
import { CoffeeCarousel } from './components/CoffeeCarousel';

const HomePage: React.FC = () => {
  return (
    <div>
      <HeroSection />
      <CoffeeCarousel />
    </div>
  );
};

export default HomePage;