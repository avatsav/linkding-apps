# Linkding Apps

Unofficial Android and iOS app for [Linkding](https://github.com/sissbruecker/linkding) self-hosted bookmarking service.

> [!WARNING]
> This is work-in-progress.

## Motivation

I developed this app for personal use, aiming to integrate with my Linkding instance while also serving as a playground for exploring architecture, Gradle, diverse libraries, design patterns, API design, and more.

This project owes its multiplatform foundation to the excellent [Tivi](https://github.com/chrisbanes/tivi) project.

## Colophon

- Kotlin + Compose Multiplatform
- [Navigation 3](https://developer.android.com/develop/ui/compose/navigation): Type-safe Compose navigation
- [Molecule](https://github.com/cashapp/molecule): Composable-based Presenters
- [Metro](https://github.com/ZacSweers/metro): Dependency Injection
- Icons generated with [Icon Kitchen](https://icon.kitchen)

## Local Linkding server

Use the development server for manual testing without touching your real library:

```bash
cp .env.example .env
# Generate a test token once:
python3 -c 'import secrets; print(secrets.token_hex(20))'
# Save it as LINKDING_TEST_API_TOKEN in .env and set a throwaway password.
docker compose up -d --wait
```

Compose provisions the admin account and the configured API token before starting
Linkding. Use `LINKDING_TEST_API_TOKEN` from `.env` in the app; there is no manual
token-creation step in the web UI. The token stays the same across restarts and
fresh data volumes as long as you keep the same `.env` value. Changing it and
running `docker compose up -d --wait` replaces the managed development token;
other API tokens are left alone.

The web UI is at <http://localhost:9090>. Use these server URLs in the app:

| Client | Server URL |
| --- | --- |
| Android emulator, debug build | `http://10.0.2.2:9090/` |
| Desktop app | `http://localhost:9090/` |
| Android device over USB | Run `adb reverse tcp:9090 tcp:9090`, then use `http://127.0.0.1:9090/` |

The container only publishes on the host's loopback interface. Android's HTTP
exceptions apply only to debug builds and these local hosts; release builds keep
their existing network policy. This setup is for local development, not production.
Other clients/builds that require HTTPS need an HTTPS endpoint.

To test pending uploads, authenticate while the server is running, then stop it:

```bash
docker compose stop linkding
```

Save a few new bookmarks and edit a pending item. Start the server again with
`docker compose start linkding`, keep the app in the foreground (or tap Retry),
and check that uploaded items move from Pending to the top of All bookmarks
without a manual refresh.

`docker compose down` removes the container but retains its named data volume.
**`docker compose down --volumes` deletes the development library and account.**
`.env` is ignored by Git. `LINKDING_PORT` can change the host port, and
`LINKDING_IMAGE` can pin a server version; both are shown in `.env.example`.

## License

```
MIT License

Copyright (c) 2025 Abhijith Srivatsav

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
