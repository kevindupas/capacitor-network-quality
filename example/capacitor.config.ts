import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'dev.kevindupas.capacitor.networkquality.example',
  appName: 'NetworkQuality Example',
  webDir: 'www',
  server: {
    androidScheme: 'https'
  }
};

export default config;
