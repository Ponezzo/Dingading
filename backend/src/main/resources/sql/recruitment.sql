SET SQL_SAFE_UPDATES = 0;

-- 트랜잭션 시작
BEGIN;

-- 기존 멤버 ID 조회
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

-- 밴드 ID 조회
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
SET @band6_id = (SELECT band_id
                 FROM band
                 WHERE name = '클래식 팝스');

-- band_member 더미 데이터 생성
INSERT INTO `band_member` (`band_id`, `created_at`, `updated_at`, `member_id`, `instrument`)
VALUES
    -- 소리꾼들 멤버
    (@band1_id, '2025-04-01 10:05:00', '2025-04-01 10:05:00', @member1_id, 'GUITAR'), -- 기타킹은 소리꾼들의 밴드마스터이자 기타리스트
    (@band1_id, '2025-04-01 10:08:00', '2025-04-01 10:08:00', @member2_id, 'DRUM'),   -- 드럼왕은 소리꾼들의 드러머

    -- 블루노트 멤버
    (@band2_id, '2025-04-01 10:15:00', '2025-04-01 10:15:00', @member2_id, 'DRUM'),   -- 드럼왕은 블루노트의 밴드마스터이자 드러머
    (@band2_id, '2025-04-01 10:18:00', '2025-04-01 10:18:00', @member8_id, 'VOCAL'),  -- 재즈맨은 블루노트의 보컬

    -- 메탈헤드 멤버
    (@band3_id, '2025-04-01 10:25:00', '2025-04-01 10:25:00', @member3_id, 'BASS'),   -- 베이싱은 메탈헤드의 밴드마스터이자 베이시스트
    (@band3_id, '2025-04-01 10:28:00', '2025-04-01 10:28:00', @member6_id, 'GUITAR'), -- 록스타는 메탈헤드의 기타리스트
    (@band3_id, '2025-04-01 10:31:00', '2025-04-01 10:31:00', @member9_id, 'VOCAL'),  -- 팝스타는 메탈헤드의 보컬

    -- 어쿠스틱 소울 멤버
    (@band4_id, '2025-04-01 10:35:00', '2025-04-01 10:35:00', @member4_id, 'VOCAL'),  -- 보컬여신은 어쿠스틱 소울의 밴드마스터이자 보컬
    (@band4_id, '2025-04-01 10:38:00', '2025-04-01 10:38:00', @member7_id, 'BASS'),   -- 음악대장은 어쿠스틱 소울의 베이시스트

    -- 국악 프로젝트 멤버
    (@band5_id, '2025-04-01 10:45:00', '2025-04-01 10:45:00', @member5_id, 'VOCAL');
-- 멜로디는 국악 프로젝트의 밴드마스터이자 보컬

-- band_recruitment_instrument 더미 데이터 생성 (기존 band_recruitment에 필요한 악기 추가)
-- 먼저 이전 데이터가 있을 경우 삭제 (옵션)
DELETE
FROM band_recruitment_instrument
WHERE band_recruitment_id IN (1, 2, 3, 4, 5, 6);

INSERT INTO `band_recruitment_instrument` (`max_size`, `band_recruitment_id`, `created_at`, `updated_at`, `instrument`,
                                           `required_tier`)
VALUES
    -- 소리꾼들 기타리스트 모집 (recruitment_id = 1)
    (1, 1, '2025-04-02 11:05:00', '2025-04-02 11:05:00', 'GUITAR', 'GOLD'),

    -- 소리꾼들 베이시스트 모집 (recruitment_id = 2)
    (1, 2, '2025-04-02 11:35:00', '2025-04-02 11:35:00', 'BASS', 'SILVER'),

    -- 블루노트 보컬 모집 (recruitment_id = 3)
    (1, 3, '2025-04-02 12:05:00', '2025-04-02 12:05:00', 'VOCAL', 'PLATINUM'),

    -- 메탈헤드 드러머 모집 (recruitment_id = 4)
    (1, 4, '2025-04-02 12:35:00', '2025-04-02 12:35:00', 'DRUM', 'GOLD'),

    -- 어쿠스틱 소울 멤버 모집 (recruitment_id = 5) - 기타와 보컬 모두 모집
    (1, 5, '2025-04-02 13:05:00', '2025-04-02 13:05:00', 'GUITAR', 'SILVER'),
    (1, 5, '2025-04-02 13:06:00', '2025-04-02 13:06:00', 'VOCAL', 'GOLD'),

    -- 국악 프로젝트 단원 모집 (recruitment_id = 6)
    (2, 6, '2025-04-02 13:35:00', '2025-04-02 13:35:00', 'GUITAR', 'BRONZE'),
    (1, 6, '2025-04-02 13:36:00', '2025-04-02 13:36:00', 'DRUM', 'SILVER');

