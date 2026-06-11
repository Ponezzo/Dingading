"use client";

import { useState, useRef, useCallback } from "react";
import { useInfiniteQuery } from "@tanstack/react-query";
import BandCard from "../../components/band/bandcard";
import CitySelector from "../../components/band/cityselector";
import { useConfigStore } from "@/store/config";

import styles from "./styles.module.css";
import HorizontalScrollWrapper from "@/components/horizontalscrollwrapper";

import { BandApi, BandApiSearchBandsRequest } from "@generated/api";
import BandDetailModal from "@/components/band/bandmodal";
import { BandDTO } from "@generated/model";

const fetchBands =
  (bandApiInstance: BandApi) =>
  async ({ pageParam = 0, region = "", searchTerm = "" }) => {
    const pagingParams = {
      params: {
        page: pageParam,
        size: 10,
      }
    }
    const apiParams: BandApiSearchBandsRequest = {
      sigun: region || undefined,
      keyword: searchTerm || undefined,
    };
    
    try {
      let res;
      // 지역이나 검색어가 있으면 searchBands 사용, 아니면 getBands 사용
      if (region || searchTerm) {
        // searchBands API 호출
        console.log("Using searchBands with params:", apiParams, pagingParams);
        res = await bandApiInstance.searchBands(apiParams, pagingParams);
      } else {
        // 필터링 없이 전체 밴드 조회
        console.log("Using getBands with params:", pagingParams);
        res = await bandApiInstance.getBands(pagingParams);
      }
      
      if (!res || !res.data || !res.data.content || !res.data.pageable) {
        console.error("Invalid API response structure:", res);
        throw new Error("Invalid API response structure");
      }
      
      const content: BandDTO[] = res.data.content;
      const currentPage = res.data.pageable.page;
      const totalPages = res.data.pageable.totalPages;
      const nextPage = currentPage < totalPages - 1 ? currentPage + 1 : undefined;
      
      console.log(`Received ${content.length} bands for region: ${region}`);
      return { items: content || [], nextPage: nextPage };
    } catch (error) {
      console.error("Error fetching bands:", error);
      throw error;
    }
  };

interface scrollInfo {
  scrollLeft: number;
  scrollWidth: number;
  clientWidth: number;
}
  

export default function Band() {
  const [region, setRegion] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedBandId, setSelectedBandId] = useState<number | null>(null); // 모달 상태

  const scrollContainerRef = useRef<HTMLDivElement>(null);
  const scrollWrapperRef = useRef<HTMLDivElement>(null);
  const { apiConfig } = useConfigStore((state) => state);

  const currentApiConfig = apiConfig;

  const bandApi = useRef(new BandApi(currentApiConfig)).current;


  const {
      data,
      fetchNextPage,
      hasNextPage,
      isFetchingNextPage,
      isLoading,
      isError,
      error,
  } = useInfiniteQuery< { items: BandDTO[]; nextPage: number | undefined }, Error >({
    queryKey: ["bands", region, searchTerm],
    queryFn: ({ pageParam = 0 }) => fetchBands(bandApi)({ pageParam: pageParam as number, region, searchTerm }),
    getNextPageParam: (lastPage) => lastPage?.nextPage,
    initialPageParam: 0,
    staleTime: 60000, // 1분 동안 결과를 캐시
  });

  const handleScroll = useCallback(({ scrollLeft, scrollWidth, clientWidth }: scrollInfo) => {
    if (scrollWidth > clientWidth && scrollWidth - scrollLeft - clientWidth < 200 && hasNextPage && !isFetchingNextPage) {
        fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);


  // 전체 밴드 데이터 펼치기
  const filteredBands: BandDTO[] = data?.pages.flatMap((page) => page.items) || [];

  // 카드 클릭 핸들러
  const handleCardClick = (bandId: number) => {
    setSelectedBandId(bandId);
  };

  // 모달 닫기 핸들러
  const handleCloseModal = () => {
    setSelectedBandId(null);
  };


  return (
    <div className={styles["band-container"]} ref={scrollWrapperRef}>
      <div className={styles["top-bar"]}>
        <CitySelector selectedCity={region} onSelect={setRegion} />
        <input
          type="text"
          placeholder="밴드 이름 검색"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className={styles["search-input"]}
        />
      </div>

      {isLoading && <div className={styles["loading-spinner"]}>Loading...</div>}
      {isError && <div className={styles["error-message"]}>Error fetching data: {error?.message}</div>}

      <HorizontalScrollWrapper onScroll={handleScroll}>
        <div ref={scrollContainerRef} className={styles["band-wrapper"]}>
          {filteredBands.map((band) => (
            // 카드 클릭 이벤트 추가
            <div key={band.bandId}
                 className={styles["band-card-wrapper"]}
                 onClick={() => handleCardClick(band.bandId)} 
                 role="button" // 시맨틱 개선
                 tabIndex={0} // 키보드 접근성
                 onKeyDown={(e) => e.key === 'Enter' && handleCardClick(band.bandId)} // Enter 키로도 선택 가능
            >
              <BandCard
                name={band.name}
                sigun={band.sigun}
                profileUrl={band.profileUrl}
                tags={band.tags}
                maxSize={band.maxSize}
                jobOpening={band.jobOpening}
                memberCount={band.memberCount}
                instrumentAvailability={band.instrumentAvailability || {}}
              />
            </div>
          ))}
          {isFetchingNextPage && <div className={styles["loading-more"]}>더 불러오는 중...</div>}
          {!isLoading && !isFetchingNextPage && !hasNextPage && filteredBands.length > 0 && 
            <div className={styles["no-more-bands"]}>더 이상 밴드가 없습니다</div>}
          {!isLoading && !isFetchingNextPage && filteredBands.length === 0 && !isError && 
            <div className={styles["no-bands-found"]}>
              {region ? `${region}에 등록된 밴드가 없습니다` : '등록된 밴드가 없습니다'}
            </div>}
        </div>
      </HorizontalScrollWrapper>

      {/* 모달 컴포넌트 렌더링 */}
      <BandDetailModal
        bandId={selectedBandId}
        isOpen={selectedBandId !== null}
        onClose={handleCloseModal}
        apiConfig={currentApiConfig} // API 설정 전달
      />
    </div>
  );
}