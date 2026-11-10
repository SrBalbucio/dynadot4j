#!/usr/bin/env python3
"""Normalize Dynadot api-document HTML (Nuxt SPA) into stable text.
Removes volatile bundle hashes, UUIDs, timestamps so diff only fires on real doc changes.
Usage: normalize-dynadot-html.py <raw.html> <out.normalized>
"""
import re
import sys
import html


def main():
    if len(sys.argv) != 3:
        print(f"Usage: {sys.argv[0]} <raw.html> <out.normalized>", file=sys.stderr)
        sys.exit(2)
    raw_path, out_path = sys.argv[1], sys.argv[2]
    raw = open(raw_path, encoding="utf-8", errors="replace").read()
    # title
    title_m = re.search(r"<title[^>]*>(.*?)</title>", raw, flags=re.I | re.S)
    title = ""
    if title_m:
        title = re.sub(r"\s+", " ", html.unescape(re.sub(r"<[^>]+>", " ", title_m.group(1)))).strip()
    # drop scripts/styles/comments (volatile Nuxt bundles)
    body = re.sub(r"<script\b[^>]*>.*?</script>", " ", raw, flags=re.I | re.S)
    body = re.sub(r"<style\b[^>]*>.*?</style>", " ", body, flags=re.I | re.S)
    body = re.sub(r"<!--.*?-->", " ", body, flags=re.S)
    # prefer <main>, fallback <body>
    m = re.search(r"<main\b[^>]*>(.*?)</main>", body, flags=re.I | re.S)
    if m:
        frag = m.group(1)
    else:
        b = re.search(r"<body\b[^>]*>(.*?)</body>", body, flags=re.I | re.S)
        frag = b.group(1) if b else body
    text = re.sub(r"<[^>]+>", "\n", frag)
    text = html.unescape(text)
    lines = []
    if title:
        lines.append("TITLE: " + title)
    for line in text.splitlines():
        line = re.sub(r"\s+", " ", line).strip()
        if not line:
            continue
        # volatile normalizations
        line = re.sub(r"/web/assets/[^\s'\"<>]+", "/web/assets/<asset>", line)
        line = re.sub(r"\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\b", "<uuid>", line, flags=re.I)
        line = re.sub(r"\bv=[0-9a-f-]+", "v=<ver>", line, flags=re.I)
        line = re.sub(r"\b\d{13}\b", "0", line)
        line = re.sub(r"\b(19|20)\d{2}[-/]\d{2}[-/]\d{2}\b", "<date>", line)
        lines.append(line)
    # dedupe consecutive duplicates (Nuxt SSR hydration repeats), keep order
    dedup = []
    prev = None
    for ln in lines:
        if ln != prev:
            dedup.append(ln)
        prev = ln
    open(out_path, "w", encoding="utf-8", newline="\n").write("\n".join(dedup) + "\n")


if __name__ == "__main__":
    main()
