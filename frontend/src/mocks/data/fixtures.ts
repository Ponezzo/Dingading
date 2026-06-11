import type { BandDTO } from '@generated/model/band-dto';
import type { MemberRankDTO } from '@generated/model/member-rank-dto';

const SAMPLE_AUDIO =
  'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3';

export const MOCK_MEMBER_ID = 'mock-member-001';

export const mockMemberRanks: MemberRankDTO[] = [
  {
    memberRankId: 1,
    memberId: MOCK_MEMBER_ID,
    instrument: 'DRUM',
    tier: 'SILVER',
    beatScore: 82,
    tuneScore: 78,
    toneScore: 85,
    rankSuccessCount: 12,
    lastAttemptTier: 'SILVER',
    defenceExpireDate: new Date(Date.now() + 7 * 86400000).toISOString(),
    lastAttemptDate: new Date().toISOString(),
  },
  {
    memberRankId: 2,
    memberId: MOCK_MEMBER_ID,
    instrument: 'GUITAR',
    tier: 'BRONZE',
    beatScore: 70,
    tuneScore: 72,
    toneScore: 68,
    rankSuccessCount: 5,
    lastAttemptTier: 'BRONZE',
    defenceExpireDate: new Date(Date.now() + 5 * 86400000).toISOString(),
    lastAttemptDate: new Date().toISOString(),
  },
  {
    memberRankId: 3,
    memberId: MOCK_MEMBER_ID,
    instrument: 'BASS',
    tier: 'GOLD',
    beatScore: 88,
    tuneScore: 86,
    toneScore: 90,
    rankSuccessCount: 20,
    lastAttemptTier: 'GOLD',
    defenceExpireDate: new Date(Date.now() + 3 * 86400000).toISOString(),
    lastAttemptDate: new Date().toISOString(),
  },
  {
    memberRankId: 4,
    memberId: MOCK_MEMBER_ID,
    instrument: 'VOCAL',
    tier: 'IRON',
    beatScore: 60,
    tuneScore: 65,
    toneScore: 62,
    rankSuccessCount: 2,
    lastAttemptTier: 'IRON',
    defenceExpireDate: new Date(Date.now() + 6 * 86400000).toISOString(),
    lastAttemptDate: new Date().toISOString(),
  },
];

export const mockBands: BandDTO[] = [
  {
    bandId: 1,
    name: '서울 인디 밴드 Aurora',
    bandMasterId: MOCK_MEMBER_ID,
    description: '인디 록과 재즈를 섞어 연주하는 4인조 밴드입니다.',
    genres: ['JAZZ', 'POP'],
    sigun: '서울특별시',
    tags: '인디,록,재즈',
    profileUrl: '/images/pic1.jpg',
    maxSize: 5,
    jobOpening: true,
    memberCount: 3,
    instrumentAvailability: { VOCAL: true, GUITAR: true, DRUM: false, BASS: true },
    contact: [{ contactId: 1, bandId: 1, sns: 'INSTAGRAM', url: 'https://instagram.com' }],
    bandMember: [],
  },
  {
    bandId: 2,
    name: '부산 재즈 트리오 Blue Note',
    bandMasterId: 'mock-member-002',
    description: '스탠다드 재즈 레퍼토리 위주의 트리오입니다.',
    genres: ['JAZZ'],
    sigun: '부산광역시',
    tags: '재즈,스탠다드',
    profileUrl: '/images/pic2.jpg',
    maxSize: 4,
    jobOpening: true,
    memberCount: 2,
    instrumentAvailability: { VOCAL: false, GUITAR: true, DRUM: true, BASS: false },
    contact: [],
    bandMember: [],
  },
  {
    bandId: 3,
    name: '대전 메탈 크루 Iron Pulse',
    bandMasterId: 'mock-member-003',
    description: '하드 메탈과 프로그레시브를 연주합니다.',
    genres: ['HARDROCK', 'HIPHOP'],
    sigun: '대전광역시',
    tags: '메탈,하드록',
    profileUrl: '/images/pic3.jpg',
    maxSize: 6,
    jobOpening: false,
    memberCount: 5,
    instrumentAvailability: { VOCAL: true, GUITAR: true, DRUM: true, BASS: true },
    contact: [],
    bandMember: [],
  },
  {
    bandId: 4,
    name: '경기 K-pop 커버팀 Starlight',
    bandMasterId: 'mock-member-004',
    description: 'K-pop 커버와 버스킹 활동을 합니다.',
    genres: ['POP', 'DANCE'],
    sigun: '경기도',
    tags: 'K-pop,커버',
    profileUrl: '/images/pic4.jpg',
    maxSize: 7,
    jobOpening: true,
    memberCount: 4,
    instrumentAvailability: { VOCAL: true, GUITAR: false, DRUM: true, BASS: true },
    contact: [],
    bandMember: [],
  },
];

