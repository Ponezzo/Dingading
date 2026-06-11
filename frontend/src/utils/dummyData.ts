export interface Band {
  id: number;
  name: string;
  region: string;
  description: string;
  memberCount: number;
  genre: string;
  createdAt: string;
}

export interface BandsResponse {
  items: Band[];
  nextPage: number | null;
}

const regions = [
  '서울특별시',
  '부산광역시',
  '대구광역시',
  '인천광역시',
  '광주광역시',
  '대전광역시',
  '울산광역시',
  '제주특별자치도',
  '경기도',
  '강원도',
  '충청북도',
  '충청남도',
  '전라북도',
  '전라남도',
  '경상북도',
  '경상남도',
];

const genres = [
  '록',
  '재즈',
  '클래식',
  '힙합',
  'R&B',
  '포크',
  '블루스',
  '메탈',
  '인디',
  'K-pop',
];

export const generateDummyBands = (page: number, pageSize: number = 8, region?: string, searchTerm?: string): BandsResponse => {
  const startId = (page - 1) * pageSize;
  const filteredRegions = region ? [region] : regions;
  
  const items = Array.from({ length: pageSize }, (_, i) => {
    const randomRegion = filteredRegions[Math.floor(Math.random() * filteredRegions.length)];
    const randomGenre = genres[Math.floor(Math.random() * genres.length)];
    const memberCount = Math.floor(Math.random() * 5) + 2; // 2-6명
    
    return {
      id: startId + i + 1,
      name: `${randomRegion} ${randomGenre} 밴드 ${startId + i + 1}`,
      region: randomRegion,
      description: `${randomGenre} 장르의 ${memberCount}인조 밴드입니다.`,
      memberCount,
      genre: randomGenre,
      createdAt: new Date(Date.now() - Math.random() * 90 * 24 * 60 * 60 * 1000).toISOString(), // 최근 90일 내
    };
  });

  // 검색어로 필터링
  const filteredItems = searchTerm
    ? items.filter(band => 
        band.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        band.description.toLowerCase().includes(searchTerm.toLowerCase())
      )
    : items;

  // 더미 데이터가 100개를 넘어가면 더 이상 데이터가 없다고 가정
  const hasMore = startId + pageSize < 100;
  
  return {
    items: filteredItems,
    nextPage: hasMore ? page + 1 : null,
  };
}; 