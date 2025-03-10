import React, { useState, useEffect } from "react";


interface SearchableDropdownProps {
  options: string[]; // List of dropdown options
  label: string; // Label for the dropdown
  onChange: (selected: string) => void; // Callback for the selected value
  value?: string; // Selected value (optional, for pre-selection)
}

const SearchableDropdown: React.FC<SearchableDropdownProps> = ({
  options,
  label,
  onChange,
  value = "",
}) => {
  const [searchTerm, setSearchTerm] = useState<string>(""); // User input
  const [filteredOptions, setFilteredOptions] = useState<string[]>(options); // Filtered list
  const [isDropdownVisible, setIsDropdownVisible] = useState<boolean>(false); // Dropdown visibility
  const [highlightedIndex, setHighlightedIndex] = useState<number>(0); // Highlighted option index

  // Synchronize filtered options with the initial options prop
  useEffect(() => {
    setFilteredOptions(options);
    setSearchTerm(value)
    setHighlightedIndex(0); // Reset highlighted index
  }, [options, value]);

  // Update filtered options when the search term changes
  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearchTerm(value);

    // Filter options based on user input
    const filtered = options.filter((option) =>
      option.toLowerCase().includes(value.toLowerCase())
    );
    setFilteredOptions(filtered);
    setHighlightedIndex(0); // Reset highlighted index to the first option
    setIsDropdownVisible(true);
  };

  // Handle selecting an option
  const handleOptionSelect = (option: string) => {
    setSearchTerm(option); // Update input with selected value
    setIsDropdownVisible(false); // Hide the dropdown
    onChange(option); // Pass selected value to parent
  };

  // Handle key events for navigation and selection
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "ArrowDown") {
      // Move down the list
      setHighlightedIndex((prevIndex) =>
        Math.min(prevIndex + 1, filteredOptions.length - 1)
      );
    } else if (e.key === "ArrowUp") {
      // Move up the list
      setHighlightedIndex((prevIndex) => Math.max(prevIndex - 1, 0));
    } else if (e.key === "Enter" || e.key === "Tab") {
      // Select the highlighted option
      if (filteredOptions.length > 0) {
        handleOptionSelect(filteredOptions[highlightedIndex]);
      }
      e.preventDefault(); // Prevent default form submission behavior
    }
  };

  return (
    <div className="position-relative">
      <label className="form-label">{label}</label>
      <input
        type="text"
        className="form-control"
        placeholder={`Search ${label.toLowerCase()}...`}
        value={searchTerm}
        onChange={handleSearchChange}
        onFocus={() => setIsDropdownVisible(true)} // Show dropdown on focus
        onBlur={() => setTimeout(() => setIsDropdownVisible(false), 150)} // Delay to allow click
        onKeyDown={handleKeyDown} // Handle navigation and selection
      />
      {isDropdownVisible && filteredOptions.length > 0 && (
        <ul
          className="list-group position-absolute mt-1"
          style={{ zIndex: 1000, width: "100%" }}
        >
          {filteredOptions.map((option, index) => (
            <li
              key={option}
              className={`list-group-item list-group-item-action ${
                index === highlightedIndex ? "active" : ""
              }`}
              onMouseEnter={() => setHighlightedIndex(index)} // Highlight on hover
              onClick={() => handleOptionSelect(option)} // Select on click
              style={{ cursor: "pointer" }}
            >
              {option}
            </li>
          ))}
        </ul>
      )}
      {isDropdownVisible && filteredOptions.length === 0 && (
        <p className="mt-1 text-muted">No results found.</p>
      )}
    </div>
  );
};

export default SearchableDropdown;