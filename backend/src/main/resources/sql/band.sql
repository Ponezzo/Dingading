SET SQL_SAFE_UPDATES = 0;

-- 트랜잭션 시작
BEGIN;

-- member 더미 데이터 생성 (UUID 생성)
INSERT INTO `member` (`created_at`, `updated_at`, `member_id`, `nickname`, `profile_img_url`, `username`)
VALUES ('2025-04-01 09:00:00', '2025-04-01 09:00:00', UUID_TO_BIN(UUID()), '기타킹',
        'https://storage.dinggading.com/profiles/avatar1.jpg', 'guitar_master@gmail.com'),
       ('2025-04-01 09:05:00', '2025-04-01 09:05:00', UUID_TO_BIN(UUID()), '드럼왕',
        'https://storage.dinggading.com/profiles/avatar2.jpg', 'drum_king@gmail.com'),
       ('2025-04-01 09:10:00', '2025-04-01 09:10:00', UUID_TO_BIN(UUID()), '베이싱',
        'https://storage.dinggading.com/profiles/avatar3.jpg', 'bass_pro@gmail.com'),
       ('2025-04-01 09:15:00', '2025-04-01 09:15:00', UUID_TO_BIN(UUID()), '보컬여신',
        'https://storage.dinggading.com/profiles/avatar4.jpg', 'vocal_queen@gmail.com'),
       ('2025-04-01 09:20:00', '2025-04-01 09:20:00', UUID_TO_BIN(UUID()), '멜로디',
        'https://storage.dinggading.com/profiles/avatar5.jpg', 'melody_singer@gmail.com'),
       ('2025-04-01 09:25:00', '2025-04-01 09:25:00', UUID_TO_BIN(UUID()), '록스타',
        'https://storage.dinggading.com/profiles/avatar6.jpg', 'rock_star@gmail.com'),
       ('2025-04-01 09:30:00', '2025-04-01 09:30:00', UUID_TO_BIN(UUID()), '음악대장',
        'https://storage.dinggading.com/profiles/avatar7.jpg', 'music_captain@gmail.com'),
       ('2025-04-01 09:35:00', '2025-04-01 09:35:00', UUID_TO_BIN(UUID()), '재즈맨',
        'https://storage.dinggading.com/profiles/avatar8.jpg', 'jazz_man@gmail.com'),
       ('2025-04-01 09:40:00', '2025-04-01 09:40:00', UUID_TO_BIN(UUID()), '팝스타',
        'https://storage.dinggading.com/profiles/avatar9.jpg', 'pop_star@gmail.com'),
       ('2025-04-01 09:45:00', '2025-04-01 09:45:00', UUID_TO_BIN(UUID()), '음악천재',
        'https://storage.dinggading.com/profiles/avatar10.jpg', 'music_genius@gmail.com'),
       ('2025-04-01 09:50:00', '2025-04-01 09:50:00', UUID_TO_BIN(UUID()), '무대여왕',
        'https://storage.dinggading.com/profiles/avatar11.jpg', 'stage_queen@gmail.com'),
       ('2025-04-01 09:55:00', '2025-04-01 09:55:00', UUID_TO_BIN(UUID()), '리듬킹',
        'https://storage.dinggading.com/profiles/avatar12.jpg', 'rhythm_king@gmail.com');

-- member UUID 조회를 위한 변수 설정 (밴드 생성용)
SET @member1_id = (SELECT BIN_TO_UUID(member_id)
                   FROM member
                   WHERE username = 'guitar_master@gmail.com');
SET @member2_id = (SELECT BIN_TO_UUID(member_id)
                   FROM member
                   WHERE username = 'drum_king@gmail.com');
SET @member3_id = (SELECT BIN_TO_UUID(member_id)
                   FROM member
                   WHERE username = 'bass_pro@gmail.com');
SET @member4_id = (SELECT BIN_TO_UUID(member_id)
                   FROM member
                   WHERE username = 'vocal_queen@gmail.com');
SET @member5_id = (SELECT BIN_TO_UUID(member_id)
                   FROM member
                   WHERE username = 'melody_singer@gmail.com');
SET @member6_id = (SELECT BIN_TO_UUID(member_id)
                   FROM member
                   WHERE username = 'rock_star@gmail.com');

-- band 더미 데이터 생성
INSERT INTO `band` (`created_at`, `updated_at`, `job_opening`, `max_size`, `band_master_id`, `description`, `name`,
                    `profile_url`, `sigun`, `tags`)