-- contact 더미 데이터 생성 (각 밴드의 SNS 연락처)
INSERT INTO `contact` (`band_id`, `created_at`, `updated_at`, `title`, `url`, `sns`)
VALUES
    -- 소리꾼들의 연락처
    (@band1_id, '2025-04-01 10:05:00', '2025-04-01 10:05:00', '소리꾼들 인스타그램', 'https://instagram.com/sorikkun',
     'INSTAGRAM'),
    (@band1_id, '2025-04-01 10:06:00', '2025-04-01 10:06:00', '소리꾼들 유튜브', 'https://youtube.com/@sorikkun', 'YOUTUBE'),

    -- 블루노트의 연락처
    (@band2_id, '2025-04-01 10:15:00', '2025-04-01 10:15:00', '블루노트 인스타그램', 'https://instagram.com/bluenote',
     'INSTAGRAM'),
    (@band2_id, '2025-04-01 10:16:00', '2025-04-01 10:16:00', '블루노트 X(트위터)', 'https://x.com/bluenote', 'X'),

    -- 메탈헤드의 연락처
    (@band3_id, '2025-04-01 10:25:00', '2025-04-01 10:25:00', '메탈헤드 인스타그램', 'https://instagram.com/metalhead',
     'INSTAGRAM'),
    (@band3_id, '2025-04-01 10:26:00', '2025-04-01 10:26:00', '메탈헤드 유튜브', 'https://youtube.com/@metalhead', 'YOUTUBE'),
    (@band3_id, '2025-04-01 10:27:00', '2025-04-01 10:27:00', '메탈헤드 웹사이트', 'https://metalhead.kr', 'CUSTOM'),

    -- 어쿠스틱 소울의 연락처
    (@band4_id, '2025-04-01 10:35:00', '2025-04-01 10:35:00', '어쿠스틱 소울 인스타그램', 'https://instagram.com/acousticsoul',
     'INSTAGRAM'),

    -- 국악 프로젝트의 연락처
    (@band5_id, '2025-04-01 10:45:00', '2025-04-01 10:45:00', '국악 프로젝트 유튜브', 'https://youtube.com/@kpproject',
     'YOUTUBE'),
    (@band5_id, '2025-04-01 10:46:00', '2025-04-01 10:46:00', '국악 프로젝트 페이스북', 'https://facebook.com/kpproject',
     'FACEBOOK');

-- 트랜잭션 커밋
COMMIT;


SET SQL_SAFE_UPDATES = 0;

-- 트랜잭션 시작
BEGIN;

-- 새로운 멤버 생성 (새로운 밴드의 밴드마스터가 될 사용자들)
INSERT INTO `member` (`created_at`, `updated_at`, `member_id`, `nickname`, `profile_img_url`, `username`)
VALUES ('2025-04-01 10:00:00', '2025-04-01 10:00:00', UUID_TO_BIN(UUID()), '일렉트로닉',
        'https://storage.dinggading.com/profiles/avatar13.jpg', 'electronic_artist@gmail.com'),
       ('2025-04-01 10:05:00', '2025-04-01 10:05:00', UUID_TO_BIN(UUID()), '힙합비트',
        'https://storage.dinggading.com/profiles/avatar14.jpg', 'hiphop_beats@gmail.com'),
       ('2025-04-01 10:10:00', '2025-04-01 10:10:00', UUID_TO_BIN(UUID()), '블루스맨',
        'https://storage.dinggading.com/profiles/avatar15.jpg', 'blues_man@gmail.com'),
       ('2025-04-01 10:15:00', '2025-04-01 10:15:00', UUID_TO_BIN(UUID()), '포크소울',
        'https://storage.dinggading.com/profiles/avatar16.jpg', 'folk_soul@gmail.com'),
       ('2025-04-01 10:20:00', '2025-04-01 10:20:00', UUID_TO_BIN(UUID()), '레게리듬',
        'https://storage.dinggading.com/profiles/avatar17.jpg', 'reggae_rhythm@gmail.com'),
       ('2025-04-01 10:25:00', '2025-04-01 10:25:00', UUID_TO_BIN(UUID()), '펑크록',
        'https://storage.dinggading.com/profiles/avatar18.jpg', 'punk_rock@gmail.com');

