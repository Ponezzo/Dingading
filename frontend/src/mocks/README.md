# Mock API (백엔드 없이 FE 개발용)

## 사용법

`frontend/.env.development`:

```
NEXT_PUBLIC_USE_MOCK=true
```

## 제거 방법

1. `.env.development`에서 `NEXT_PUBLIC_USE_MOCK=true` 삭제 또는 `false`로 변경
2. `src/mocks/` 폴더 전체 삭제
3. `src/app/layout.tsx`에서 `MockProvider` import 및 JSX 제거
4. `src/app/login/page.tsx`의 Mock 로그인 분기 제거
5. `src/app/livehouses/[livehouseId]/page.tsx`의 Mock 분기 제거

## Mock으로 동작하는 기능

- 로그인 (Mock Google)
- 밴드 목록/상세
- 티어/곡 목록/녹음 업로드
- 라이브하우스 목록/생성/상세 UI (OpenVidu 영상은 Mock UI만)

## Mock으로 동작하지 않는 기능

- 실제 OpenVidu 영상 통화
- Google OAuth 실로그인
- AI 채점 결과
