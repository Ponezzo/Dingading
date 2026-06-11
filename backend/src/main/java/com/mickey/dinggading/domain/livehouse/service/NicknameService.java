package com.mickey.dinggading.domain.livehouse.service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class NicknameService {

    private static final List<String> ADJECTIVES = Arrays.asList(
            "감탄하는", "격렬한", "고요한", "귀여운", "깜찍한", "따뜻한", "멋진",
            "모험적인", "발랄한", "빛나는", "사랑스러운", "상쾌한", "신나는", "열정적인",
            "용감한", "유쾌한", "자유로운", "즐거운", "친절한", "특별한", "행복한", "화려한"
    );

    private static final List<String> ANIMALS = Arrays.asList(
            "강아지", "고양이", "곰", "기린", "다람쥐", "돌고래", "독수리", "사자",
            "여우", "오리", "원숭이", "쥐", "치타", "캥거루", "코끼리", "토끼", "팬더", "펭귄"
    );

    private static final Random RANDOM = new Random();

    /**
     * 랜덤 닉네임을 생성합니다. (형식: 형용사 + 동물 + 숫자)
     */
    public String generateRandomNickname() {
        String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
        String animal = ANIMALS.get(RANDOM.nextInt(ANIMALS.size()));
        int number = RANDOM.nextInt(1000);

        return adjective + " " + animal + number;
    }
}