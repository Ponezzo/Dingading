import { useAuthStore } from '@/store/auth';
import { useConfigStore } from '@/store/config';
import { MOCK_MEMBER_ID } from './data/fixtures';

/** Mock 모드용 JWT-like 토큰 (payload.id = memberId) */
export function createMockAccessToken(): string {
  const payload = btoa(JSON.stringify({ id: MOCK_MEMBER_ID }));
  return `mock.${payload}.dev`;
}

export function ensureMockAuth(): void {
  const { isLoggedIn, loginWithToken } = useAuthStore.getState();
  if (isLoggedIn) return;

  const token = createMockAccessToken();
  loginWithToken(token, 'mock.user', encodeURIComponent('MockUser'), '');
  useConfigStore.getState().setAccessToken(token);
  useConfigStore.getState().setMemberId(MOCK_MEMBER_ID);
}

export function loginWithMockGoogle(): void {
  ensureMockAuth();
}
