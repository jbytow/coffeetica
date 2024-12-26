import React from 'react';
import HeroSection from './components/HeroSection';
import { CoffeeCarousel } from './components/CoffeeCarousel';

const HomePage: React.FC = () => {
  return (
    <>
      <HeroSection />
      <CoffeeCarousel />
    </>
  );
};

export default HomePage;