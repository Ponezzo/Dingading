// C:\Users\SSAFY\Desktop\project\S12P21E107\frontend\src\app\tier\attempt\[attemptId]\success\page.tsx
import SuccessContent from './SuccessContent'

// Next.js 15.2.3 요구사항에 맞게 수정
export default async function SuccessPage({ 
  params 
}: { 
  params: Promise<{ attemptId: string }> 
}) {
  // Promise를 항상 await으로 해결
  const resolvedParams = await params;
  
  return (
    <SuccessContent attemptId={resolvedParams.attemptId} />
  );
}