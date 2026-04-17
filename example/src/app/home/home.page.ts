import { JsonPipe } from '@angular/common';
import { Component, signal } from '@angular/core';
import { IonHeader, IonToolbar, IonTitle, IonContent, IonButton, IonList, IonItem, IonLabel, IonTextarea } from '@ionic/angular/standalone';
import { NetworkQuality, NetworkInfo, NetworkType } from '@kevindupas/capacitor-network-quality';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
  standalone: true,
  imports: [IonTextarea, IonButton, IonHeader, IonToolbar, IonTitle, IonContent, JsonPipe],
})
export class HomePage {
  info = signal<NetworkInfo | null>(null);

  networkType = signal<{ type: NetworkType } | null>(null);

  constructor() { }

  async getInfo() {
    const info = await NetworkQuality.getInfo();
    this.info.set(info);
  }

  async getNetworkType() {
    const networkType = await NetworkQuality.getNetworkType({ withBasicPermission: true });
    this.networkType.set(networkType);
  }
}
