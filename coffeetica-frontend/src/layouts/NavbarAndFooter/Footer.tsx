import React from 'react';

const Footer: React.FC = () => {
  return (
    <footer className="bg-dark text-white text-center py-3">
      <p className="mb-0">
        © {new Date().getFullYear()} Coffeetica. All rights reserved. Jakub.bytow@gmail.com
      </p>
    </footer>
  );
};

export default Footer;