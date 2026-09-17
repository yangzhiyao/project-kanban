import { ApplicationConfig, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient } from '@angular/common/http';
import { DeleteOutline, EditOutline, PlusOutline, ReloadOutline, SearchOutline } from '@ant-design/icons-angular/icons';
import { provideNzNativeDateAdapter } from 'ng-zorro-antd/core/time';
import { provideNzIcons } from 'ng-zorro-antd/icon';
import { provideNzI18n, zh_CN } from 'ng-zorro-antd/i18n';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideAnimationsAsync(),
    provideHttpClient(),
    provideNzI18n(zh_CN),
    // nz-date-picker 必须声明日期适配器，这里使用基于原生 Date + Intl 的实现
    provideNzNativeDateAdapter({ locale: 'zh-CN' }),
    provideNzIcons([SearchOutline, ReloadOutline, PlusOutline, EditOutline, DeleteOutline])
  ]
};