VALUES ('2025-04-01 10:00:00', '2025-04-01 10:00:00', TRUE, 5, UUID_TO_BIN(@member1_id), '다양한 장르의 음악을 연주하는 밴드입니다',
        '소리꾼들',
        'https://plus.unsplash.com/premium_photo-1682855223699-edb85ffa57b3?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8JUVCJUIwJUI0JUVCJTkzJTlDfGVufDB8fDB8fHww',
        '서울', '락밴드,인디,커버'),
       ('2025-04-01 10:10:00', '2025-04-01 10:10:00', TRUE, 4, UUID_TO_BIN(@member2_id), '재즈와 팝 음악을 혼합한 창작곡을 연주합니다',
        '블루노트',
        'https://plus.unsplash.com/premium_photo-1682855221220-4dcc4ac702c7?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OXx8JUVCJUIwJUI0JUVCJTkzJTlDfGVufDB8fDB8fHww',
        '부산', '재즈,팝,창작곡'),
       ('2025-04-01 10:20:00', '2025-04-01 10:20:00', FALSE, 6, UUID_TO_BIN(@member3_id), '록과 메탈 음악을 주로 연주하는 열정적인 밴드',
        '메탈헤드',
        'https://images.unsplash.com/photo-1507808973436-a4ed7b5e87c9?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjN8fCVFQiVCMCVCNCVFQiU5MyU5Q3xlbnwwfHwwfHx8MA%3D%3D',
        '대구', '록,메탈,헤비'),
       ('2025-04-01 10:30:00', '2025-04-01 10:30:00', TRUE, 5, UUID_TO_BIN(@member4_id), '감성적인 어쿠스틱 사운드를 연주하는 밴드',
        '어쿠스틱 소울',
        'https://images.unsplash.com/photo-1617136785693-54b279bddfeb?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NDN8fCVFQiVCMCVCNCVFQiU5MyU5Q3xlbnwwfHwwfHx8MA%3D%3D',
        '인천', '어쿠스틱,발라드,감성'),
       ('2025-04-01 10:40:00', '2025-04-01 10:40:00', TRUE, 7, UUID_TO_BIN(@member5_id), '한국 전통 음악과 현대 음악을 결합한 퓨전 밴드',
        '국악 프로젝트',
        'https://images.unsplash.com/photo-1651694721718-7a72df522ae3?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTE4fHwlRUIlQjAlQjQlRUIlOTMlOUN8ZW58MHx8MHx8fDA%3D',
        '광주', '퓨전,국악,전통'),
       ('2025-04-01 10:50:00', '2025-04-01 10:50:00', FALSE, 5, UUID_TO_BIN(@member6_id), '클래식과 팝을 결합한 독특한 사운드의 밴드',
        '클래식 팝스',
        'https://images.unsplash.com/photo-1632054543980-3b8b44256f27?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTI3fHwlRUIlQjAlQjQlRUIlOTMlOUN8ZW58MHx8MHx8fDA%3D',
        '울산', '클래식,팝,오케스트라');

-- 밴드 ID 조회 (밴드 리크루트먼트 생성용)
SET @band1_id = (SELECT band_id
                 FROM band
                 WHERE name = '소리꾼들');
SET @band2_id = (SELECT band_id
                 FROM band
                 WHERE name = '블루노트');
SET @band3_id = (SELECT band_id
                 FROM band
                 WHERE name = '메탈헤드');
SET @band4_id = (SELECT band_id
                 FROM band
                 WHERE name = '어쿠스틱 소울');
SET @band5_id = (SELECT band_id
                 FROM band
                 WHERE name = '국악 프로젝트');

-- band_recruitment 더미 데이터 생성
INSERT INTO `band_recruitment` (`audition_date`, `audition_song_id`, `band_id`, `band_recruitment_id`, `created_at`,
                                `updated_at`, `description`, `title`, `status`)
