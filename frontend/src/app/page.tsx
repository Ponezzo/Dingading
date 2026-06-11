"use client";
import { useEffect } from 'react';
// import { useEffect, useState } from 'react';
// import type { NextPage } from 'next';
// import HorizontalScrollWrapper from '@/components/horizontalscrollwrapper';
// import { RankMatchingApi, RankMatchingApiCreateRankMatchingRequest } from '@generated/api';
// import { CreateRankMatchingRequestDTO } from '@generated/model';
// import { useConfigStore } from '@/store/config';
import { useRouter } from 'next/navigation';
// import { RankMatchingApi, RankMatchingApiCreateRankMatchingRequest } from '@generated/api';
// import { CreateRankMatchingRequestDTO } from '@generated/model';
// import { Configuration } from '@generated/configuration';

export default function Home () {
  // const [response, setResponse] = useState<string>('');
  // const [loading, setLoading] = useState<boolean>(false);
  // const mode = process.env.NEXT_PUBLIC_ENV_MODE || 'default-mode';
  // const url = process.env.NEXT_PUBLIC_API_BASE_URL || 'https://default-api.com';
  // console.log("page.tsx", mode);
  // console.log("page.tsx", url);
  
  const router = useRouter() 
  useEffect(() => {
    router.push("main")
  }, [])

  // // config 경로 결정 
  // const { apiConfig } = useConfigStore()

  // const firstRankRequest: CreateRankMatchingRequestDTO = {
  //   songInstrumentPackId: 123,
  //   instrument: 'BASS',
  //   rankType: 'FIRST'
  // };
  // const request :RankMatchingApiCreateRankMatchingRequest = {
  //   createRankMatchingRequestDTO: firstRankRequest
  // }
  // const api = new RankMatchingApi(apiConfig);
  // api.createRankMatching(request).then((res) => {
  //   console.log(res.data);
  // }).then((err) => {
  //   console.log(err);
  // });

  // const testApi = async () => {
  //   setLoading(true);
  //   try {
  //     // 프록시 API 라우트 호출
  //     const res = await fetch('/api/hello');

  //     // 나중에 json으로 변경 res.json()
  //     const data = await res.text();
  //     setResponse(data);
  //   } catch (error) {
  //     console.error('API 호출 오류:', error);
  //     setResponse('API 호출 중 오류가 발생했습니다.');
  //   } finally {
  //     setLoading(false);
  //   }
  // };
  
  return (
    null
    // <HorizontalScrollWrapper>
    // <div style={{ padding: '20px' }}>
    //   <h1>API </h1>

    //   <h1>현재 환경 {mode}</h1>
    //   <button 
    //     onClick={testApi}
    //     disabled={loading}
    //   >
    //     {loading ? '로딩 중...' : 'API 호출하기 - 눌러주세요'}
    //   </button>
    //   {response && (
    //     <div style={{ marginTop: '20px' }}>
    //       <h2>응답:</h2>
    //       <pre>{response}</pre>
    //     </div>
    //   )}
    // </div>
    // </HorizontalScrollWrapper>
  );
};
