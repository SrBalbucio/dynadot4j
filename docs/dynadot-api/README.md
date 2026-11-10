# Dynadot API Watch

This directory stores the normalized baselines used to detect changes in the Dynadot API documentation.

The Woodpecker pipeline `.woodpecker/dynadot-api-watch.yml` runs on the `dynadot-api-watch` cron event and on manual runs. It fetches the current Dynadot API documentation, normalizes it, compares it with the tracked baseline, and then runs the unit test suite.

## Two Baselines (JSON and HTML)

Dynadot has served the monitored URL both as machine-readable JSON and, more recently, as an HTML page (a Nuxt SPA that loads the command data client-side). The watcher detects the payload format on every run and compares against the matching baseline:

| Payload | Baseline | Contents |
| --- | --- | --- |
| JSON | `latest.normalized` | Full machine-readable command contract (`commonCommand`, parameters, response demos, error lists). |
| HTML | `latest.normalized.html` | Normalized text of the documentation page (general docs, auth, rate limits, webhooks, sandbox, change log). |

The first line of every normalized file records the source format (`SOURCE: json` / `SOURCE: html`), so a format switch shows up as a single line rather than a spurious full-file change.

The HTML normalization (`scripts/normalize-dynadot-html.py`) strips Nuxt bundles, asset hashes, UUIDs, and timestamps so the diff only fires on real documentation changes. It prefers the `<main>` region, which excludes navigation, promo banners, and the footer.

> The HTML page does **not** include the per-command contract. When the source is HTML, the watcher can detect changes to the general documentation and change log, but it cannot validate the command schemas. Keep `latest.normalized` (JSON) as the contract baseline and refresh it only when a JSON source is available.

## Configure Woodpecker

Create a Woodpecker cron named `dynadot-api-watch`. A daily or twice-daily schedule is enough while the Dynadot API v2.0.0 remains in beta.

Optional environment variables:

- `DYNADOT_API_SPEC_URL`: source URL to monitor. Defaults to `https://www.dynadot.com/domain/api-document?getCommandInfoData=1&apiVersion=2.0.0`.
- `DYNADOT_API_BASELINE`: JSON baseline path. Defaults to `docs/dynadot-api/latest.normalized`.
- `DYNADOT_API_BASELINE_HTML`: HTML baseline path. Defaults to `docs/dynadot-api/latest.normalized.html`.
- `DYNADOT_API_WORK_DIR`: output directory for the latest fetched artifacts. Defaults to `target/dynadot-api-watch`.

Optional Woodpecker secret:

- `discord_webhook_url`: Discord webhook URL used by the notification steps.

The pipeline sends a Discord message when the watch passes and another when the watch or tests fail.

## Update The Baseline

When the pipeline detects a change, inspect the diff, update the SDK if needed, and then refresh the accepted baseline locally:

```bash
scripts/watch-dynadot-api.sh --update-baseline
```

The command refreshes the baseline for the payload format that was actually fetched. Commit the updated baseline (`docs/dynadot-api/latest.normalized` or `docs/dynadot-api/latest.normalized.html`) together with any SDK changes.

## Exit Codes

- `0`: the fetched documentation matches the tracked baseline.
- `1`: the documentation changed; review the diff before updating the baseline.
- `2`: the watcher could not run (missing command, baseline absent, unusable payload).

If Dynadot publishes a stable OpenAPI endpoint, set `DYNADOT_API_SPEC_URL` to that URL. The script automatically sorts JSON with `jq` (or Python) when the fetched document is JSON.
