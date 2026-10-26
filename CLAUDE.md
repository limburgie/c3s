# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this project is

C3S is a Spring Boot website builder that serves CMS-driven sites without requiring the CMS to run on the host. 
A request comes in, C3S looks up the site by virtual host, loads its structure/templates from an asset store, 
fetches content in the site from a content repository, and renders templates server-side using a template engine. 
There is no separate frontend — the application *is* the public site for every site it hosts.

Full project documentation lives at https://github.com/limburgie/c3s/wiki.

## Build / run

- Build: `./mvnw clean package` (or `mvn clean package`). The `openapi-generator-maven-plugin` runs during `generate-sources`
  and writes Paginea client code to `target/generated-sources/openapi` from `src/main/resources/paginea.json` — 
  `mvn clean` removes it, so a fresh build regenerates.
- Run locally: `mvn spring-boot:run` or `java -jar target/c3s.jar`. The default profile uses `c3s.registry.type=LOCAL` 
  from `application.properties`; the developer-specific profile is activated via `-Dspring.profiles.active=peter` and 
  pulls config from `application-peter.properties` (gitignored, contains DB credentials and reCAPTCHA secret).

## Architecture

### Strategy-pattern plugin model

Three pluggable abstractions share the same shape: an interface, multiple `@Service` implementations, an enum identifying 
each implementation, and a `*Factory` that injects `List<Interface>` and picks the right one by enum value. When adding 
a new backend, follow this pattern — Spring auto-discovery does the wiring.

| Abstraction                                       | Selector enum                | Implementations                                                                                       |
|---------------------------------------------------|------------------------------|-------------------------------------------------------------------------------------------------------|
| `SiteConnectionRegistry` (siteconnectionregistry) | `SiteConnectionRegistryType` | `LocalSiteConnectionRegistry` (properties), `DatabaseSiteConnectionRegistry` (JPA/MySQL)              |
| `SiteAssetStore` (siteassetstore)                 | `SiteAssetStoreType`         | `WebserverSiteAssetStore` (HTTP or local files), `S3SiteAssetStore` (S3)                              |
| `ContentRepository` (contentrepository)           | `ContentRepositoryType`      | `ContentfulContentService`, `PrismicContentService`, `MockContentService`, `PagineaContentRepository` |
| `TemplateParser` (templateparser)                 | `TemplateEngine`             | `FreemarkerTemplateParser`                                                                            |

### Request flow

`PageController` (controller/PageController.java) renders pages and handles every URL not claimed by a more specific controller via a catch-all mapping.
E.g., suppose we need to render the page at https://www.example.com/hello and:
- The site connection registry is configured using local properties.
- The site's config, pages, and assets are located in an S3 bucket.
- The site's content is stored in a Prismic.io content repository.
- The site's templates are stored in a local file system.

The steps are then:

1. C3S first looks up the `SiteConnection` in the `LocalSiteConnectionRegistry` that matches the `www.example.com` virtual host.
2. By looking at the `SiteConnection` it sees that the site is configured to use the `S3SiteAssetStore`.
3. It parses the `c3s.json` in the root of the bucket and resolves the site's configuration and pages.
4. It reads the page that matches the `/hello` friendly URL and feeds it to the `FreemarkerTemplateParser`.
5. `c3s.json` also configured `PrismicContentRepository` as the content repository, so it fetches any content references through that repository.
6. When everything is parsed, `PageController` renders the resulting page.

Special routes handled before the catch-all:
- `AssetController`: `/assets/**` (static assets, SASS compiled on the fly).
- `FormController`: `/submit` (form POST).
- `SeoController`: `/favicon.{ico,svg}`, `/sitemap.xml` and `/robots.txt` (the site's own `robots.txt` from its asset 
  store root if present, otherwise a default pointing to the sitemap). Pages with a `sitemap` block in `c3s.json` also
  list their content-driven detail pages, so the sitemap queries the content repository once per locale.
- `PageController`: `/c3s/{contentItemId}` (redirect to CMS edit URL).
- `CachePurgeController`: `POST /cache/assets/purge` and `POST /cache/pages/purge` (webhooks, see Caching).

Every controller resolves the requested site's asset store through `SiteAssetStoreResolver` (by virtual host).

### Caching

Two in-memory Caffeine caches, local to the pod (C3S runs as a single replica, so purges reach every cache):
- `AssetCache`: raw site assets (`c3s.json`, templates, static files, compiled SASS), keyed by repository id + path. 
  Properties `c3s.assets.cache.{enabled,max-entries,webhook.secret}`.
- `PageCache`: final rendered HTML of `PageController` page renders, keyed by `PageCacheKey` (repository id, locale 
  context, path without locale prefix; the query string is ignored). Only successful renders are cached, and requests 
  with a non-empty shopping cart bypass the cache. Entries live until purged, unless the page sets `cacheTtl` (seconds) 
  in `c3s.json`; `0` disables caching for that page. Properties `c3s.pages.cache.{enabled,max-entries,webhook.secret}`.
  `/sitemap.xml` is cached here too, keyed by its absolute URL (several hosts can serve one site), with a 1-day TTL 
  for date-filtered content; a sitemap whose content queries failed is served but not cached.

Both are purged per site through `CachePurgeController`, which resolves the site from the request's virtual host and 
requires `Authorization: Bearer <secret>` with the cache's own secret. Purging assets also purges pages (pages are 
derived from assets); purging pages leaves assets intact. Site deploy workflows call the asset purge; the CMS calls the 
page purge on content edits. Both caches are disabled in the developer profile so template edits show immediately.

### Request-scoped state via ThreadLocals

Per-request state is propagated through `ThreadLocal`s rather than method parameters: `LocationThreadLocal` (locale + timezone), 
`RequestUriThreadLocal` (current URI without locale prefix), `ShoppingCartThreadLocal`. Code reading these assumes the controller 
handling the request (`PageController`, `FormController`) has set them earlier in the request — preserve that ordering when adding new entry points.

### Content API abstraction

`ContentApi` + `QueryBuilder` (content/service/domain/) are a backend-agnostic content query language exposed as `api` 
in templates. Each backend implementation translates calls into native CMS queries. When changing this interface, every 
`*ContentApi`/`*QueryBuilder` must be updated in lockstep — there is no shared base class.