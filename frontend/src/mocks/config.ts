/** Mock API 사용 여부. .env.development 의 NEXT_PUBLIC_USE_MOCK 로 제어 */
export function isMockEnabled(): boolean {
  return process.env.NEXT_PUBLIC_USE_MOCK === 'true';
}
