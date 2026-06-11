'use client';

import { useEffect } from 'react';
import { isMockEnabled } from './config';
import { setupMockApi } from './setup';
import { ensureMockAuth } from './mockAuth';

export default function MockProvider() {
  useEffect(() => {
    if (!isMockEnabled()) return;
    setupMockApi();
    ensureMockAuth();
  }, []);

  if (!isMockEnabled()) return null;

  return (
    <div
      style={{
        position: 'fixed',
        bottom: 8,
        right: 8,
        zIndex: 9999,
        background: 'rgba(255, 152, 0, 0.92)',
        color: '#111',
        padding: '6px 12px',
        borderRadius: 8,
        fontSize: 12,
        fontWeight: 600,
        pointerEvents: 'none',
      }}
    >
      MOCK MODE (백엔드 없음)
    </div>
  );
}
