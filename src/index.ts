import { registerPlugin } from '@capacitor/core';

import type { NetworkQualityPlugin } from './definitions';

const NetworkQuality = registerPlugin<NetworkQualityPlugin>('NetworkQuality', {
  web: () => import('./web').then(m => new m.NetworkQualityWeb()),
});

export * from './definitions';
export { NetworkQuality };
