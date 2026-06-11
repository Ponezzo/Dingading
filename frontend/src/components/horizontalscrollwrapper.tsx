'use client';

import { useEffect, useRef } from 'react';

type Props = {
  children: React.ReactNode;
  onScroll?: (e: {
    scrollLeft: number;
    scrollWidth: number;
    clientWidth: number;
  }) => void;
};

export default function HorizontalScrollWrapper({ children, onScroll }: Props) {
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;

    const handleWheel = (e: WheelEvent) => {
      // 항상 휠 이벤트를 가로 스크롤로 처리
      if (container.scrollHeight <= container.clientHeight) {
        container.scrollLeft += e.deltaY;

        onScroll?.({
          scrollLeft: container.scrollLeft,
          scrollWidth: container.scrollWidth,
          clientWidth: container.clientWidth,
        });
      }
    };

    const handleScroll = () => {
      onScroll?.({
        scrollLeft: container.scrollLeft,
        scrollWidth: container.scrollWidth,
        clientWidth: container.clientWidth,
      });
    };

    container.addEventListener('wheel', handleWheel, { passive: false });
    container.addEventListener('scroll', handleScroll);

    return () => {
      container.removeEventListener('wheel', handleWheel);
      container.removeEventListener('scroll', handleScroll);
    };
  }, [onScroll]);

  return (
    <div
      ref={containerRef}
      style={{
        width: '100%',
        overflowX: 'auto',
        overflowY: 'hidden',
        whiteSpace: 'nowrap'
      }}
    >
      {children}
    </div>
  );
}
