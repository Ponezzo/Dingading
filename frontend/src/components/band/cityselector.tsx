import React, { useState } from "react";
import styles from "./cityselector.module.css";

const cities = [
  "서울",
  "부산",
  "대구",
  "인천",
  "광주",
  "대전",
  "울산",
  "세종",
  "제주",
];

interface Props {
  onSelect: (city: string) => void;
  selectedCity: string;
}

const CitySelector: React.FC<Props> = ({ onSelect, selectedCity }) => {
  const [open, setOpen] = useState(false);

  const handleSelect = (city: string) => {
    // 이미 선택된 도시를 다시 클릭하면 필터 제거
    if (city === selectedCity) {
      onSelect("");
    } else {
      onSelect(city);
    }
    setOpen(false);
  };

  const handleClear = (e: React.MouseEvent) => {
    e.stopPropagation();
    onSelect("");
    setOpen(false);
  };

  return (
    <div className={styles.selectorWrapper}>
      <button className={styles.selectorButton} onClick={() => setOpen(!open)}>
        {selectedCity || "지역 선택"}
        {selectedCity && (
          <span className={styles.clearButton} onClick={handleClear}>
            ✕
          </span>
        )}
      </button>

      {open && (
        <div className={styles.dropdown}>
          {cities.map((city) => (
            <button
              key={city}
              onClick={() => handleSelect(city)}
              className={`${styles.cityButton} ${selectedCity === city ? styles.citySelected : ""}`}
            >
              {city}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

export default CitySelector;