export const mockSongs = [
  {
    songId: 1,
    title: 'Shape of You',
    artist: 'Ed Sheeran',
    youtubeUrl: 'https://www.youtube.com/watch?v=JGwWNGJdvx8',
    description: '2017년 Ed Sheeran 히트곡',
    songFilename: SAMPLE_AUDIO,
    instruments: [
      {
        songByInstrumentId: 101,
        songId: 1,
        songInstrumentPackId: 1,
        songByInstrumentExFilename: SAMPLE_AUDIO,
        songByInstrumentFilename: SAMPLE_AUDIO,
        songByInstrumentAnalysisJson: '{}',
        instrument: 'DRUM',
        tier: 'SILVER',
      },
      {
        songByInstrumentId: 102,
        songId: 1,
        songInstrumentPackId: 1,
        songByInstrumentExFilename: SAMPLE_AUDIO,
        songByInstrumentFilename: SAMPLE_AUDIO,
        songByInstrumentAnalysisJson: '{}',
        instrument: 'GUITAR',
        tier: 'SILVER',
      },
    ],
  },
  {
    songId: 2,
    title: 'Blinding Lights',
    artist: 'The Weeknd',
    youtubeUrl: 'https://www.youtube.com/watch?v=4NRXx6U8ABQ',
    description: '80s synthwave 스타일',
    songFilename: SAMPLE_AUDIO,
    instruments: [
      {
        songByInstrumentId: 201,
        songId: 2,
        songInstrumentPackId: 2,
        songByInstrumentExFilename: SAMPLE_AUDIO,
        songByInstrumentFilename: SAMPLE_AUDIO,
        songByInstrumentAnalysisJson: '{}',
        instrument: 'DRUM',
        tier: 'BRONZE',
      },
    ],
  },
  {
    songId: 3,
    title: 'Bohemian Rhapsody',
    artist: 'Queen',
    youtubeUrl: 'https://www.youtube.com/watch?v=fJ9rUzIMcZQ',
    description: 'Queen 대표곡',
    songFilename: SAMPLE_AUDIO,
    instruments: [
      {
        songByInstrumentId: 301,
        songId: 3,
        songInstrumentPackId: 3,
        songByInstrumentExFilename: SAMPLE_AUDIO,
        songByInstrumentFilename: SAMPLE_AUDIO,
        songByInstrumentAnalysisJson: '{}',
        instrument: 'VOCAL',
        tier: 'GOLD',
      },
    ],
  },
];

export interface MockLivehouse {
  livehouseId: string;
  title: string;
  description: string;
  hostId: string;
  hostNickname: string;
  participantCount: number;
  maxParticipants: number;
}

export const mockLivehouses: MockLivehouse[] = [
  {
    livehouseId: 'mock-lh-1',
    title: '금요일 재즈 잼 세션',
    description: '가볍게 즉흥 연주해요',
    hostId: 'host-1',
    hostNickname: '재즈맨',
    participantCount: 3,
    maxParticipants: 8,
  },
  {
    livehouseId: 'mock-lh-2',
    title: '기타 연습실',
    description: '기타 파트 위주 연습',
    hostId: 'host-2',
    hostNickname: '기타리스트',
    participantCount: 1,
    maxParticipants: 5,
  },
];

export { SAMPLE_AUDIO };
