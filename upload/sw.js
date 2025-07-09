const CACHE_NAME = 'peek-and-pick-cache-v1';

const STATIC_ASSETS = [
    '/offline.html',
    '/manifest.json',
    '/favicon.ico',
    '/app/app.css',
    '/icons/icon-192.png',
    '/icons/icon-512.png',
    '/icons/icon_clean.png',
    '/icons/main_barcode.png',
    '/icons/main_chatbot.png',
    '/icons/main_event.png',
    '/icons/main_favorite.png',
    '/icons/main_map.png',
    '/icons/main_ranking.png',
    '/icons/map_directions_marker.png',
    '/icons/map_shop.png',
    '/carousel/best_photo_review.png',
    '/carousel/bingsu.png',
    '/carousel/invitation.png',
    '/carousel/review_event.png',
    '/carousel/summer_event.png',
    '/default.png',
    '/',
    '/login',
    '/signup',
    '/main',
    '/barcode/scan',
    '/products/search'
];

// ✅ Install: Pre-cache static assets
self.addEventListener('install', (event) => {
    event.waitUntil(
        (async () => {
            const cache = await caches.open(CACHE_NAME);
            for (const asset of STATIC_ASSETS) {
                try {
                    await cache.add(asset);
                } catch (e) {
                    console.warn(`❌ Failed to cache: ${asset}`, e);
                }
            }
        })()
    );
    self.skipWaiting();
});

// ✅ Activate: Delete old caches
self.addEventListener('activate', (event) => {
    event.waitUntil(
        caches.keys().then((keys) =>
            Promise.all(keys.filter((key) => key !== CACHE_NAME).map((key) => caches.delete(key)))
        )
    );
    self.clients.claim();
});

// ✅ Fetch Strategy
self.addEventListener('fetch', (event) => {
    const request = event.request;
    const url = new URL(request.url);

    if (url.protocol.startsWith('ws') || request.method !== 'GET') return;

    const isSameOrigin = url.origin === self.location.origin;
    const acceptHeader = request.headers.get('accept') || '';

    // JSON 요청 여부 판단 보완 (application/json 혹은 API 경로 포함)
    const isJsonRequest =
        acceptHeader.includes('application/json') ||
        url.pathname.startsWith('/api/') ||
        url.pathname.startsWith('/_data') || // Remix 데이터 경로 등
        url.pathname.startsWith('/resources') ||
        url.pathname.startsWith('/__remix') ||
        url.pathname.includes('?_data');

    const isHTMLRequest =
        request.mode === 'navigate' ||
        acceptHeader.includes('text/html');

    const isImageRequest = request.destination === 'image';

    // 정적 자산 매칭 (경로 완전 일치 혹은 엄격 체크 권장)
    const isStaticAsset = STATIC_ASSETS.some((path) => {
        if (path === '/') return url.pathname === '/';
        return url.pathname.startsWith(path);
    });

    // 백엔드 API (네트워크만 처리, 캐시 제외)
    const isBackendAPI = url.origin === 'http://localhost:8080';

    if (isBackendAPI) return;

    // 1. JSON 요청 (API, Remix 내부 데이터) → 네트워크 우선, 실패 시 JSON fallback
    if (isJsonRequest) {
        event.respondWith(
            fetch(request).catch(() =>
                new Response(JSON.stringify({ error: 'offline' }), {
                    status: 503,
                    headers: { 'Content-Type': 'application/json' },
                })
            )
        );
        return;
    }

    // 2. 정적 자산 → 캐시 우선 전략
    if (isStaticAsset) {
        event.respondWith(
            caches.match(request).then((cached) => {
                if (cached) return cached;
                return fetch(request)
                    .then(async (res) => {
                        const cache = await caches.open(CACHE_NAME);
                        cache.put(request, res.clone());
                        return res;
                    })
                    .catch(() => {
                        if (isImageRequest) return caches.match('/default.png');
                        return caches.match('/offline.html');
                    });
            })
        );
        return;
    }

    // 3. HTML 페이지 → 네트워크 우선, 실패 시 캐시된 offline.html 혹은 요청된 페이지 캐시
    if (isHTMLRequest) {
        event.respondWith(
            fetch(request)
                .then(async (res) => {
                    const cache = await caches.open(CACHE_NAME);
                    cache.put(request, res.clone());
                    return res;
                })
                .catch(async () => {
                    const cache = await caches.open(CACHE_NAME);
                    return (await cache.match(request)) || (await cache.match('/offline.html'));
                })
        );
        return;
    }

    // 4. 기타 요청 → 네트워크 우선, 실패 시 이미지/HTML 캐시 fallback 또는 빈 응답
    event.respondWith(
        (async () => {
            try {
                return await fetch(request);
            } catch {
                if (isImageRequest) return caches.match('/default.png');
                if (isHTMLRequest) return caches.match('/offline.html');
                return new Response('', { status: 200 });
            }
        })()
    );
});

// ✅ Push Notifications
self.addEventListener('push', (event) => {
    const data = event.data?.json().notification || {};
    const title = data.title || 'Peek&Pick';
    const options = {
        body: data.body || '새로운 알림이 있습니다.',
        icon: '/icons/icon-192.png',
        badge: '/icons/icon-192.png',
        data: {
            url: data.url || '/',
        },
    };
    event.waitUntil(self.registration.showNotification(title, options));
});

// ✅ Notification Click
self.addEventListener('notificationclick', (event) => {
    event.notification.close();
    event.waitUntil(
        clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clientList) => {
            for (const client of clientList) {
                if (client.url === event.notification.data.url && 'focus' in client) {
                    return client.focus();
                }
            }
            if (clients.openWindow) {
                return clients.openWindow(event.notification.data.url);
            }
        })
    );
});