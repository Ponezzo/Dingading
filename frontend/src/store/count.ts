// src/store/count.ts
import { create } from 'zustand';

// count 상태를 관리하는 store
interface CountState {
  count: number;
  increment: () => void;
  decrement: () => void;
  reset: () => void;
}

export const useCountStore = create<CountState>((set) => ({
  count: 0,  // 초기 count 값은 0
  increment: () => set((state) => ({ count: state.count + 1 })),  // count를 1 증가시키는 함수
  decrement: () => set((state) => ({ count: state.count - 1 })),  // count를 1 감소시키는 함수
  reset: () => set({ count: 0 }),  // count를 0으로 리셋하는 함수
}));
