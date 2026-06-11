'use client';
import { NextPage } from 'next';
import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/auth';
import { isMockEnabled } from '@/mocks/config';
import { loginWithMockGoogle } from '@/mocks/mockAuth';

const Login: NextPage = () => {
  const router = useRouter();
  const { isLoggedIn } = useAuthStore();

  const handleGoogleLogin = () => {
    if (isMockEnabled()) {
      loginWithMockGoogle();
      router.push('/main');
      return;
    }

    const baseUrl = process.env.NEXT_PUBLIC_API_OAUTH_URL;
    const authUrl = `${baseUrl}/oauth2/authorization/google`;
    window.location.href = authUrl;
  };

  // 이미 로그인된 경우 홈페이지로 리다이렉트
  useEffect(() => {
    if (isLoggedIn) {
      router.push('/');
    }
  }, [isLoggedIn, router]);

  return (
    <div className="flex min-h-screen flex-col items-center justify-center ">
      <div className="w-full max-w-md rounded-lg bg-gray-800 p-8 shadow-md">
        <h1 className="mb-6 text-center text-2xl font-bold text-white">로그인</h1>
        
        <button
          onClick={handleGoogleLogin}
          className="flex w-full items-center justify-center rounded-md bg-white px-4 py-2 text-gray-700 shadow-md hover:bg-gray-50"
        >
          <svg className="mr-2 h-6 w-6" viewBox="0 0 24 24">
            <path
              fill="#4285F4"
              d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.03 5.03 0 0 1-2.2 3.32v2.76h3.56c2.08-1.92 3.28-4.74 3.28-8.09z"
            />
            <path
              fill="#34A853"
              d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.56-2.76c-.98.66-2.24 1.06-3.72 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
            />
            <path
              fill="#FBBC05"
              d="M5.84 14.11c-.22-.66-.35-1.36-.35-2.11s.13-1.45.35-2.11V7.05H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.95l3.66-2.84z"
            />
            <path
              fill="#EA4335"
              d="M12 5.36c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.05l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
            />
          </svg>
          Google 계정으로 로그인
        </button>
      </div>
    </div>
  );
};

export default Login;