VALUES ('2025-04-15 14:00:00', 1, @band1_id, NULL, '2025-04-02 11:00:00', '2025-04-02 11:00:00',
        '기타리스트를 모집합니다. 락 음악에 관심 있는 분이면 좋겠습니다.', '소리꾼들 기타리스트 모집', 'RECRUITING'),
       ('2025-04-16 15:00:00', 3, @band1_id, NULL, '2025-04-02 11:30:00', '2025-04-02 11:30:00',
        '베이시스트를 모집합니다. 경험자 우대합니다.', '소리꾼들 베이시스트 모집', 'RECRUITING'),
       ('2025-04-17 16:00:00', 5, @band2_id, NULL, '2025-04-02 12:00:00', '2025-04-02 12:00:00',
        '보컬을 모집합니다. 재즈와 팝 음악을 좋아하는 분이면 좋겠습니다.', '블루노트 보컬 모집', 'RECRUITING'),
       ('2025-04-18 17:00:00', 4, @band3_id, NULL, '2025-04-02 12:30:00', '2025-04-02 12:30:00',
        '드러머를 모집합니다. 헤비 메탈 경험이 있으면 좋습니다.', '메탈헤드 드러머 모집', 'READY'),
       ('2025-04-19 18:00:00', 2, @band4_id, NULL, '2025-04-02 13:00:00', '2025-04-02 13:00:00',
        '기타리스트와 보컬을 모집합니다. 어쿠스틱 음악에 관심 있는 분이면 좋겠습니다.', '어쿠스틱 소울 멤버 모집', 'RECRUITING'),
       ('2025-04-20 19:00:00', 6, @band5_id, NULL, '2025-04-02 13:30:00', '2025-04-02 13:30:00',
        '국악기 연주자를 모집합니다. 전통 악기 연주 가능자 우대합니다.', '국악 프로젝트 단원 모집', 'COMPLETED');

-- 멤버 ID 다시 조회 (memberRank 생성용)
SET @member1_id = (SELECT member_id
                   FROM member
                   WHERE username = 'guitar_master@gmail.com');
SET @member2_id = (SELECT member_id
                   FROM member
                   WHERE username = 'drum_king@gmail.com');
SET @member3_id = (SELECT member_id
                   FROM member
                   WHERE username = 'bass_pro@gmail.com');
SET @member4_id = (SELECT member_id
                   FROM member
                   WHERE username = 'vocal_queen@gmail.com');
SET @member5_id = (SELECT member_id
                   FROM member
                   WHERE username = 'melody_singer@gmail.com');
SET @member6_id = (SELECT member_id
                   FROM member
                   WHERE username = 'rock_star@gmail.com');
SET @member7_id = (SELECT member_id
                   FROM member
                   WHERE username = 'music_captain@gmail.com');
SET @member8_id = (SELECT member_id
                   FROM member
                   WHERE username = 'jazz_man@gmail.com');
SET @member9_id = (SELECT member_id
                   FROM member
                   WHERE username = 'pop_star@gmail.com');
SET @member10_id = (SELECT member_id
                    FROM member
                    WHERE username = 'music_genius@gmail.com');
SET @member11_id = (SELECT member_id
                    FROM member
                    WHERE username = 'stage_queen@gmail.com');
SET @member12_id = (SELECT member_id
                    FROM member
                    WHERE username = 'rhythm_king@gmail.com');

-- memberRank 더미 데이터 생성
INSERT INTO `member_rank` (`beat_score`, `rank_success_count`, `tone_score`, `tune_score`, `created_at`,
                           `defence_expire_date`, `last_attempt_date`, `member_rank_id`, `updated_at`, `member_id`,
                           `instrument`, `last_attempt_tier`, `tier`)
