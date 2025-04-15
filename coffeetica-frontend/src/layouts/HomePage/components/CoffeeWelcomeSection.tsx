import React from "react";
import { Link } from "react-router-dom";

const CoffeeWelcomeSection: React.FC = () => {
    const token = localStorage.getItem("token");
  
    return (
        <div>
          <div className="d-none d-lg-block">
            <div className="row g-0 mt-5">
              {/* Left Image Section */}
              <div className="col-sm-6 col-md-6">
                <div className="col-image col-image-left"></div>
              </div>
    
              {/* Right Text Section */}
              <div className="col-4 col-md-4 container d-flex justify-content-center align-items-center">
                <div className="ml-2">
                  <h1>Your Coffee Journey Starts Here</h1>
                  <p className="lead">
                    Discover a world of coffee. From unique origins to exciting flavor profiles, we provide the perfect beans for every coffee lover.
                  </p>
                  {token ? (
                    <Link
                      type="button"
                      className="btn btn-primary btn-lg text-white"
                      to="/coffees"
                    >
                      Browse Coffees
                    </Link>
                  ) : (
                    <Link
                      className="btn btn-primary btn-lg text-white"
                      to="/login"
                    >
                      Join Us
                    </Link>
                  )}
                </div>
              </div>
            </div>
    
            <div className="row g-0">
              {/* Left Text Section */}
              <div className="col-4 col-md-4 container d-flex justify-content-center align-items-center">
                <div className="ml-2">
                  <h1>Fresh Beans, Every Day</h1>
                  <p className="lead">
                    Our collection is constantly growing. Check back often to find the freshest and most exciting coffee offerings.
                  </p>
                </div>
              </div>
    
              {/* Right Image Section */}
              <div className="col-sm-6 col-md-6">
                <div className="col-image col-image-right"></div>
              </div>
            </div>
          </div>
    
          {/* Mobile Version */}
          <div className="d-lg-none">
            <div className="container">
              <div className="m-2">
                <div className="col-image col-image-left"></div>
                <div className="mt-2">
                  <h1>Your Coffee Journey Starts Here</h1>
                  <p className="lead">
                    Discover a world of coffee. From unique origins to exciting flavor profiles, we provide the perfect beans for every coffee lover.
                  </p>
                  {token ? (
                    <Link
                      type="button"
                      className="btn btn-primary btn-lg text-white"
                      to="/coffees"
                    >
                      Browse Coffees
                    </Link>
                  ) : (
                    <Link
                      className="btn btn-primary btn-lg text-white"
                      to="/login"
                    >
                      Join Us
                    </Link>
                  )}
                </div>
              </div>
    
              <div className="m-2">
                <div className="col-image col-image-right"></div>
                <div className="mt-2">
                  <h1>Fresh Beans, Every Day</h1>
                  <p className="lead">
                    Our collection is constantly growing. Check back often to find the freshest and most exciting coffee offerings.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      );
    };
    
  
  export default CoffeeWelcomeSection;