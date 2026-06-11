import axios from 'axios';
import { isMockEnabled } from './config';
import { resolveMockResponse } from './handlers';

let initialized = false;

export function setupMockApi(): void {
  if (!isMockEnabled() || initialized || typeof window === 'undefined') {
    return;
  }

  initialized = true;

  axios.interceptors.request.use((config) => {
    const mockResponse = resolveMockResponse(config);
    if (mockResponse) {
      config.adapter = () => Promise.resolve(mockResponse);
    }
    return config;
  });

  console.info('[Mock API] axios interceptor enabled');
}