-- 새로운 멤버 UUID 조회
SET @member13_id = (SELECT BIN_TO_UUID(member_id)
                    FROM member
                    WHERE username = 'electronic_artist@gmail.com');
SET @member14_id = (SELECT BIN_TO_UUID(member_id)
                    FROM member
                    WHERE username = 'hiphop_beats@gmail.com');
SET @member15_id = (SELECT BIN_TO_UUID(member_id)
                    FROM member
                    WHERE username = 'blues_man@gmail.com');
SET @member16_id = (SELECT BIN_TO_UUID(member_id)
                    FROM member
                    WHERE username = 'folk_soul@gmail.com');
SET @member17_id = (SELECT BIN_TO_UUID(member_id)
                    FROM member
                    WHERE username = 'reggae_rhythm@gmail.com');
SET @member18_id = (SELECT BIN_TO_UUID(member_id)
                    FROM member
                    WHERE username = 'punk_rock@gmail.com');

-- 추가 밴드 더미 데이터 생성
INSERT INTO `band` (`created_at`, `updated_at`, `job_opening`, `max_size`, `band_master_id`, `description`, `name`,
                    `profile_url`, `sigun`, `tags`)
VALUES ('2025-04-01 11:00:00', '2025-04-01 11:00:00', TRUE, 5, UUID_TO_BIN(@member13_id), '일렉트로닉과 EDM을 기반으로 하는 밴드입니다',
        '일렉트로닉 웨이브',
        'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8ZWxlY3Ryb25pYyUyMG11c2ljfGVufDB8fDB8fHww',
        '서울', 'EDM,일렉트로닉,신스팝'),
       ('2025-04-01 11:10:00', '2025-04-01 11:10:00', TRUE, 6, UUID_TO_BIN(@member14_id), '힙합과 R&B를 결합한 퓨전 밴드입니다',
        '힙합 퓨전',
        'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8aGlwJTIwaG9wfGVufDB8fDB8fHww',
        '경기', '힙합,R&B,퓨전'),
       ('2025-04-01 11:20:00', '2025-04-01 11:20:00', FALSE, 4, UUID_TO_BIN(@member15_id), '클래식 블루스와 모던 블루스를 연주하는 밴드',
        '블루스 트레인',
        'https://images.unsplash.com/photo-1579797990768-555ac3a4c7e5?q=80&w=3174&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
        '부산', '블루스,소울,재즈블루스'),
       ('2025-04-01 11:30:00', '2025-04-01 11:30:00', TRUE, 5, UUID_TO_BIN(@member16_id), '포크와 컨트리 음악을 연주하는 어쿠스틱 밴드',
        '포크 하모니',
        'https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Zm9sayUyMG11c2ljfGVufDB8fDB8fHww',
        '대전', '포크,컨트리,어쿠스틱'),
       ('2025-04-01 11:40:00', '2025-04-01 11:40:00', TRUE, 6, UUID_TO_BIN(@member17_id), '레게와 스카 음악을 연주하는 밴드',
        '레게 비트',
        'https://images.unsplash.com/photo-1561839561-b13bcfe95249?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8cmVnZ2FlfGVufDB8fDB8fHww',
        '광주', '레게,스카,라틴'),
       ('2025-04-01 11:50:00', '2025-04-01 11:50:00', FALSE, 4, UUID_TO_BIN(@member18_id), '펑크와 하드코어를 연주하는 에너지 넘치는 밴드',
        '펑크 레볼루션',
        'https://images.unsplash.com/photo-1598387993248-0addc49abf0d?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTF8fHB1bmslMjByb2NrfGVufDB8fDB8fHww',
        '대구', '펑크,하드코어,얼터너티브');

-- 밴드 ID 조회
SET @band7_id = (SELECT band_id
                 FROM band
                 WHERE name = '일렉트로닉 웨이브');
SET @band8_id = (SELECT band_id
                 FROM band
                 WHERE name = '힙합 퓨전');
SET @band9_id = (SELECT band_id
                 FROM band
                 WHERE name = '블루스 트레인');
SET @band10_id = (SELECT band_id
                  FROM band
                  WHERE name = '포크 하모니');
SET @band11_id = (SELECT band_id
                  FROM band
                  WHERE name = '레게 비트');
SET @band12_id = (SELECT band_id
                  FROM band
                  WHERE name = '펑크 레볼루션');

