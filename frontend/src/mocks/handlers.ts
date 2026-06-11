import type { AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import {
  mockBands,
  mockLivehouses,
  mockMemberRanks,
  mockSongs,
  MOCK_MEMBER_ID,
  SAMPLE_AUDIO,
  type MockLivehouse,
} from './data/fixtures';

function getPath(config: InternalAxiosRequestConfig): string {
  const raw = config.url ?? '';
  try {
    const base = config.baseURL || 'http://localhost';
    return new URL(raw, base).pathname;
  } catch {
    return raw.split('?')[0];
  }
}

function ok<T>(config: InternalAxiosRequestConfig, data: T, status = 200): AxiosResponse<T> {
  return { data, status, statusText: 'OK', headers: {}, config };
}

function pageBands(content: typeof mockBands, page = 0, size = 10) {
  const start = page * size;
  const slice = content.slice(start, start + size);
  const totalPages = Math.max(1, Math.ceil(content.length / size));
  return {
    content: slice,
    pageable: {
      page,
      size,
      totalElements: content.length,
      totalPages,
    },
  };
}

let livehouseCounter = mockLivehouses.length;

export function resolveMockResponse(
  config: InternalAxiosRequestConfig
): AxiosResponse | null {
  const path = getPath(config);
  const method = (config.method ?? 'get').toLowerCase();
  const params = config.params ?? {};

  // --- Bands ---
  if (method === 'get' && (path === '/api/bands' || path === '/api/bands/search')) {
    let filtered = [...mockBands];
    const keyword = params.keyword as string | undefined;
    const sigun = params.sigun as string | undefined;
    if (keyword) {
      filtered = filtered.filter(
        (b) => b.name.includes(keyword) || b.description?.includes(keyword)
      );
    }
    if (sigun) {
      filtered = filtered.filter((b) => b.sigun === sigun);
    }
    const page = Number(params.page ?? 0);
    const size = Number(params.size ?? 10);
    return ok(config, pageBands(filtered, page, size));
  }

  const bandMatch = path.match(/^\/api\/bands\/(\d+)$/);
  if (method === 'get' && bandMatch) {
    const band = mockBands.find((b) => b.bandId === Number(bandMatch[1]));
    if (band) return ok(config, band);
    return ok(config, mockBands[0]);
  }

  // --- Member ranks ---
  if (method === 'get' && path === '/api/members/me/ranks') {
    return ok(config, mockMemberRanks);
  }

  // --- Songs ---
  const songsByTierMatch = path.match(/^\/api\/songs\/by-instrument\/([^/]+)\/by-tier\/([^/]+)$/);
  if (method === 'get' && songsByTierMatch) {
    const instrument = songsByTierMatch[1].toUpperCase();
    const tier = songsByTierMatch[2].toUpperCase();
    const content = mockSongs.filter((song) =>
      song.instruments.some(
        (i) => i.instrument === instrument && i.tier === tier
      )
    );
    if (content.length === 0) {
      return ok(config, {
        content: mockSongs.map(({ instruments, ...song }) => ({
          ...song,
          instruments: instruments.filter((i) => i.instrument === instrument),
        })),
        last: true,
        first: true,
        totalElements: mockSongs.length,
        totalPages: 1,
        size: 10,
        number: 0,
        numberOfElements: mockSongs.length,
        empty: false,
      });
    }
    return ok(config, {
      content,
      last: true,
      first: true,
      totalElements: content.length,
      totalPages: 1,
      size: 10,
      number: 0,
      numberOfElements: content.length,
      empty: false,
    });
  }

  const songUrlMatch = path.match(/^\/api\/songs\/(\d+)\/by-instrument\/([^/]+)$/);
  if (method === 'post' && songUrlMatch) {
    return ok(config, { songByInstrumentUrl: SAMPLE_AUDIO });
  }

  const songInstrumentMatch = path.match(/^\/api\/songs\/instruments\/(\d+)$/);
  if (method === 'get' && songInstrumentMatch) {
    return ok(config, {
      songByInstrumentId: Number(songInstrumentMatch[1]),
      songByInstrumentUrl: SAMPLE_AUDIO,
    });
  }

  // --- Records ---
  if (method === 'post' && path === '/api/records/me') {
    return ok(config, {
      recordId: Date.now(),
      memberId: MOCK_MEMBER_ID,
      dtype: 'CHALLENGE',
      title: 'Mock Recording',
      recordUrl: SAMPLE_AUDIO,
      createdAt: new Date().toISOString(),
    });
  }

  if (method === 'get' && path.startsWith('/api/records')) {
    return ok(config, { content: [], pageable: { page: 0, size: 10, totalElements: 0, totalPages: 0 } });
  }

  // --- Attempts ---
  const attemptMatch = path.match(/^\/api\/attempts\/(\d+)$/);
  if (method === 'get' && attemptMatch) {
    return ok(config, {
      attemptId: Number(attemptMatch[1]),
      songByInstrumentId: 101,
      status: 'SUCCESS',
      gameType: 'RANK',
      rankType: 'CHALLENGE',
      tuneScore: 85,
      toneScore: 90,
      beatScore: 88,
      totalScore: 92,
      createdAt: new Date().toISOString(),
    });
  }

  // --- Livehouses ---
  if (method === 'get' && path === '/api/livehouses') {
    return ok(config, { content: mockLivehouses });
  }

  if (method === 'get' && path.startsWith('/api/livehouses/search')) {
    const keyword = new URL(config.url ?? '', config.baseURL || 'http://localhost').searchParams.get('keyword') ?? '';
    const filtered = keyword
      ? mockLivehouses.filter((lh) => lh.title.includes(keyword))
      : mockLivehouses;
    return ok(config, { content: filtered });
  }

  const lhMatch = path.match(/^\/api\/livehouses\/([^/]+)$/);
  if (method === 'get' && lhMatch && lhMatch[1] !== 'search') {
    const lh = mockLivehouses.find((l) => l.livehouseId === lhMatch[1]);
    const data: MockLivehouse & { participantId?: string } = lh ?? mockLivehouses[0];
    return ok(config, {
      ...data,
      hostNickname: data.hostNickname,
      participantId: 'mock-participant-1',
    });
  }

  const joinMatch = path.match(/^\/api\/livehouses\/([^/]+)\/join$/);
  if (method === 'post' && joinMatch) {
    return ok(config, {
      livehouseId: joinMatch[1],
      sessionId: 'mock-session',
      token: 'mock-openvidu-token',
      participantId: 1,
      nickname: typeof window !== 'undefined' ? localStorage.getItem('nickname') || 'MockUser' : 'MockUser',
    });
  }

  if (method === 'post' && path === '/api/livehouses') {
    livehouseCounter += 1;
    const newId = `mock-lh-${livehouseCounter}`;
    const body = typeof config.data === 'string' ? JSON.parse(config.data) : config.data ?? {};
    const created: MockLivehouse = {
      livehouseId: newId,
      title: body.title ?? '새 라이브하우스',
      description: body.description ?? '',
      hostId: MOCK_MEMBER_ID,
      hostNickname: typeof window !== 'undefined' ? localStorage.getItem('nickname') || 'MockUser' : 'MockUser',
      participantCount: 1,
      maxParticipants: body.maxParticipants ?? 4,
    };
    mockLivehouses.unshift(created);
    return ok(config, {
      livehouseId: newId,
      sessionId: 'mock-session',
      token: 'mock-openvidu-token',
    });
  }

  const leaveMatch = path.match(/^\/api\/livehouses\/([^/]+)\/leave\//);
  if (method === 'post' && leaveMatch) {
    return ok(config, {});
  }

  const closeMatch = path.match(/^\/api\/livehouses\/([^/]+)\/close$/);
  if (method === 'post' && closeMatch) {
    return ok(config, {});
  }

  console.warn('[Mock API] Unhandled request:', method.toUpperCase(), path);
  return ok(config, {});
}
