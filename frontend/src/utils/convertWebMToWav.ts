import { encodeWAV } from './encodeWAV';

export async function convertWebMToWav(blob: Blob): Promise<Blob> {
  const arrayBuffer = await blob.arrayBuffer();
  const audioContext = new AudioContext();
  const audioBuffer = await audioContext.decodeAudioData(arrayBuffer);

  const wavBuffer = encodeWAV(audioBuffer);
  return new Blob([wavBuffer], { type: 'audio/wav' });
}
