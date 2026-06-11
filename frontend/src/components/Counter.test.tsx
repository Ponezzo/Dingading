import { render, screen, fireEvent } from '@testing-library/react';
import Counter from './Counter';

test('초기 카운트는 0이어야 한다.', () => {
  render(<Counter />);
  const counterValue = screen.getByTestId('counter-value');
  expect(counterValue).toHaveTextContent('Count: 0');
});

test('버튼을 클릭하면 카운트가 증가해야 한다.', () => {
  render(<Counter />);
  const button = screen.getByText('Increment');
  const counterValue = screen.getByTestId('counter-value');

  fireEvent.click(button);
  expect(counterValue).toHaveTextContent('Count: 1');
});