-- 멤버 ID 조회 (BIN 형식)
SET @member13_id_bin = (SELECT member_id
                        FROM member
                        WHERE username = 'electronic_artist@gmail.com');
SET @member14_id_bin = (SELECT member_id
                        FROM member
                        WHERE username = 'hiphop_beats@gmail.com');
SET @member15_id_bin = (SELECT member_id
                        FROM member
                        WHERE username = 'blues_man@gmail.com');
SET @member16_id_bin = (SELECT member_id
                        FROM member
                        WHERE username = 'folk_soul@gmail.com');
SET @member17_id_bin = (SELECT member_id
                        FROM member
                        WHERE username = 'reggae_rhythm@gmail.com');
SET @member18_id_bin = (SELECT member_id
                        FROM member
                        WHERE username = 'punk_rock@gmail.com');

-- 기존 멤버 ID 조회
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

-- band_member 더미 데이터 생성
INSERT INTO `band_member` (`band_id`, `created_at`, `updated_at`, `member_id`, `instrument`)
VALUES
    -- 일렉트로닉 웨이브 멤버
    (@band7_id, '2025-04-01 11:05:00', '2025-04-01 11:05:00', @member13_id_bin, 'GUITAR'),  -- 일렉트로닉은 밴드마스터이자 기타리스트
    (@band7_id, '2025-04-01 11:06:00', '2025-04-01 11:06:00', @member2_id, 'DRUM'),         -- 드럼왕은 드러머로 참여
    (@band7_id, '2025-04-01 11:07:00', '2025-04-01 11:07:00', @member5_id, 'VOCAL'),        -- 멜로디는 보컬로 참여

    -- 힙합 퓨전 멤버
    (@band8_id, '2025-04-01 11:15:00', '2025-04-01 11:15:00', @member14_id_bin, 'VOCAL'),   -- 힙합비트는 밴드마스터이자 보컬
    (@band8_id, '2025-04-01 11:16:00', '2025-04-01 11:16:00', @member7_id, 'BASS'),         -- 음악대장은 베이스로 참여
    (@band8_id, '2025-04-01 11:17:00', '2025-04-01 11:17:00', @member12_id, 'DRUM'),        -- 리듬킹은 드럼으로 참여

    -- 블루스 트레인 멤버
    (@band9_id, '2025-04-01 11:25:00', '2025-04-01 11:25:00', @member15_id_bin, 'GUITAR'),  -- 블루스맨은 밴드마스터이자 기타리스트
    (@band9_id, '2025-04-01 11:26:00', '2025-04-01 11:26:00', @member3_id, 'BASS'),         -- 베이싱은 베이스로 참여
    (@band9_id, '2025-04-01 11:27:00', '2025-04-01 11:27:00', @member8_id, 'VOCAL'),        -- 재즈맨은 보컬로 참여

    -- 포크 하모니 멤버
    (@band10_id, '2025-04-01 11:35:00', '2025-04-01 11:35:00', @member16_id_bin, 'VOCAL'),  -- 포크소울은 밴드마스터이자 보컬
    (@band10_id, '2025-04-01 11:36:00', '2025-04-01 11:36:00', @member1_id, 'GUITAR'),      -- 기타킹은 기타로 참여

    -- 레게 비트 멤버
    (@band11_id, '2025-04-01 11:45:00', '2025-04-01 11:45:00', @member17_id_bin, 'DRUM'),   -- 레게리듬은 밴드마스터이자 드러머
    (@band11_id, '2025-04-01 11:46:00', '2025-04-01 11:46:00', @member4_id, 'VOCAL'),       -- 보컬여신은 보컬로 참여
    (@band11_id, '2025-04-01 11:47:00', '2025-04-01 11:47:00', @member10_id, 'BASS'),       -- 음악천재는 베이스로 참여

    -- 펑크 레볼루션 멤버
    (@band12_id, '2025-04-01 11:55:00', '2025-04-01 11:55:00', @member18_id_bin, 'GUITAR'), -- 펑크록은 밴드마스터이자 기타리스트
    (@band12_id, '2025-04-01 11:56:00', '2025-04-01 11:56:00', @member6_id, 'VOCAL'),       -- 록스타는 보컬로 참여
    (@band12_id, '2025-04-01 11:57:00', '2025-04-01 11:57:00', @member9_id, 'BASS');
-- 팝스타는 베이스로 참여

