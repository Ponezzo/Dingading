// youtube 썸네일 활용해 이미지 구하기 
export const getYouTubeThumbnail = (url: string): string | null => {
  const match = url.match(/(?:https?:\/\/)?(?:www\.)?youtube\.com\/watch\?v=([^&]+)/);
  const thumbnailUrl = match ? `https://img.youtube.com/vi/${match[1]}/hqdefault.jpg` : null;
  return thumbnailUrl
}