VALUES
    -- 기타킹 (기타 다이아몬드)
    (95, 10, 90, 92, '2025-04-03 09:00:00', '2025-06-03 09:00:00', '2025-04-03 09:00:00', NULL, '2025-04-03 09:00:00',
     @member1_id, 'GUITAR', 'DIAMOND', 'DIAMOND'),
    -- 기타킹 (베이스 실버)
    (75, 3, 70, 72, '2025-04-03 09:10:00', '2025-06-03 09:10:00', '2025-04-03 09:10:00', NULL, '2025-04-03 09:10:00',
     @member1_id, 'BASS', 'SILVER', 'SILVER'),
    -- 기타킹 (드럼 브론즈)
    (65, 2, 60, 62, '2025-04-03 09:20:00', '2025-06-03 09:20:00', '2025-04-03 09:20:00', NULL, '2025-04-03 09:20:00',
     @member1_id, 'DRUM', 'BRONZE', 'BRONZE'),
    -- 기타킹 (보컬 언랭크)
    (0, 0, 0, 0, '2025-04-03 09:30:00', NULL, NULL, NULL, '2025-04-03 09:30:00', @member1_id, 'VOCAL', 'UNRANKED',
     'UNRANKED'),

    -- 드럼왕 (드럼 플래티넘)
    (92, 8, 88, 90, '2025-04-03 10:00:00', '2025-06-03 10:00:00', '2025-04-03 10:00:00', NULL, '2025-04-03 10:00:00',
     @member2_id, 'DRUM', 'PLATINUM', 'PLATINUM'),
    -- 드럼왕 (기타 골드)
    (80, 5, 78, 82, '2025-04-03 10:10:00', '2025-06-03 10:10:00', '2025-04-03 10:10:00', NULL, '2025-04-03 10:10:00',
     @member2_id, 'GUITAR', 'GOLD', 'GOLD'),
    -- 드럼왕 (베이스 실버)
    (70, 3, 68, 72, '2025-04-03 10:20:00', '2025-06-03 10:20:00', '2025-04-03 10:20:00', NULL, '2025-04-03 10:20:00',
     @member2_id, 'BASS', 'SILVER', 'SILVER'),
    -- 드럼왕 (보컬 브론즈)
    (60, 2, 58, 62, '2025-04-03 10:30:00', '2025-06-03 10:30:00', '2025-04-03 10:30:00', NULL, '2025-04-03 10:30:00',
     @member2_id, 'VOCAL', 'BRONZE', 'BRONZE'),

    -- 베이싱 (베이스 다이아몬드)
    (96, 12, 94, 95, '2025-04-03 11:00:00', '2025-06-03 11:00:00', '2025-04-03 11:00:00', NULL, '2025-04-03 11:00:00',
     @member3_id, 'BASS', 'DIAMOND', 'DIAMOND'),
    -- 베이싱 (기타 플래티넘)
    (85, 7, 82, 84, '2025-04-03 11:10:00', '2025-06-03 11:10:00', '2025-04-03 11:10:00', NULL, '2025-04-03 11:10:00',
     @member3_id, 'GUITAR', 'PLATINUM', 'PLATINUM'),
    -- 베이싱 (드럼 골드)
    (75, 5, 73, 76, '2025-04-03 11:20:00', '2025-06-03 11:20:00', '2025-04-03 11:20:00', NULL, '2025-04-03 11:20:00',
     @member3_id, 'DRUM', 'GOLD', 'GOLD'),
    -- 베이싱 (보컬 실버)
    (65, 3, 63, 66, '2025-04-03 11:30:00', '2025-06-03 11:30:00', '2025-04-03 11:30:00', NULL, '2025-04-03 11:30:00',
     @member3_id, 'VOCAL', 'SILVER', 'SILVER'),

    -- 보컬여신 (보컬 다이아몬드)
    (94, 11, 98, 96, '2025-04-03 12:00:00', '2025-06-03 12:00:00', '2025-04-03 12:00:00', NULL, '2025-04-03 12:00:00',
     @member4_id, 'VOCAL', 'DIAMOND', 'DIAMOND'),
    -- 보컬여신 (기타 브론즈)
    (65, 2, 63, 64, '2025-04-03 12:10:00', '2025-06-03 12:10:00', '2025-04-03 12:10:00', NULL, '2025-04-03 12:10:00',
     @member4_id, 'GUITAR', 'BRONZE', 'BRONZE'),
    -- 보컬여신 (베이스 아이언)
    (55, 1, 53, 54, '2025-04-03 12:20:00', '2025-06-03 12:20:00', '2025-04-03 12:20:00', NULL, '2025-04-03 12:20:00',
     @member4_id, 'BASS', 'IRON', 'IRON'),
    -- 보컬여신 (드럼 언랭크)
    (0, 0, 0, 0, '2025-04-03 12:30:00', NULL, NULL, NULL, '2025-04-03 12:30:00', @member4_id, 'DRUM', 'UNRANKED',
     'UNRANKED'),

    -- 멜로디 (보컬 플래티넘)
    (88, 7, 92, 90, '2025-04-03 13:00:00', '2025-06-03 13:00:00', '2025-04-03 13:00:00', NULL, '2025-04-03 13:00:00',
     @member5_id, 'VOCAL', 'PLATINUM', 'PLATINUM'),
    -- 멜로디 (기타 골드)
    (82, 5, 80, 81, '2025-04-03 13:10:00', '2025-06-03 13:10:00', '2025-04-03 13:10:00', NULL, '2025-04-03 13:10:00',
     @member5_id, 'GUITAR', 'GOLD', 'GOLD'),
    -- 멜로디 (베이스 실버)
    (72, 3, 70, 71, '2025-04-03 13:20:00', '2025-06-03 13:20:00', '2025-04-03 13:20:00', NULL, '2025-04-03 13:20:00',
     @member5_id, 'BASS', 'SILVER', 'SILVER'),
    -- 멜로디 (드럼 브론즈)
    (62, 2, 60, 61, '2025-04-03 13:30:00', '2025-06-03 13:30:00', '2025-04-03 13:30:00', NULL, '2025-04-03 13:30:00',
     @member5_id, 'DRUM', 'BRONZE', 'BRONZE'),

    -- 록스타 (기타 다이아몬드)
    (96, 12, 94, 95, '2025-04-03 14:00:00', '2025-06-03 14:00:00', '2025-04-03 14:00:00', NULL, '2025-04-03 14:00:00',
     @member6_id, 'GUITAR', 'DIAMOND', 'DIAMOND'),
    -- 록스타 (드럼 다이아몬드)
    (94, 11, 92, 93, '2025-04-03 14:10:00', '2025-06-03 14:10:00', '2025-04-03 14:10:00', NULL, '2025-04-03 14:10:00',
     @member6_id, 'DRUM', 'DIAMOND', 'DIAMOND'),
    -- 록스타 (베이스 플래티넘)
    (86, 8, 84, 85, '2025-04-03 14:20:00', '2025-06-03 14:20:00', '2025-04-03 14:20:00', NULL, '2025-04-03 14:20:00',
     @member6_id, 'BASS', 'PLATINUM', 'PLATINUM'),
    -- 록스타 (보컬 골드)
    (78, 5, 80, 79, '2025-04-03 14:30:00', '2025-06-03 14:30:00', '2025-04-03 14:30:00', NULL, '2025-04-03 14:30:00',
     @member6_id, 'VOCAL', 'GOLD', 'GOLD'),

    -- 음악대장
    (82, 6, 80, 81, '2025-04-03 15:00:00', '2025-06-03 15:00:00', '2025-04-03 15:00:00', NULL, '2025-04-03 15:00:00',
     @member7_id, 'GUITAR', 'GOLD', 'GOLD'),
    (84, 6, 82, 83, '2025-04-03 15:10:00', '2025-06-03 15:10:00', '2025-04-03 15:10:00', NULL, '2025-04-03 15:10:00',
     @member7_id, 'DRUM', 'GOLD', 'GOLD'),
    (86, 7, 84, 85, '2025-04-03 15:20:00', '2025-06-03 15:20:00', '2025-04-03 15:20:00', NULL, '2025-04-03 15:20:00',
     @member7_id, 'BASS', 'PLATINUM', 'PLATINUM'),
    (75, 4, 78, 76, '2025-04-03 15:30:00', '2025-06-03 15:30:00', '2025-04-03 15:30:00', NULL, '2025-04-03 15:30:00',
     @member7_id, 'VOCAL', 'SILVER', 'SILVER'),

    -- 재즈맨
    (78, 5, 76, 77, '2025-04-03 16:00:00', '2025-06-03 16:00:00', '2025-04-03 16:00:00', NULL, '2025-04-03 16:00:00',
     @member8_id, 'GUITAR', 'SILVER', 'SILVER'),
    (68, 3, 66, 67, '2025-04-03 16:10:00', '2025-06-03 16:10:00', '2025-04-03 16:10:00', NULL, '2025-04-03 16:10:00',
     @member8_id, 'DRUM', 'BRONZE', 'BRONZE'),
    (75, 4, 73, 74, '2025-04-03 16:20:00', '2025-06-03 16:20:00', '2025-04-03 16:20:00', NULL, '2025-04-03 16:20:00',
     @member8_id, 'BASS', 'SILVER', 'SILVER'),
    (90, 9, 92, 91, '2025-04-03 16:30:00', '2025-06-03 16:30:00', '2025-04-03 16:30:00', NULL, '2025-04-03 16:30:00',
     @member8_id, 'VOCAL', 'PLATINUM', 'PLATINUM'),

    -- 팝스타
    (75, 4, 73, 74, '2025-04-03 17:00:00', '2025-06-03 17:00:00', '2025-04-03 17:00:00', NULL, '2025-04-03 17:00:00',
     @member9_id, 'GUITAR', 'SILVER', 'SILVER'),
    (65, 2, 63, 64, '2025-04-03 17:10:00', '2025-06-03 17:10:00', '2025-04-03 17:10:00', NULL, '2025-04-03 17:10:00',
     @member9_id, 'DRUM', 'BRONZE', 'BRONZE'),
    (70, 3, 68, 69, '2025-04-03 17:20:00', '2025-06-03 17:20:00', '2025-04-03 17:20:00', NULL, '2025-04-03 17:20:00',
     @member9_id, 'BASS', 'BRONZE', 'BRONZE'),
    (88, 8, 90, 89, '2025-04-03 17:30:00', '2025-06-03 17:30:00', '2025-04-03 17:30:00', NULL, '2025-04-03 17:30:00',
     @member9_id, 'VOCAL', 'PLATINUM', 'PLATINUM'),

    -- 음악천재
    (92, 10, 90, 91, '2025-04-03 18:00:00', '2025-06-03 18:00:00', '2025-04-03 18:00:00', NULL, '2025-04-03 18:00:00',
     @member10_id, 'GUITAR', 'PLATINUM', 'PLATINUM'),
    (88, 8, 86, 87, '2025-04-03 18:10:00', '2025-06-03 18:10:00', '2025-04-03 18:10:00', NULL, '2025-04-03 18:10:00',
     @member10_id, 'DRUM', 'PLATINUM', 'PLATINUM'),
    (90, 9, 88, 89, '2025-04-03 18:20:00', '2025-06-03 18:20:00', '2025-04-03 18:20:00', NULL, '2025-04-03 18:20:00',
     @member10_id, 'BASS', 'PLATINUM', 'PLATINUM'),
    (85, 7, 87, 86, '2025-04-03 18:30:00', '2025-06-03 18:30:00', '2025-04-03 18:30:00', NULL, '2025-04-03 18:30:00',
     @member10_id, 'VOCAL', 'GOLD', 'GOLD'),

    -- 무대여왕
    (70, 3, 68, 69, '2025-04-03 19:00:00', '2025-06-03 19:00:00', '2025-04-03 19:00:00', NULL, '2025-04-03 19:00:00',
     @member11_id, 'GUITAR', 'BRONZE', 'BRONZE'),
    (60, 2, 58, 59, '2025-04-03 19:10:00', '2025-06-03 19:10:00', '2025-04-03 19:10:00', NULL, '2025-04-03 19:10:00',
     @member11_id, 'DRUM', 'BRONZE', 'BRONZE'),
    (65, 2, 63, 64, '2025-04-03 19:20:00', '2025-06-03 19:20:00', '2025-04-03 19:20:00', NULL, '2025-04-03 19:20:00',
     @member11_id, 'BASS', 'BRONZE', 'BRONZE'),
    (92, 10, 94, 93, '2025-04-03 19:30:00', '2025-06-03 19:30:00', '2025-04-03 19:30:00', NULL, '2025-04-03 19:30:00',
     @member11_id, 'VOCAL', 'PLATINUM', 'PLATINUM'),

    -- 리듬킹
    (72, 4, 70, 71, '2025-04-03 20:00:00', '2025-06-03 20:00:00', '2025-04-03 20:00:00', NULL, '2025-04-03 20:00:00',
     @member12_id, 'GUITAR', 'SILVER', 'SILVER'),
    (94, 11, 92, 93, '2025-04-03 20:10:00', '2025-06-03 20:10:00', '2025-04-03 20:10:00', NULL, '2025-04-03 20:10:00',
     @member12_id, 'DRUM', 'DIAMOND', 'DIAMOND'),
    (80, 6, 78, 79, '2025-04-03 20:20:00', '2025-06-03 20:20:00', '2025-04-03 20:20:00', NULL, '2025-04-03 20:20:00',
     @member12_id, 'BASS', 'GOLD', 'GOLD'),
    (68, 3, 70, 69, '2025-04-03 20:30:00', '2025-06-03 20:30:00', '2025-04-03 20:30:00', NULL, '2025-04-03 20:30:00',
     @member12_id, 'VOCAL', 'BRONZE', 'BRONZE');

-- 트랜잭션 커밋
COMMIT;