-- contact 더미 데이터 생성
INSERT INTO `contact` (`band_id`, `created_at`, `updated_at`, `title`, `url`, `sns`)
VALUES
    -- 일렉트로닉 웨이브 연락처
    (@band7_id, '2025-04-01 11:00:00', '2025-04-01 11:00:00', '일렉트로닉 웨이브 인스타그램',
     'https://instagram.com/electronic_wave',
     'INSTAGRAM'),
    (@band7_id, '2025-04-01 11:01:00', '2025-04-01 11:01:00', '일렉트로닉 웨이브 사운드클라우드',
     'https://soundcloud.com/electronic_wave',
     'CUSTOM'),

    -- 힙합 퓨전 연락처
    (@band8_id, '2025-04-01 11:10:00', '2025-04-01 11:10:00', '힙합 퓨전 인스타그램', 'https://instagram.com/hiphop_fusion',
     'INSTAGRAM'),
    (@band8_id, '2025-04-01 11:11:00', '2025-04-01 11:11:00', '힙합 퓨전 유튜브', 'https://youtube.com/@hiphop_fusion',
     'YOUTUBE'),

    -- 블루스 트레인 연락처
    (@band9_id, '2025-04-01 11:20:00', '2025-04-01 11:20:00', '블루스 트레인 인스타그램', 'https://instagram.com/blues_train',
     'INSTAGRAM'),
    (@band9_id, '2025-04-01 11:21:00', '2025-04-01 11:21:00', '블루스 트레인 페이스북', 'https://facebook.com/blues_train',
     'FACEBOOK'),

    -- 포크 하모니 연락처
    (@band10_id, '2025-04-01 11:30:00', '2025-04-01 11:30:00', '포크 하모니 인스타그램', 'https://instagram.com/folk_harmony',
     'INSTAGRAM'),
    (@band10_id, '2025-04-01 11:31:00', '2025-04-01 11:31:00', '포크 하모니 X(트위터)', 'https://x.com/folk_harmony',
     'X'),

    -- 레게 비트 연락처
    (@band11_id, '2025-04-01 11:40:00', '2025-04-01 11:40:00', '레게 비트 인스타그램', 'https://instagram.com/reggae_beat',
     'INSTAGRAM'),
    (@band11_id, '2025-04-01 11:41:00', '2025-04-01 11:41:00', '레게 비트 유튜브', 'https://youtube.com/@reggae_beat',
     'YOUTUBE'),

    -- 펑크 레볼루션 연락처
    (@band12_id, '2025-04-01 11:50:00', '2025-04-01 11:50:00', '펑크 레볼루션 인스타그램', 'https://instagram.com/punk_revolution',
     'INSTAGRAM'),
    (@band12_id, '2025-04-01 11:51:00', '2025-04-01 11:51:00', '펑크 레볼루션 유튜브', 'https://youtube.com/@punk_revolution',
     'YOUTUBE'),
    (@band12_id, '2025-04-01 11:52:00', '2025-04-01 11:52:00', '펑크 레볼루션 웹사이트', 'https://punk-revolution.kr',
     'CUSTOM');

-- 새로운 멤버들의 memberRank 생성
INSERT INTO `member_rank` (`beat_score`, `rank_success_count`, `tone_score`, `tune_score`, `created_at`,
                           `defence_expire_date`, `last_attempt_date`, `member_rank_id`, `updated_at`, `member_id`,
                           `instrument`, `last_attempt_tier`, `tier`)
