# Dynadot API Watch

This directory stores the normalized baseline used to detect changes in the Dynadot API documentation.

The Woodpecker pipeline `.woodpecker/dynadot-api-watch.yml` runs on the `dynadot-api-watch` cron event and on manual runs. It fetches the current Dynadot API documentation, normalizes it, compares it with `latest.normalized`, and then runs the unit test suite.

## Configure Woodpecker

Create a Woodpecker cron named `dynadot-api-watch`. A daily or twice-daily schedule is enough while the Dynadot API v2.0.0 remains in beta.

Optional environment variables:

- `DYNADOT_API_SPEC_URL`: source URL to monitor. Defaults to `https://www.dynadot.com/domain/api-document?getCommandInfoData=1&apiVersion=2.0.0`.
- `DYNADOT_API_BASELINE`: baseline path. Defaults to `docs/dynadot-api/latest.normalized`.
- `DYNADOT_API_WORK_DIR`: output directory for the latest fetched artifacts. Defaults to `target/dynadot-api-watch`.

## Update The Baseline

When the pipeline detects a change, inspect the diff, update the SDK if needed, and then refresh the accepted baseline locally:

```bash
scripts/watch-dynadot-api.sh --update-baseline
```

Commit the updated `docs/dynadot-api/latest.normalized` together with any SDK changes.

If Dynadot publishes a stable OpenAPI endpoint, set `DYNADOT_API_SPEC_URL` to that URL. The script automatically sorts JSON with `jq` or Python when the fetched document is JSON.