VALUES
    -- 일렉트로닉 (멤버13)
    (90, 8, 88, 89, '2025-04-03 21:00:00', '2025-06-03 21:00:00', '2025-04-03 21:00:00', NULL, '2025-04-03 21:00:00',
     @member13_id_bin, 'GUITAR', 'PLATINUM', 'PLATINUM'),
    (80, 5, 78, 79, '2025-04-03 21:10:00', '2025-06-03 21:10:00', '2025-04-03 21:10:00', NULL, '2025-04-03 21:10:00',
     @member13_id_bin, 'BASS', 'GOLD', 'GOLD'),
    (70, 3, 68, 69, '2025-04-03 21:20:00', '2025-06-03 21:20:00', '2025-04-03 21:20:00', NULL, '2025-04-03 21:20:00',
     @member13_id_bin, 'DRUM', 'SILVER', 'SILVER'),
    (60, 1, 58, 59, '2025-04-03 21:30:00', '2025-06-03 21:30:00', '2025-04-03 21:30:00', NULL, '2025-04-03 21:30:00',
     @member13_id_bin, 'VOCAL', 'BRONZE', 'BRONZE'),

    -- 힙합비트 (멤버14)
    (70, 3, 72, 71, '2025-04-03 22:00:00', '2025-06-03 22:00:00', '2025-04-03 22:00:00', NULL, '2025-04-03 22:00:00',
     @member14_id_bin, 'GUITAR', 'SILVER', 'SILVER'),
    (75, 4, 77, 76, '2025-04-03 22:10:00', '2025-06-03 22:10:00', '2025-04-03 22:10:00', NULL, '2025-04-03 22:10:00',
     @member14_id_bin, 'DRUM', 'SILVER', 'SILVER'),
    (80, 5, 82, 81, '2025-04-03 22:20:00', '2025-06-03 22:20:00', '2025-04-03 22:20:00', NULL, '2025-04-03 22:20:00',
     @member14_id_bin, 'BASS', 'GOLD', 'GOLD'),
    (95, 10, 97, 96, '2025-04-03 22:30:00', '2025-06-03 22:30:00', '2025-04-03 22:30:00', NULL, '2025-04-03 22:30:00',
     @member14_id_bin, 'VOCAL', 'DIAMOND', 'DIAMOND'),

    -- 블루스맨 (멤버15)
    (93, 9, 92, 93, '2025-04-03 23:00:00', '2025-06-03 23:00:00', '2025-04-03 23:00:00', NULL, '2025-04-03 23:00:00',
     @member15_id_bin, 'GUITAR', 'PLATINUM', 'PLATINUM'),
    (78, 4, 77, 78, '2025-04-03 23:10:00', '2025-06-03 23:10:00', '2025-04-03 23:10:00', NULL, '2025-04-03 23:10:00',
     @member15_id_bin, 'DRUM', 'SILVER', 'SILVER'),
    (82, 6, 81, 82, '2025-04-03 23:20:00', '2025-06-03 23:20:00', '2025-04-03 23:20:00', NULL, '2025-04-03 23:20:00',
     @member15_id_bin, 'BASS', 'GOLD', 'GOLD'),
    (88, 7, 90, 89, '2025-04-03 23:30:00', '2025-06-03 23:30:00', '2025-04-03 23:30:00', NULL, '2025-04-03 23:30:00',
     @member15_id_bin, 'VOCAL', 'PLATINUM', 'PLATINUM'),

    -- 포크소울 (멤버16)
    (85, 6, 84, 85, '2025-04-04 00:00:00', '2025-06-04 00:00:00', '2025-04-04 00:00:00', NULL, '2025-04-04 00:00:00',
     @member16_id_bin, 'GUITAR', 'GOLD', 'GOLD'),
    (65, 2, 64, 65, '2025-04-04 00:10:00', '2025-06-04 00:10:00', '2025-04-04 00:10:00', NULL, '2025-04-04 00:10:00',
     @member16_id_bin, 'DRUM', 'BRONZE', 'BRONZE'),
    (70, 3, 69, 70, '2025-04-04 00:20:00', '2025-06-04 00:20:00', '2025-04-04 00:20:00', NULL, '2025-04-04 00:20:00',
     @member16_id_bin, 'BASS', 'SILVER', 'SILVER'),
    (92, 9, 94, 93, '2025-04-04 00:30:00', '2025-06-04 00:30:00', '2025-04-04 00:30:00', NULL, '2025-04-04 00:30:00',
     @member16_id_bin, 'VOCAL', 'PLATINUM', 'PLATINUM'),

    -- 레게리듬 (멤버17)
    (78, 5, 77, 78, '2025-04-04 01:00:00', '2025-06-04 01:00:00', '2025-04-04 01:00:00', NULL, '2025-04-04 01:00:00',
     @member17_id_bin, 'GUITAR', 'SILVER', 'SILVER'),
    (92, 9, 91, 92, '2025-04-04 01:10:00', '2025-06-04 01:10:00', '2025-04-04 01:10:00', NULL, '2025-04-04 01:10:00',
     @member17_id_bin, 'DRUM', 'PLATINUM', 'PLATINUM'),
    (80, 6, 79, 80, '2025-04-04 01:20:00', '2025-06-04 01:20:00', '2025-04-04 01:20:00', NULL, '2025-04-04 01:20:00',
     @member17_id_bin, 'BASS', 'PLATINUM', 'PLATINUM'),
    (92, 9, 94, 93, '2025-04-04 00:30:00', '2025-06-04 00:30:00', '2025-04-04 00:30:00', NULL, '2025-04-04 00:30:00',
     @member17_id_bin, 'VOCAL', 'PLATINUM', 'PLATINUM');

COMMIT;